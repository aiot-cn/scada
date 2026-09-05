package org.aiot.controller;

import org.aiot.model.DataRes;
import org.aiot.model.enums.SessionEnum;
import org.aiot.model.project.Token;
import org.aiot.model.table.*;
import org.aiot.mvc.CheckLevel;
import org.aiot.service.BaseService;
import org.aiot.service.UserService;
import org.aiot.util.HttpUtil;
import org.aiot.util.SysUtil;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.By;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.filter.CrossOriginFilter;


import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import static org.aiot.main.Constants.ioc;
@At("/user")
public class UserController {
	private NutMap licenseCode = new NutMap();

	@At("/?")
	public @Ok("re") String toPath(String path) {
		return "pm:user." + path;
	}

	@At
	@Filters
    public @Ok("pm:user.login") void login(HttpServletResponse resp){

    }


	@At
	@Filters(@By(type=CrossOriginFilter.class))
	public @Ok("json") NutMap getLicense(String serial) {
		NutMap nm = new NutMap();
		nm.put("license",licenseCode.get(serial));
		return nm;
	}

	@At
	@Filters(@By(type=CrossOriginFilter.class))
	public @Ok("json") DataRes doLogin(String loginName,String password,boolean remember,HttpServletResponse resp) {
		UserService us = ioc.get(UserService.class);
		BaseService bs = ioc.get(BaseService.class);
		try {
			SysUser user = us.validate(loginName, password);
			us.sessionUser(user);
			bs.daoSave(new TLog("账号["+loginName+"]登录"));
			if(remember){
				HttpUtil.addCookie(resp, "loginName", loginName, 30*24*60*60,null);
				HttpUtil.addCookie(resp, "password", password, 30*24*60*60,null);
			}
			NutMap nm = new NutMap().addv("user",user).addv("site",us.getUserDefSite(user.getId()));
			return new DataRes(nm);
		}catch (RuntimeException e){
			bs.daoSave(new TLog(e.getMessage()));
			return new DataRes("登录失败："+e.getMessage());
		}
	}

	/**
	 * 注销用户
	 * 必须使用>>:XXX 即302重定向,不要使用->:XXX内部重定向, 因为后者在shiro环境下会报错.
	 * @param session
	 */
	@At
	@Filters
	public @Ok(">>:/user/login") void  logout(HttpSession session, HttpServletResponse resp) {
		HttpUtil.delCookie(resp, null, "loginName","password");
		session.invalidate();
	}

	@At
	@Filters(@By(type= CheckLevel.class, args="1"))
	public @Ok("pm:user.selectSite") void selectSite() {

	}

	/**
	 * 选择站点
	 */
	@At
	@Filters(@By(type=CheckLevel.class, args="1"))
	public @Ok("json") DataRes setSite(Long siteId) {
		UserService users = ioc.get(UserService.class);
		BaseService bs = ioc.get(BaseService.class);
		SysSite site = bs.getTCache(SysSite.class,siteId);
		if(site == null){
			throw Lang.makeThrow("不存在站点:%d",siteId);
		}
		users.inSite(site);
		return new DataRes();
	}

	/**
	 * 获取用户权限动作
	 */
	@At
	public @Ok("json") Map<String, Integer> getAction(){
		SysUser user = SessionEnum.user.val();
		UserService users = ioc.get(UserService.class);
		return users.getAction(user.getId(),SessionEnum.siteIds.val());
	}

	/**
	 * 获取权限菜单
	 */
	@At
	public @Ok("json") List<SysMenu> getMenu(Long siteId){
		SysUser user = SessionEnum.user.val();
		BaseService bs = ioc.get(BaseService.class);
		NutMap pm = new NutMap().setv("siteIds",SessionEnum.siteIds.val()).setv("userId", user.getId());
		List<SysMenu> menus = bs.querySqlCode("getRoleMenu", pm, SysMenu.class, null);
		if(siteId != null)
			menus  = menus.stream().filter(v->{
				Long dId = v.getDeviceId();
				if(dId == null)
					return true;

				TDevice d = bs.getTCache(TDevice.class, v.getDeviceId());
				return d != null && siteId.equals(d.getSiteId());
			}).collect(Collectors.toList());
		return menus;
	}

	@At
	public @Ok("json") DataRes resetPassword(Long userId,String password) {
		String msg = null;
		SysUser user = SessionEnum.user.val();
		BaseService bs = ioc.get(BaseService.class);
		UserService users = ioc.get(UserService.class);
		SysUser u = bs.getDao().fetch(SysUser.class,userId);
		u.setPassword(users.cipherPassword(password));
		if(bs.getDao().updateIgnoreNull(u) != 1){
			msg = "重置密码失败";
		}
		return new DataRes(msg);
	}

	@At
	@Filters
	public @Ok("json") DataRes getToken(String user,String password){
		UserService us = ioc.get(UserService.class);
		try {
			us.validate(user, password);
			String token = SysUtil.desEncode(Json.toJson(new Token(user)));
			Object t = token.replace('+', '-').replace('/', '_').replaceAll("=", "");
			return new DataRes(t);
		}catch (RuntimeException e){
			return new DataRes("获取失败："+e.getMessage());
		}
	}
	
	
}
