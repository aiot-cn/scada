package org.aiot.service;

import org.aiot.lang.CronAction;
import org.aiot.lang.NotifyEvent;
import org.aiot.model.enums.EventEnum;
import org.aiot.model.table.SysTrigger;
import org.nutz.ioc.loader.annotation.Inject;
import org.nutz.ioc.loader.annotation.IocBean;
import org.nutz.lang.Lang;
import org.nutz.lang.util.NutMap;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;
import org.quartz.impl.matchers.GroupMatcher;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.Set;

@IocBean(create="init")
public class CronService implements Observer {
	@Inject BaseService bs;
	Scheduler scheduler;

	public void init(){
		bs.addObserver(this);
		try {
			scheduler = StdSchedulerFactory.getDefaultScheduler();
			scheduler.start();
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
		bs.getTCache(SysTrigger.class, v->v.getDeviceId() == -3).forEach(this::add);
	}
	
	//新建一个任务
	public String add(Long id){
		SysTrigger sc = bs.getTCache(SysTrigger.class, id);
		if (sc != null) {
			return add(sc);
		}else{
			return "没有查询到该任务";
		}		
	}

	/**
	 * name是它们在这个sheduler里面的唯一标识。如果我们要更新一个JobDetail定义，只需要设置一个name相同的JobDetail实例即可。
	 * group是一个组织单元，sheduler会提供一些对整组操作的API，比如 scheduler.resumeJobs()。
	 */
	public String add(SysTrigger sc) {
		String msg = null;
		try {
			JobKey jobKey = jobKey(sc.getId());
			JobDataMap dataMap = new JobDataMap();
			dataMap.put("sysTrigger",sc);
			JobDetail job = JobBuilder.newJob(CronAction.class)
					.withIdentity(jobKey).usingJobData(dataMap).build();
			Trigger cronTrigger = TriggerBuilder.newTrigger()
					.withIdentity(sc.getId()+"")
					.withSchedule(CronScheduleBuilder.cronSchedule(sc.getMember())
					.withMisfireHandlingInstructionFireAndProceed() // 设置 Misfire 策略：若错过触发且任务已结束，立即补发一次
					)
					.build();
			scheduler.scheduleJob(job,cronTrigger);
		} catch (SchedulerException e) {
			msg = String.format("定时任务 %d  启动失败：%s ",sc.getId(),e.getMessage());
		}
		return msg;
	}
	
	//清除一个任务
	public void delete(Long id){
		try {
			scheduler.deleteJob(jobKey(id));
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}
	
	//执行任务
	public Object exec(Long id) {

		JobDataMap dataMap = new JobDataMap();
		dataMap.put("id", id);
		try {
			scheduler.triggerJob(jobKey(id), dataMap);
		}catch (SchedulerException e) {
			throw Lang.makeThrow(e.getMessage());
		}
		return null;
	}
	
	public NutMap query(){
		NutMap nm = new NutMap();
		try {
			Set<JobKey> jobKeys = scheduler.getJobKeys(GroupMatcher.anyGroup());
			for(JobKey j : jobKeys){
				//scheduler.getJobDetail(j);
				List<? extends Trigger> t =scheduler.getTriggersOfJob(j);
				Trigger.TriggerState t2 = scheduler.getTriggerState(t.get(0).getKey());
				nm.put(j.getName(),t2.name());
			}
		} catch (SchedulerException e) {
			e.printStackTrace();
		}

		return nm;
	}

	public JobKey jobKey(Long id){
		return JobKey.jobKey(id+"", "group1");
	}

	public void clean(){
		try {
			//true=等待作业完成，false=立即关闭
			scheduler.shutdown(false);
		} catch (SchedulerException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void update(Observable o, Object event) {
		if(!(event instanceof NotifyEvent))
			return;
		NotifyEvent ne = (NotifyEvent) event;
		if(ne.getEventType() != EventEnum.SAVE_AFTER)
			return;
		Object arg = ne.getData();

		if(arg instanceof SysTrigger){
			SysTrigger sysCrontab = (SysTrigger) arg;
			if(sysCrontab.getDeviceId() != -3)
				return;

			delete(sysCrontab.getId());
			if(sysCrontab.getIsRemoved() == 0){
				add(sysCrontab);
			}
		}
	}
	
}
