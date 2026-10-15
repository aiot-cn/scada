package org.aiot.main;

import org.aiot.handler.json.JsonFileHandler;
import org.aiot.model.castor.String2File;
import org.aiot.model.enums.*;
import org.aiot.service.*;
import org.aiot.util.SysUtil;
import org.nutz.castor.Castors;
import org.nutz.ioc.impl.PropertiesProxy;
import org.nutz.json.Json;
import org.nutz.json.JsonTypeHandler;
import org.nutz.lang.Files;
import org.nutz.lang.Mirror;
import org.nutz.lang.Strings;
import org.nutz.lang.util.Disks;
import org.nutz.log.Log;
import org.nutz.log.Logs;
import org.nutz.mvc.NutConfig;
import org.nutz.mvc.Setup;
import org.nutz.resource.Scans;

import javax.script.ScriptContext;
import javax.servlet.ServletContext;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.aiot.main.Constants.*;

public class MainSetup implements Setup{
	public void init(NutConfig nc) {
		//避免尝试初始化AWT，无图形界面
		System.setProperty("java.awt.headless", "true");

		Log log = Logs.get();
		ioc = nc.getIoc();
		//这里的conf是dao.js中的键
		prop = ioc.get(PropertiesProxy.class, "conf");
		//修改环境变量IDE中需要重启IDE,所以直接用配置文件
		HOME_PATH = prop.get("AIOT_HOME");

		ServletContext servletContext = nc.getServletContext();
		String contextName = servletContext.getContextPath().replace("/","");
		if(Strings.isBlank(HOME_PATH))
			HOME_PATH = Disks.home() + File.separator + Strings.sBlank(contextName, "aiot")+"-data";

		homePropFile = new File(HOME_PATH,"aiot.properties");
		Files.createFileIfNoExists(homePropFile);
		try (Reader reader = new InputStreamReader(
				new FileInputStream(homePropFile),StandardCharsets.UTF_8)) {
			homeProp.load(reader);
		}catch(IOException e) {
			e.printStackTrace();
		}

		SysUtil.addLibraryPath(PathEnum.lib.p());//JNI
		SysUtil.addLibraryPath(PathEnum.lib.p()+"FFmpeg");
		System.setProperty("jna.library.path",PathEnum.lib.p());//JNA

		ServletEnum.res.val(servletContext.getContextPath()+"/static");
		ServletEnum.resCache.val(System.currentTimeMillis());
		ServletEnum.serialNo.val(servletContext.getSessionCookieConfig().getName());

		for(Class<?> klass: Scans.me().scanPackage(String2File.class)){
			Castors.me().addCastor(klass);
		}
		for(Class<?> klass: Scans.me().scanPackage(JsonFileHandler.class)){
			Json.addTypeHandler((JsonTypeHandler) Mirror.me(klass).born());
		}

		try {
			BaseService bs = ioc.get(BaseService.class);
			ioc.get(ConfigService.class);	//配置
			ioc.get(DeviceService.class);	//设备
			SysUtil.jsFile("script/baseJava.js","script/analysis.js");
			Constants.scriptManager.getBindings().putAll(jse.getBindings(ScriptContext.ENGINE_SCOPE));
			ioc.get(CommuService.class);	//通讯
			ioc.get(CronService.class);	//定时任务

			ioc.get(AiotService.class).log(LogEventEnum.SYSTEM_BOOTING, "系统启动");
			try {
				ScriptEnum.initCall.exec();
			}catch (Exception e){
				log.warn(ANSI.COLOR_FORE.yellow.format("启动脚本："+e.getMessage()));
			}

		}catch (Exception e){
			e.printStackTrace();
		}
		isAuthorized = SysUtil.beAuthorized(PropHomeEnum.license.val()) == null;
		//ioc.addBean(name, Mirror.me(c).born());
	}

	public static String daoPath(){
		String dbUrl = PropHomeEnum.DB_URL.val();
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
