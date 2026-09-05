package org.aiot.controller;

import org.aiot.main.Constants;
import org.aiot.model.DataRes;
import org.aiot.model.enums.DictTypeEnum;
import org.aiot.model.enums.PropEnum;
import org.aiot.model.enums.ServletEnum;
import org.aiot.model.enums.VarRuntimeEnum;
import org.aiot.model.table.SysDict;
import org.aiot.model.table.TParam;
import org.aiot.service.ConfigService;
import org.aiot.util.CommonUtil;
import org.aiot.util.SysUtil;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Filters;
import org.nutz.mvc.annotation.Ok;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.aiot.main.Constants.ioc;
@At("/config")
public class ConfigController {
	@At
	public @Ok("pm:config.user") void user(){

	}
	//==================== 参数 =============================
	@At
	@AdaptBy(type=JsonAdaptor.class)
    public @Ok("json")
	DataRes saveParams(List<TParam> config){
		ConfigService cs = ioc.get(ConfigService.class);
		for(TParam v : config){
			cs.saveConfigParams(v);
		}
		return new DataRes();
    }

	@At
	public @Ok("json") DataRes getConfig(String key){
		Map<String,Object> config = ServletEnum.config.val();
		return new DataRes(config.get(key));
	}

	@At
	public @Ok("json") DataRes varRuntime(String key,String value){
		VarRuntimeEnum.valueOf(key).val(value);
		return DataRes.success("");
	}

	/*-------------------- 系统 ---------------------------*/

	@At
	@Filters
	public @Ok("json") DataRes setLicense(String license){
		String msg = SysUtil.beAuthorized(license);
		if(msg == null){
			Constants.isAuthorized = true;
			PropEnum.license.val(license);
		}
		return new DataRes(msg);
	}

}
