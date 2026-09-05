package org.aiot.communication.mode;

import org.aiot.communication.CommunicationInfc;
import org.aiot.lang.Command;
import org.aiot.lang.annotation.AoReflect;
import org.aiot.main.Constants;
import org.aiot.model.enums.AstEnum;
import org.aiot.model.table.DeviceCommand;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.io.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author DTJ
 */
@AoReflect("控制台")
public class Console extends CommunicationInfc {
	private Log log = Logs.get();

	@AoReflect(value="编码",type = AstEnum.param,placeholder = "GBK、UTF-8、ISO-8859-1")
	String charsetName = Lang.isWin()?"GBK":"UTF-8";

	private Process process;
	private InputStream is = null;
	private OutputStream os = null;
	private PrintWriter writer;
	private StringBuilder sb;
	private CountDownLatch countDownLatch = new CountDownLatch(1);

	@Override
	public boolean open() {
		if(os == null && Strings.isNotBlank(commun.getUri())){
			Command cmd= new Command();
			cmd.setContent(Constants.format(commun.getUri()));
			send(cmd);
		}
		return true;
	}

	@Override
	public byte[] send(Command command) {
		try {
			String con = command.getContent();
			countDownLatch = new CountDownLatch(1);
			sb = new StringBuilder();
			if(os == null){
				String[] c = con.split("\\s+");
				ProcessBuilder pb = new ProcessBuilder(c);
				pb.redirectErrorStream(true);// 合并 错误流和标准流
				process = pb.start();
				//process = Runtime.getRuntime().exec(command.getContent());
				is = process.getInputStream();
				os = process.getOutputStream();
				writer = new PrintWriter(new OutputStreamWriter(os, charsetName));

				BufferedReader reader = new BufferedReader(new InputStreamReader(is,charsetName));
				new Thread(()->{
					try {
						String line;
						while ((line = reader.readLine()) != null) {
							if(sb != null){
								sb.append(line).append("\n");
								if(line.contains("[finish]")){
									countDownLatch.countDown();
								}else if(sb.length() > 100000){
									sb = null;
								}
							}
							notify(line);
						}
						int status = process.waitFor();
						notify("[终止]:"+status);
					} catch (IOException | InterruptedException e) {
						notify("[error]:"+e.getMessage());
					} finally {
						countDownLatch.countDown();
						os = null;
					}
				}).start();

			}else{
				writer.println(con);
				writer.flush();
			}

			DeviceCommand dc = command.getDeviceCommand();
			if(dc != null && dc.getResponseTime() != null){
				countDownLatch.await(dc.getResponseTime(), TimeUnit.MILLISECONDS);
				byte[] b = sb.toString().getBytes();
				return b;
			}


		} catch (IOException e) {
			os = null;
			notify("[error]:"+e.getMessage());
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void notify(String data){
		this.setChanged();
		this.notifyObservers(data);
	}


	@Override
	public boolean close() {

		try {
			if(os != null){
				os.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

		if(process != null){
			process.destroy();
		}

		return true;
	}

	public String getCharsetName() {
		return charsetName;
	}

	public void setCharsetName(String charsetName) {
		this.charsetName = charsetName;
	}
}
