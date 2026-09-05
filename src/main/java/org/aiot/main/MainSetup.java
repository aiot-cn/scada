package org.aiot.main;

import org.aiot.model.castor.String2File;
import org.aiot.model.enums.*;
import org.aiot.handler.json.JsonFileHandler;
import org.aiot.model.table.TLog;
import org.aiot.service.*;
import org.aiot.util.SysUtil;
import org.nutz.castor.Castors;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.*;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.resource.Scans;

import javax.script.ScriptContext;
import javax.servlet.ServletContext;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.aiot.main.Constants.*;

public class MainSetup implements Setup{
	public void init(NutConfig nc) {
		//避免尝试初始化AWT，无图形界面
		System.setProperty("java.awt.headless", "true");

		Log log = Logs.get();
		ioc = nc.getIoc();
		prop = ioc.get(PropertiesProxy.class, "conf");
		HOME_PATH = prop.get("HOME_PATH", Disks.home());

		ServletContext servletContext = nc.getServletContext();
		String contextName = servletContext.getContextPath().replace("/","");
		HOME_PATH += File.separator + prop.get("HOME_PATH_SUB",Strings.sBlank(contextName, "aiot"));

		SysUtil.addLibraryPath(PathEnum.lib.p());//JNI
		SysUtil.addLibraryPath(PathEnum.lib.p()+"FFmpeg");
		System.setProperty("jna.library.path",PathEnum.lib.p());//JNA

		File propFile = new File(HOME_PATH,"aiot.properties");
		Files.createFileIfNoExists(propFile);
		try {
			Reader reader = Streams.fileInr(propFile);
			propStation.load(reader);
			Streams.safeClose(reader);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ServletEnum.res.val(servletContext.getContextPath()+"/static");
		ServletEnum.resCache.val(Times.getNowSDT());
		ServletEnum.serialNo.val(servletContext.getSessionCookieConfig().getName());

		for(Class<?> klass: Scans.me().scanPackage(String2File.class)){
			Castors.me().addCastor(klass);
		}
		for(Class<?> klass: Scans.me().scanPackage(JsonFileHandler.class)){
			Json.addTypeHandler((JsonTypeHandler) Mirror.me(klass).born());
		}

		isAuthorized = SysUtil.beAuthorized(PropEnum.license.val()) == null;
		try {
			BaseService bs = ioc.get(BaseService.class);
			ioc.get(ConfigService.class);	//配置
			ioc.get(DeviceService.class);	//设备
			SysUtil.jsFile("script/baseJava.js","script/analysis.js");
			Constants.scriptManager.getBindings().putAll(jse.getBindings(ScriptContext.ENGINE_SCOPE));
			ioc.get(CommuService.class);	//通讯
			ioc.get(CronService.class);	//定时任务

			bs.daoSave(new TLog("系统启动"));
			try {
				ScriptEnum.initCall.exec();
			}catch (Exception e){
				log.warn(ANSI.COLOR_FORE.yellow.format("启动脚本："+e.getMessage()));
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		//ioc.addBean(name, Mirror.me(c).born());
	}

	public static String daoPath(){
		String dbUrl = PropEnum.DB_URL.val();
		if(dbUrl.startsWith("jdbc:")){
			VarRuntimeEnum.dbUrl.val(dbUrl);
		}else{
			File daoFile = new File(HOME_PATH,dbUrl);
			VarRuntimeEnum.dbUrl.val("jdbc:sqlite:"+daoFile.getAbsolutePath());
			if(daoFile.isFile() && (!daoFile.canRead() || !daoFile.canWrite())){
				throw new RuntimeException("数据库文件不能读写："+daoFile.getAbsolutePath());
			}
		}
		return VarRuntimeEnum.dbUrl.val();
	}

	public void destroy(NutConfig nc) {
		AiotService as = ioc.get(AiotService.class);
		as.destroy();
		try {
			while(DriverManager.getDrivers().hasMoreElements()) {
				DriverManager.deregisterDriver(DriverManager.getDrivers().nextElement());
			}
		}catch (SQLException ignored){

		}
	}

}
