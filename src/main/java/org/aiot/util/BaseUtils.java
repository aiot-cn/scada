package org.aiot.util;

import org.aiot.lang.workflow.Workflow;
import org.aiot.model.table.SysTrigger;
import org.aiot.model.table.TBase;
import org.aiot.model.table.TWorkflow;
import org.aiot.service.BaseService;
import org.nutz.lang.Strings;

import java.util.Map;

import static org.aiot.main.Constants.ioc;

public class BaseUtils {
	
	public static TWorkflow getTWorkflow(TBase tBase){
		BaseService bs = ioc.get(BaseService.class);
		return bs.getTCacheFirst(TWorkflow.class, v->
				tBase.getId().equals(v.getPid()) &&
				tBase.getClass().getSimpleName().equals(v.getPlass()));
	}

	public static SysTrigger getTrigger(Long devId,String member){
		BaseService bs = ioc.get(BaseService.class);
		return bs.getTCacheFirst(SysTrigger.class, v->
				devId.equals(v.getDeviceId()) && Strings.equals(member,v.getMember())
		);
	}

	public static Object runWorkflow(TBase tBase,Map<String,Object> params){
		if(tBase == null)
			return null;
		TWorkflow tWorkflow = getTWorkflow(tBase);
		if(tWorkflow == null)
			return null;
		Workflow wf = new Workflow(tWorkflow);
		return wf.run(params);
	}

	public static Object runWorkflow(Long devId,String member,Map<String,Object> params){
		SysTrigger trigger = getTrigger(devId,member);
		if(trigger == null)
			return null;
		return runWorkflow(trigger,params);
	}
}
