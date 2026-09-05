package org.aiot.mvc;

import org.aiot.main.Constants;
import org.aiot.service.UserService;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.ForwardView;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * int level <br>
 * 0无需验证 <br>
 * 1需要登录 <br>
 * 2进入站点<br>
 * @author taojin
 *
 */
public class CheckLevel implements ActionFilter{
	private int level;

	public CheckLevel(int level) {
        this.level = level;
    }
	
	@Override
	public View match(ActionContext ac) {
		HttpServletResponse resp = ac.getResponse();
		HttpServletRequest req = ac.getRequest();
		resp.setHeader("Access-Control-Allow-Origin", "*");//允许跨域请求

		if(level == 0 || req.getDispatcherType() == DispatcherType.FORWARD)
			return null;

		UserService us = Constants.ioc.get(UserService.class);
		if(us.autoLogon(level,req.getParameter("token")))
			return null;

		return new ForwardView("/user/login");//内部重定向;
	}

}
