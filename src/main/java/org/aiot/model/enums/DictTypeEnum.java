package org.aiot.model.enums;

import org.aiot.main.Constants;
import org.aiot.model.table.SysDict;
import org.aiot.service.BaseService;
import org.aiot.service.ConfigService;
import org.nutz.lang.Strings;

import java.util.ArrayList;
import java.util.List;

/**
 * 字典类型
 * @author TAOJIN
 *
 */
public enum DictTypeEnum {
	recStatus("记录状态"), //0正常 1预警 2报警
	//devStatus("设备状态"),
	args("实参"), //参数值 相当于全局动态参数配置
	dataClassify("数据分类"),//0环境 1消防 2安防
	sysDevice("系统设备"),//以太网 ethernet PCI\VEN_10EC&DEV_8168
	sex("性别"),
	docProject("文档项目"),
	;

	
	private String name;
	
	private DictTypeEnum(String name){
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public SysDict getByCode(String code){
		BaseService bs = Constants.ioc.get(BaseService.class);
		return bs.getTCacheFirst(SysDict.class,v->name().equals(v.getType()) && Strings.equals(v.getCode(),code));
	}

	public String val(String code){
		SysDict dict = getByCode(code);
		return dict == null ? null : dict.getValue();
	}

	public Integer valInt(String code){
		String vs = val(code);
		return Strings.isBlank(vs) ? null : Integer.parseInt(vs);
	}

	
	public List<SysDict> getList(){
		ConfigService cs = Constants.ioc.get(ConfigService.class);
		return cs.getDictMap().getOrDefault(name(),new ArrayList<>());
	}

	@Deprecated
	public SysDict getDict(String value){
		BaseService bs = Constants.ioc.get(BaseService.class);
		return bs.getTCacheFirst(SysDict.class,v->name().equals(v.getType()) && Strings.equals(v.getValue(),value));
	}
    
}
