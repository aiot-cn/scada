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

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.Optional;

/**
 * @author DTJ
 */
@AoReflect("TCPServer")
public class TCPServer extends CommunicationInfc {
	private Log log = Logs.get();
	ServerSocket serverSocket;
	Socket socket;
	private InputStream is;
	private OutputStream os;
	//Map<String, Socket> socketMap = new HashMap<>();

	private int port = 502;

	@AoReflect(value = "监听间隔",type = AstEnum.param)
	private int listenInterval = 10;

	private boolean isListen;

	@Override
	public boolean open() {
        try {  
			if(serverSocket != null && !serverSocket.isClosed() ){
			   return true;
			}

			String uri = commun.getUri();
			if(Strings.isNotBlank(uri)){
				port = Integer.parseInt(uri);
			}

			serverSocket = new ServerSocket(port);
			log.infof("TCP服务：%d 已启动",port);
			getSocket();
             
        } catch (IOException e) {
        	e.printStackTrace();
            close();
            return false;
        }
		return true;
	}

	@Override
	public boolean close() {
		isListen = false;
		try {
			if(serverSocket != null){
				serverSocket.close();
				serverSocket = null;
			}

			closeSocket();

		} catch (IOException e) {
			e.printStackTrace();
		}
		log.warnf("TCP服务：%d 已关闭",port);
		return true;
	}

	public void getSocket(){
		log.infof("TCP服务:%d 开始监听连接", port);
		new Thread(()->{
			while(serverSocket != null){
				try {
					Socket s = serverSocket.accept();//从Socket队列中取出连接
					closeSocket();

					socket = s;
					is = s.getInputStream();
					os = s.getOutputStream();

					InetAddress inet = socket.getInetAddress();
					log.infof("TCPServer:%d 收到连接 %s:%d",port,inet.getHostAddress(),socket.getPort());
					//socketMap.put(inet.getHostAddress(), socket);
				}catch (SocketException e){
					log.warnf("TCP服务:%d 结束监听:%s",port,e.getMessage());
					break;
				}catch (IOException e) {
					e.printStackTrace();
					break;
				}

			}
		}).start();
	}

	@Override
	public void receive() {
		isListen = true;
		 new Thread(() -> {
			while (isListen) {
				try {
					if (is != null && is.available() > 0) {
						byte[] b = new byte[is.available()];
						is.read(b);
						setChanged();
						notifyObservers(b);
						if(is.available() > 0){
							is.read(new byte[is.available()]);
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
				Lang.sleep(listenInterval);
			}
		},"TcpServer - "+getCommun().getUri()).start();
	}

	public void closeSocket(){
		try {
			if(is != null){
				is.close();
				is = null;
			}

			if(os != null)
				os.close();

			if(socket != null)
				socket.close();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public byte[] send(Command command) {

		if(os == null){
			sendSocket(CdataEnum.Er,"还没有被连接");
			Lang.sleep(1000);
			return null;
		}

		try {
			os.write(command.getDataToSend());
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
		} catch (IOException e) {
			sendSocket(CdataEnum.Er,e.getMessage());
			Lang.sleep(1000);
		}
		return null;
	}


}
