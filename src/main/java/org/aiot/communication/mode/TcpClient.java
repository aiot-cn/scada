package org.aiot.communication.mode;

import org.aiot.communication.CommunicationInfc;
import org.aiot.lang.Command;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.enums.CdataEnum;
import org.aiot.model.table.DeviceCommand;
import org.aiot.util.CalcUtil;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;


import java.io.*;
import java.net.*;
import java.util.Optional;

@AoReflect("TCPClient")
public class TcpClient extends CommunicationInfc {
	Log log = Logs.get();
	protected Socket socket;
	protected InputStream is = null;
	protected OutputStream os = null;
	private BufferedReader reader = null;
	protected String HOST = "127.0.0.1";
	protected int PORT = 8000;

	@AoReflect(value = "监听间隔",type = AstEnum.param)
	private int listenInterval = 10;

	@AoReflect(value = "心跳包",type = AstEnum.param)
	private String heartbeat;

	@AoReflect(value = "行读取",type = AstEnum.param)
	private boolean isReadLine;

	private boolean isListen;

	@Override
	public boolean open() {
		if(socket == null || !socket.isConnected()){
			if(commun.getIsRemoved() != 0)
				return false;
			String uri = commun.getUri();
			if(Strings.isNotBlank(uri)){
				String[] s = uri.split(":");
				HOST = s[0];
				if(s.length > 1)
					PORT = Integer.parseInt(s[1]);
			}
			SocketAddress addr = new InetSocketAddress(HOST,PORT);
			socket = new Socket();//默认阻塞模式;
			try {
				socket.connect(addr,2000);//连接超时
				//socket.setKeepAlive(true);//开启保持活动状态的套接字,一般2小时探测一次，没卵用
				socket.setSoTimeout(1000);//读取数据超时
				is=socket.getInputStream(); //接受服务端消息并打印
				os=socket.getOutputStream(); //给服务端发送响应信息
				log.infof("TCPClient已连接到 %s:%d",HOST,PORT);
			} catch(IOException e){
				//TODO 这里应该抛出异常
				sendSocket(CdataEnum.OTS,"error %s:%d连接失败 %s",HOST,PORT,e.getMessage());
				return false;
			}
		}
		return socket.isConnected() && !socket.isClosed();
	}

	public void send(byte[] b){
		try {
			os.write(b);
			os.flush();
		} catch (SocketException e){
			log.errorf("TCPClient %s:%d 发送失败(%s),将关闭Socket",HOST,PORT,e.getMessage());
			closeSocket();
		}catch (IOException e) {
			e.printStackTrace();
			closeSocket();
		}
	}

	@Override
	public boolean close() {
		isListen = false;
		deleteObservers();
		closeSocket();
		return true;
	}



	public void closeSocket() {
		try {
			if(socket != null && socket.isConnected()){
				socket.shutdownInput();
				socket.shutdownOutput();
				
				if(!socket.isClosed())
					socket.close();
				log.warnf( "TCPClient已关闭 %s:%s",HOST,PORT);
			}

			if(reader != null){
				reader.close();
			}else if(is != null){
				is.close();
			}

			if(os != null)
				os.close();

		} catch (IOException e) {
			log.errorf("TCPClient关闭异常 %s:%d %s",HOST,PORT,e.getMessage());
		}finally {
			os = null;
			is = null;
			socket = null;
			reader = null;
		}

	}


	@Override
	public synchronized byte[] send(Command command) {
		byte[] b = command.getDataToSend();
		try{
			if(this.open()){
				cleanInput();
				send(b);
			}

			if(commun.isListen())
				return null;

			DeviceCommand dc = command.getDeviceCommand();
			int timeout = Optional.ofNullable(dc.getTimeout()).orElse(15);
			//响应时间
			int responseTime = Optional.ofNullable(dc.getResponseTime()).orElse(1000);
			long t1 = System.currentTimeMillis();
			while(System.currentTimeMillis() - t1 < responseTime && is != null && is.available() == 0){
				Lang.sleep(10);//控制CPU占用
			}

			if(is == null || is.available() == 0){
				return null;
			}
			byte[] r = new byte[0];
			do{
				byte[] rb = new byte[is.available()];
				is.read(rb);
				r = CalcUtil.byteJoin(r,rb);
				Lang.sleep(timeout);
			}while (is.available()>0);
			//log.info("耗时:"+(System.currentTimeMillis()-t1)+"ms");
            return r;
		}catch (Exception e) {
			log.errorf("%s:%d %s",HOST,PORT,e.getMessage());
			Lang.sleep(1000);
		}
		return null;
	}

	@Override
	public void receive() {
		isListen = true;
		log.infof( "TCPClient等待接收 %s:%d",HOST,PORT);
		new Thread(()->{
			while (isListen){
				if(open()){
					try {
						if(reader == null && isReadLine)
							reader = new BufferedReader(new InputStreamReader(is));
						byte[] bytes = reader != null ? readLine() : read();
						this.setChanged();
						this.notifyObservers(bytes);
					}catch (SocketTimeoutException e){
						//System.out.println(Times.getNowSDT());
					}catch(SocketException e){
						log.errorf("TCPClient %s:%d 接收异常(%s),将关闭Socket",HOST,PORT,e.getMessage());
						closeSocket();
					}catch (Exception e) {
						e.printStackTrace();
						Lang.sleep(1000);
					}
				}else{
					Lang.sleep(1000);
				}
			}
		},"TcpSender->inform").start();
	}

	public byte[] read() throws IOException {
		int length;
		int i = 0;
		while(isListen && is != null && is.available() == 0){
			Lang.sleep(listenInterval);//控制CPU占用
			i += listenInterval;
			if(i > 3000){
				i = 0;
				selfTest();
			}
		}

		if(is == null)
			return null;

		do{
			length = is.available();
			Lang.sleep(listenInterval);
		}while (length != is.available());

		byte[] b = new byte[is.available()];
		is.read(b);

		return b;
	}

	public byte[] readLine() throws IOException {
		String line;
		while ((line = reader.readLine()) != null) {
			return line.getBytes();
		}
		return null;
	}

	@Override
	public boolean selfTest(){

		try {
			if(socket.isConnected() && os != null){
				if(Strings.isNotBlank(heartbeat)){
					os.write(CalcUtil.hexToByte(heartbeat));
					os.flush();
				}else{
					socket.sendUrgentData(0xFF);
				}
				return true;
			}
		} catch (IOException e) {
			closeSocket();
			return open();
		}
		return false;
		
	}

	public void cleanInput(){
		try {
			int length = is.available();
			if(length > 0){
				is.read(new byte[length]);
			}
		} catch (IOException ignored) {

		}
	}

	public Socket getSocket() {
		return socket;
	}
}
