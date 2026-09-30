package org.aiot.mvc;

import org.aiot.service.BaseService;
import org.aiot.service.UserService;
import org.nutz.mvc.ActionContext;
import org.nutz.mvc.View;
import org.nutz.mvc.view.ForwardView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.aiot.main.Constants.ioc;

/**
 * 角色[表]权限 <br>
 */
public class RoleTableFilter extends RoleActionFilter{

	@Override
	public View match(ActionContext ac) {
		HttpServletResponse resp = ac.getResponse();
		HttpServletRequest req = ac.getRequest();
		View view = super.match(ac);
		if(view != null)
			return view;
		BaseService bs = ioc.get(BaseService.class);
		UserService us = ioc.get(UserService.class);
		Class<?> c = bs.getModelClass(req.getParameter("tableName"));
		if (!us.hasRoleTable(c)){
			req.setAttribute("obj", "没有表操作权限");
			return new ForwardView("/common/error");
		}
		return null;

	}

}
