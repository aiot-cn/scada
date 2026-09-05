package org.aiot.controller;

import org.aiot.infc.device.DeviceInfc;
import org.aiot.model.DataRes;
import org.aiot.model.enums.ConfigEnum;
import org.aiot.model.lang.DeviceTypeComposite;
import org.aiot.model.table.*;
import org.aiot.service.BaseService;
import org.aiot.service.DeviceService;
import org.aiot.util.CommonUtil;
import org.aiot.util.HttpUtil;
import org.apache.commons.beanutils.BeanUtils;
import org.nutz.dao.Cnd;
import org.nutz.lang.Files;
import org.nutz.lang.Lang;
import org.nutz.lang.Streams;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;
import org.nutz.log.Logs;
import org.nutz.mvc.View;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;
import org.nutz.mvc.view.RawView;
import org.nutz.mvc.view.UTF8JsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.aiot.main.Constants.ioc;

@At("/device")
public class DeviceController {

	@At
	public @Ok("json") List<TDevice> getDevice(Long siteId,String klass) throws ClassNotFoundException {
		BaseService bs = ioc.get(BaseService.class);
		List<TDevice> list = bs.getTCache(TDevice.class,v->siteId == null || siteId.equals(v.getSiteId()));
		if(Strings.isNotBlank(klass)){
			DeviceService ds = ioc.get(DeviceService.class);
			Class<?> c = Lang.loadClass(klass);
			list = list.stream().filter(v->{
				DeviceInfc bd = ds.getInstance(v.getId());
				return bd != null && c.isAssignableFrom(bd.getClass());
			}).collect(Collectors.toList());
		}
		return list;
	}

	/**
	 * 获取设备类型实现
	 */
	@At
	public @Ok("json") DataRes getTypeImplement(){
		List<Class<?>> devClass = ioc.get(DeviceService.class).getDevClassList();
		List<NutMap> list = new ArrayList<>();
		for(Class<?> c : devClass){
			list.add(CommonUtil.aoClassStrMap(c));
		}
		return new DataRes(list);
	}

	/**
	 * 调用设备方法 必须以map方式传值以符合调用习惯
	 * @param devNo 设备ID或类型
	 * @param methodName 方法名
	 */
	@At("/exec/?/?")
	public void exec(String devNo, String methodName, HttpServletRequest req, HttpServletResponse resp) throws Throwable {
		DeviceService ds = ioc.get(DeviceService.class);
		TDevice dev = ds.getDeviceFirst(devNo);
		if(dev == null)
			throw Lang.makeThrow("没有找到标识为[%s]的设备", devNo);

		DeviceInfc d = ds.getInstance(dev.getId());
		Map<String,Object> m = new HashMap<>();
		m.put("request",req);
		m.put("response",resp);
		req.getParameterMap().forEach((k,v)-> m.put(k,v[0]));

		if("$".equals(methodName))
			methodName = req.getParameter("methodName");

		try {
			Object re =  d.invoke(methodName,m);
			if(re == null){
				UTF8JsonView.COMPACT.render(req, resp, DataRes.success(""));
				return;
			}

			if(re instanceof DataRes){
				UTF8JsonView.COMPACT.render(req, resp, re);
				return;
			}

			if(HttpUtil.isAjax(req)){
				UTF8JsonView.COMPACT.render(req, resp, new DataRes(re));
				return;
			}

			if(re instanceof BufferedImage){
				new RawView("jpg").render(req, resp, re);
				return;
			}

			if(re instanceof File && !HttpUtil.isAjax(req)){
				File file = (File)re;
				if(file.getName().endsWith(".pdf")){
					resp.setContentType("application/pdf");
					new RawView("pdf").render(req, resp, Files.readBytes(file));
				}else{
					new RawView("pdf").render(req, resp, re);
				}

				return;
			}

			if(re instanceof View){
				View view = (View) re;
				view.render(req,resp,null);
				return;
			}

			UTF8JsonView.COMPACT.render(req, resp, new DataRes(re));

		}catch (Exception e){
			e.printStackTrace();
			StringBuilder s = new StringBuilder();
			String contentType = req.getHeader("Content-Type");
			if("application/x-www-form-urlencoded".equals(contentType)){
				m.remove("request");
				m.remove("response");
				s.append(m);
			}else{
				s.append(Lang.readAll(Streams.utf8r(req.getInputStream())));
			}

			Logs.get().error(dev.getName() + "["+devNo+ "] -> " + methodName + s + " msg:"+e.getMessage()+" from Ip:"+Lang.getIP(req)+" Content-Type:"+contentType);
			if(ConfigEnum.debug.getBoolean())
				e.printStackTrace();

			String msg = "调用"+d.getClass().getName()+"."+methodName+"() 失败:"+e.getMessage();
			if(e instanceof RuntimeException)
				msg = e.getMessage();
			UTF8JsonView.COMPACT.render(req, resp, DataRes.error(msg));
		}

	}

	@At
	public @Ok("json") DataRes propToPoint(){
		BaseService bs = ioc.get(BaseService.class);
		bs.getTCache(TDevice.class).forEach(t -> {
			bs.getTCache(DeviceProperty.class,p -> Strings.equals(t.getDeviceType(),p.getDeviceType())).forEach(p -> {
				String pointCode = "dev-"+t.getId()+"-"+p.getCode();
				TPoint point = bs.getTCacheFirst(TPoint.class,v -> Strings.equals(pointCode,v.getCode()));
				if(point == null){
					point = new TPoint();
					point.setRecOnTime(true);//默认定时保存
				}
				point.setCode(pointCode);
				point.setName(t.getName()+"-"+p.getName());
				point.setUnit(p.getUnit());
				bs.daoSave(point);
			});
		});
		return new DataRes();
	}

	//====================	设备（类型）导入导出 =============================
	@At
	public @Ok("json") DeviceTypeComposite deviceTypeJson(String code) throws InvocationTargetException, IllegalAccessException {
		BaseService bs = ioc.get(BaseService.class);
		DeviceType dt = bs.daoFetch(DeviceType.class, Cnd.where("code","=",code));
		DeviceTypeComposite devType = new DeviceTypeComposite();
		BeanUtils.copyProperties(devType, dt);
		List<DeviceCommand> commands = bs.query(DeviceCommand.class, Cnd.where("deviceType","=",code));
		for(DeviceCommand dc : commands){
			dc.setAnalysis(bs.getTCacheStreamAll(DeviceAnalysis.class).filter(v->dc.getId().equals(v.getCommandId())).collect(Collectors.toList()));
		}
		devType.setCommand(commands);
		devType.setProperty(bs.getTCacheStreamAll(DeviceProperty.class).filter(v->code.equals(v.getDeviceType())).collect(Collectors.toList()));
		return devType;
	}

	@At
	public @Ok("json") DeviceType deviceTypeImp(String code,@Param("json")DeviceTypeComposite devType) throws InvocationTargetException, IllegalAccessException {
		BaseService bs = ioc.get(BaseService.class);
		devType.setCode(code);
		DeviceType dt = bs.getTCacheAllFirst(DeviceType.class,v->code.equals(v.getCode()));
		Long tid = null;
		if(dt != null){
			tid = dt.getId();
			List<DeviceProperty> dps = bs.getTCacheStreamAll(DeviceProperty.class).filter(v->Strings.equals(v.getDeviceType(),code)).collect(Collectors.toList());
			List<DeviceCommand> dcs = bs.getTCacheStreamAll(DeviceCommand.class).filter(v->Strings.equals(v.getDeviceType(),code)).collect(Collectors.toList());
			dps.forEach(bs::daoDel);
			dcs.forEach(bs::daoDel);
		}else{
			dt = new DeviceType();
		}
		BeanUtils.copyProperties(dt,devType);
		dt.setId(tid);
		bs.daoSave(dt);
		bs.setTCache(dt);

		for(DeviceProperty dp : devType.getProperty()){
			dp.setDeviceType(code);
			bs.daoSave(dp);
		}

		for(DeviceCommand dc : devType.getCommand()){
			dc.setDeviceType(code);
			bs.daoSave(dc);
			if(dc.getAnalysis() != null)
				for(DeviceAnalysis da : dc.getAnalysis()){
					da.setCommandId(dc.getId());
					bs.daoSave(da);
				}
		}
		return dt;
	}

	//====================	API =============================
	@At
	public @Ok("json") DataRes setValue(String device,String code,String value) throws Throwable{
		DeviceService ds = ioc.get(DeviceService.class);
		TDevice dev = ds.getDeviceFirst(device);
		DeviceInfc d = ds.getInstance(dev.getId());
		DeviceProperty dp = ds.getProperty(dev,code);
		if(dp != null && dp.getType() == 2){
			d.comSet(code,Integer.parseInt(value));
		}else{
			d.putData(code,value);
		}
		return new DataRes();
	}
}
