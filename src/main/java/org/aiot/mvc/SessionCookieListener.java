package org.aiot.mvc;


import org.aiot.util.SysUtil;
import org.nutz.log.Logs;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.SessionCookieConfig;
public class SessionCookieListener implements ServletContextListener{

	public void contextDestroyed(ServletContextEvent arg0){
	
	}

	public void contextInitialized(ServletContextEvent contextEvent){
		SessionCookieConfig scc = contextEvent.getServletContext().getSessionCookieConfig();
		String serial = SysUtil.getHDSerial();
		scc.setName(serial);
		Logs.get().info("SessionCookie:"+serial);
	}


}
