package org.aiot.mvc;

import org.aiot.main.Constants;
import org.aiot.model.enums.RoleActionEnum;
import org.aiot.model.enums.SessionEnum;
import org.aiot.model.table.user.SysUser;
import org.aiot.service.BaseService;
import org.aiot.service.UserService;
import org.nutz.lang.Strings;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.ActionFilter;
import org.nutz.mvc.View;
import org.nutz.mvc.view.ForwardView;

import javax.servlet.DispatcherType;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 角色权限 <br>
 */
public class RoleActionFilter implements ActionFilter{
	private RoleActionEnum roleAction;

	public RoleActionFilter(){

	}

	public RoleActionFilter(String actionCode) {
        this.roleAction = RoleActionEnum.valueOf(actionCode);
    }
	
	@Override
	public View match(ActionContext ac) {
		HttpServletResponse resp = ac.getResponse();
		HttpServletRequest req = ac.getRequest();
		resp.setHeader("Access-Control-Allow-Origin", "*");//允许跨域请求

		if(req.getDispatcherType() == DispatcherType.FORWARD)
			return null;

		BaseService bs = Constants.ioc.get(BaseService.class);
		UserService us = Constants.ioc.get(UserService.class);
		SysUser user = SessionEnum.user.val();
		if(user == null){
			String token = req.getParameter("token");
			if(Strings.isNotBlank(token)){
				user = us.authToken(token);
			}else{
				user =  bs.getTCacheFirst(SysUser.class,v->v.getIsDefault() == 1);
			}

			if(user != null){
				us.sessionUser(user);
			}else{
				return new ForwardView("/user/login");//内部重定向;
			}
		}
		//只需要登录 或者是超级管理员
		if(roleAction == null || user.getId() == 0)
			return null;

		if(!us.hasRoleAction(user.getRoleId(),roleAction)){
			String msg = "用户["+user.getLogin()+"]没有 "+roleAction.getName()+" 权限";
			req.setAttribute("obj", msg);
			return new ForwardView("/common/error");
		}

		//检查权限
		return null;
	}

}
