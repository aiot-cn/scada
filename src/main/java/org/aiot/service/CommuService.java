package org.aiot.service;

import org.aiot.infc.ProtocolInfc;
import org.aiot.communication.CommunicationInfc;
import org.aiot.communication.ReceiveData;
import org.aiot.infc.device.DeviceInfc;
import org.aiot.lang.Command;
import org.aiot.lang.CommuncationThread;
import org.aiot.lang.NotifyEvent;
import org.aiot.model.enums.CommandTypeEnum;
import org.aiot.model.enums.EventEnum;
import org.aiot.model.table.DeviceCommand;
import org.aiot.model.table.TCommunication;
import org.aiot.model.table.TDevice;
import org.aiot.model.table.TParam;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Log;
import org.nutz.log.Logs;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * 通讯服务
 */
@IocBean(create="init")
public class CommuService implements Observer  {
	Log log = Logs.get();
	@Inject BaseService bs;
	@Inject ConfigService cs;
	@Inject DeviceService ds;

	private Map<Long, CommunicationInfc> instance = new ConcurrentHashMap<>();
	private Map<Long,CommuncationThread> commuThread = new ConcurrentHashMap<>();
	private Map<String,ProtocolInfc> protocolMap = new ConcurrentHashMap<>();

	public void init() {
		bs.addObserver(this);
		bs.getTCacheStreamAll(TCommunication.class).forEach(v->new Thread(()->commuStart(v)).start());
	}

	public void close(){
		instance.forEach((k,v)->{
			v.close();
		});
	}

	public CommunicationInfc getInstance(Long id){
		return  instance.get(id);
	}

	private CommunicationInfc getInstance(TCommunication commun){
		CommunicationInfc ci = instance.get(commun.getId());
		if(ci == null)
			try {
				Class<?> c = Lang.loadClass(commun.getKlass());
				Constructor<?> constructor = c.getDeclaredConstructors()[0];
				Object[] arg = new Object[constructor.getParameterCount()];
				ci = (CommunicationInfc) constructor.newInstance(arg);
				ci.setCommun(commun);
				instance.put(commun.getId(),ci);
				log.infof("通讯加载 - %s:%s",commun.getName(),commun.getUri() );
			}catch (RuntimeException e) {
				log.errorf("%s异常：%s",commun.getKlass(),e.getMessage());
			}catch (ClassNotFoundException e) {
				log.errorf("通讯类 %s 没有找到",commun.getKlass());
			}catch (InvocationTargetException e) {
				log.errorf("通讯类 %s:newInstance(%s)",commun.getKlass(),commun.getUri());
			}catch (InstantiationException | IllegalAccessException e) {
				e.printStackTrace();
			}

		return ci;
	}

	public ProtocolInfc getProtocol(String klass){
		ProtocolInfc ci = protocolMap.get(klass);
		if(ci == null)
			try {
				Class<?> c = Lang.loadClass(klass);
				Constructor<?> constructor = c.getDeclaredConstructors()[0];
				Object[] arg = new Object[constructor.getParameterCount()];
				ci = (ProtocolInfc) constructor.newInstance(arg);
				protocolMap.put(klass,ci);
			}catch (Exception e) {
				e.printStackTrace();
			}

		return ci;
	}

	public void commuStart(TCommunication v){
		CommunicationInfc cf = getInstance(v);
		if(cf == null)
			return;
		cs.injectParams(cf, 4,v.getId());

		if(v.getIsRemoved() != 0)
			return;

		if(!cf.open()){
			log.errorf("通讯 %s:%s 打开失败",v.getName(),v.getUri());
		}
		if(v.isListen()) {//监听模式
			cf.addObserver(this);
			cf.receive();
		}else{
			//轮询模式
			commuThread.computeIfAbsent(v.getId(), c->new CommuncationThread(cf)).statr();
		}
	}

	//-------------------------------------------添加指令到队列-------------------------------------------------------------

	public void addCommand(List<Command> commands,Object... args){
		for(Command c:commands){
			c.setArgs(args);
			TCommunication commu = c.getCommunication();

			if(commu == null){
				log.errorf("设备 %s 没有通讯方式，不能添加指令 %s:%s",c.getDevice().getName(),c.getDeviceCommand().getCode(),c.getContent());
				continue;
			}

			CommuncationThread ct = commuThread.get(commu.getId());
			if(ct != null)
				ct.addCommand(c);
		}
	}

	@Override
	public void update(Observable o, Object event) {
		if(!(event instanceof NotifyEvent))
			return;
		NotifyEvent ne = (NotifyEvent) event;
		if(ne.getEventType() != EventEnum.SAVE_AFTER)
			return;
		Object arg = ne.getData();

		if(o instanceof CommunicationInfc){
			listen(arg,(CommunicationInfc) o);
		}else if(o instanceof BaseService){
			if(arg instanceof TCommunication){
				TCommunication commu = (TCommunication) arg;

				CommuncationThread ct = commuThread.get(commu.getId());
				if(ct != null){
					ct.stop();
					commuThread.remove(commu.getId());
				}

				CommunicationInfc ci =  instance.get(commu.getId());
				if(ci != null)
					ci.close();

				if(commu.getIsRemoved() != 0)
					return;

				commuStart(commu);

			}
		}else if(arg instanceof TParam){
			TParam param = (TParam) arg;
			if(param.getType() == 4){
				cs.injectParams(getInstance(param.getCid()),param.getCode(),param.getValue());;
			}
		}
	}

	public void listen(Object data, CommunicationInfc ci){
			if(data == null)
				return;

			byte[] b = null;
			String s = null;
			String topic = "";

		try {
			TCommunication cc = ci.getCommun();
			if(data instanceof ReceiveData){
				ReceiveData rx = ((ReceiveData) data);
				b = rx.getBytes();
				topic = rx.getTopic();
			}else if(data instanceof String){
				s = data.toString();
			}else if(data instanceof byte[]){
				b = (byte[]) data;
			}else{
				s = data+"";
			}

			s = ci.sendListen(b,topic,s);

			String verifyStr = Strings.sBlank(topic,s);

			List<TDevice> deviceList = bs.getTCache(TDevice.class, dev -> cc.getId().equals(dev.getCommunication()));
			for (TDevice device : deviceList) {
				DeviceInfc bd = ds.getInstance(device.getId());
				if(bd == null)
					continue;
				List<DeviceCommand> l = bs.getTCache(DeviceCommand.class, v -> device.getDeviceType().equals(v.getDeviceType()));
				if(l.size() == 0){
					Command c = new Command();
					c.setDataReceived(b);
					c.setReceive(data);
					bd.comRx(c);
				}else{
					l = l.stream().filter(v->
							Strings.equals(v.getCode(), CommandTypeEnum.comRx.name()) ||
							Strings.isNotBlank(v.getPattern())
					).collect(Collectors.toList());

					NutMap arg = new NutMap();
					for(DeviceCommand devCom:l){
						Command c = new Command(device,devCom,null);
						c.setDataReceived(b);
						c.setReceive(data);
						if(Strings.isNotBlank(devCom.getCrc())){
							if(!c.crcVerify())
								continue;
						}else if ((b == null && Strings.isBlank(s)) || Strings.isBlank(verifyStr) || c.isVerify(verifyStr) != null){
							continue;
						}
						arg.put("command", c);
						bd.invoke(devCom.getCode(), arg);
					}

				}
			}

		}catch (Exception e){
			e.printStackTrace();
		}
	}


}
