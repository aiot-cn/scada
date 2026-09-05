package org.aiot.lang;

import org.aiot.lang.workflow.Workflow;
import org.aiot.model.table.SysTrigger;
import org.aiot.model.table.TWorkflow;
import org.aiot.service.BaseService;
import org.quartz.*;
import static org.aiot.main.Constants.ioc;


@DisallowConcurrentExecution // 禁止同一任务的并发执行
public class CronAction implements Job {

	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		JobDataMap dataMap = context.getMergedJobDataMap();
		SysTrigger sysCrontab = (SysTrigger) dataMap.get("sysTrigger");
		BaseService bs = ioc.get(BaseService.class);
		TWorkflow tw = bs.getTCacheFirst(TWorkflow.class, v->sysCrontab.getId().equals(v.getPid()) && SysTrigger.class.getSimpleName().equals(v.getPlass()));
		if(tw != null){
			Workflow wf = new Workflow(tw);
			wf.run(dataMap);
		}
	}

}
