package org.aiot.lang;

import org.aiot.infc.ProtocolInfc;
import org.aiot.communication.CommunicationInfc;
import org.aiot.model.table.*;
import org.aiot.service.DeviceService;
import org.aiot.service.WebsocketRoom;
import org.nutz.lang.Lang;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import static org.aiot.main.Constants.ioc;

/**
 * 通讯线程
 * @author QCJ
 *
 */
public class CommuncationThread implements Runnable {

	Log log = Logs.get();
	private PriorityBlockingQueue<Command> commandQueue = new PriorityBlockingQueue<>(100);
	private CountDownLatch countDownLatch = new CountDownLatch(1);
	private CommunicationInfc commuInfc;
	private boolean isRun = true;

	public CommuncationThread(CommunicationInfc commuInfc) {
		this.commuInfc = commuInfc;
	}


	public void statr(){
		TCommunication commu = commuInfc.getCommun();
		Thread t = new Thread(this,"Communcation-"+commu.getId());
		log.infof("通讯轮询 - %s %s:%s",commu.getName(),commu.getKlass(),commu.getUri());
		t.start();
	}

	public void stop(){
		isRun = false;
		commandQueue.clear();
		TCommunication commu = commuInfc.getCommun();
		log.infof("通讯线程结束 - %s:%s",commu.getName(),commu.getUri());
	}

	@Override
	public void run() {
		while(isRun){
			try{
				Command command = commandQueue.poll();
				if(command == null){
					addPoll();
					continue;
				}
				ProtocolInfc protocol = command.getProtocolInfc();
				if(protocol == null){
					continue;
				}

				long t1 = System.currentTimeMillis();
				if(t1 - command.getCreateTime() > 5 * 60 * 1000){
					TCommunication commu = commuInfc.getCommun();
					log.error("通讯["+commu.getId()+"]"+commu.getName()+" 发送时间超过五分钟,清空当前"+commandQueue.size()+"条指令");
					commandQueue.clear();
				}
				try {
					protocol.build(command);
				}catch (Exception e){
					sendSocket("设备["+command.getDevice().getName()+"]指令["+command.getDeviceCommand().getCode()+"] "+e.getMessage());
					Lang.sleep(1000);
					continue;
				}

				byte[] b = command.sendCommand(commuInfc);
				command.setRX(b);

				/*if(System.currentTimeMillis() - t1 > 3000){
					log.warnf("设备[%s]指令[%d]发送时间过长",command.getDevice().getName(),command.getDeviceCommand().getId());
				}*/

				protocol.analysis(command);

			}catch(Throwable e){
				//原则上异常应该在上层处理，这里捕获异常只是为了避免通讯线程终止
				//e.printStackTrace();
				sendSocket("error 通讯线程 发送指令时异常 ："+e.getMessage());
				Lang.sleep(1000);
			}
		}

	}

	public void addPoll(){
		TCommunication commu = commuInfc.getCommun();
		DeviceService ds = ioc.get(DeviceService.class);
		for(TDevice d : ds.getDeviceByCommu(commu.getId())){
			try {
				//不用返回值添加是为了避免手动执行依赖线程，但此处语义不明
				ds.getInstance(d).comPoll();
			}catch (Throwable e){
				sendSocket("设备:%s(%d) 巡检指令添加错误:%s",d.getName(),d.getId(),e.getMessage());
			}
		}
		if(commandQueue.size() == 0){
			sendSocket("通讯线程["+commu.getName()+"]本轮没有获取到巡检指令");
			countDownLatch = new CountDownLatch(1);
			try {
				countDownLatch.await(1000, TimeUnit.MILLISECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	public void sendSocket(String msg,Object... args){
		WebsocketRoom.sendCommu(commuInfc.getCommun(),String.format(msg,args));
	}

	public void addCommand(Command c){
		DeviceCommand dc = c.getDeviceCommand();
		if(dc != null && dc.isCover()){
			Optional<Command> op = commandQueue.stream().filter(
					v -> v.getDevice() == c.getDevice() && v.getDeviceCommand() == c.getDeviceCommand()
			).findFirst();
			op.ifPresent(v->v.setArgs(c.getArgs()));
			if(!op.isPresent())
				commandQueue.offer(c);
		}else {
			commandQueue.offer(c);
		}
		countDownLatch.countDown();
	}

}
