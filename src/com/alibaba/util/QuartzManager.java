package com.alibaba.util;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobDataMap;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Component;

import com.alibaba.model.ScheduleJob;

/**
 * 定时任务管理类
 *
 * @author
 */
@Component
public class QuartzManager {

	private final static String PARA_OBJECT = "scheduleJob";

	@Autowired
	private SchedulerFactoryBean schedulerFactory;

	private static final Logger logger = Logger.getLogger(QuartzManager.class);

	/**
	 * 添加一个定时任务
	 * 
	 * @param job
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void addJob(InspectionScheduleJob job) {
		try {
			Scheduler sched = schedulerFactory.getScheduler();

			TriggerKey triggerKey = TriggerKey.triggerKey(job.getName(), job.getGroup());
			CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
			if (trigger != null) {
				modifyJobTime(job);
				return;
			}

			// 任务名，任务组，任务执行类
			Class jobClass = Class.forName(job.getClassPath());
			JobDetail jobDetail = JobBuilder.newJob(jobClass).withIdentity(job.getName(), job.getGroup()).build();
			jobDetail.getJobDataMap().put(PARA_OBJECT, job.getParam());
			// 触发器
			TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
			// 触发器名，触发器组
			triggerBuilder.withIdentity(job.getName(), job.getGroup());
			triggerBuilder.startNow();
			// 触发器时间设定
			triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExp()));
			// 创建Trigger对象
			trigger = (CronTrigger) triggerBuilder.build();
			// 调度容器设置JobDetail和Trigger
			sched.scheduleJob(jobDetail, trigger);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 修改一个任务的触发时间
	 * 
	 * @param job
	 * @return
	 */
	public boolean modifyJobTime(InspectionScheduleJob job) {
		try {
			Scheduler sched = schedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(job.getName(), job.getGroup());
			CronTrigger trigger = (CronTrigger) sched.getTrigger(triggerKey);
			if (trigger == null) {
				addJob(job);
				return true;
			}

			String oldCron = trigger.getCronExpression();
			if (!oldCron.equalsIgnoreCase(job.getCronExp())) {
				// 触发器
				TriggerBuilder<Trigger> triggerBuilder = TriggerBuilder.newTrigger();
				// 触发器名,触发器组
				triggerBuilder.withIdentity(job.getName(), job.getGroup());
				triggerBuilder.startNow();
				// 触发器时间设定
				triggerBuilder.withSchedule(CronScheduleBuilder.cronSchedule(job.getCronExp()));
				// 创建Trigger对象
				trigger = (CronTrigger) triggerBuilder.build();
				// 修改一个任务的触发时间
				sched.rescheduleJob(triggerKey, trigger);
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		return true;
	}

	/**
	 * 移除一个任务
	 * 
	 * @param job
	 */
	public void removeJob(InspectionScheduleJob job) {
		try {
			Scheduler sched = schedulerFactory.getScheduler();
			TriggerKey triggerKey = TriggerKey.triggerKey(job.getName(), job.getGroup());
			sched.pauseTrigger(triggerKey);// 停止触发器
			sched.unscheduleJob(triggerKey);// 移除触发器
			sched.deleteJob(JobKey.jobKey(job.getName(), job.getGroup()));// 删除任务
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 暂停一个任务
	 * 
	 * @param job
	 */
	public void pauseJob(InspectionScheduleJob job) {
		try {
			Scheduler sched = schedulerFactory.getScheduler();
			sched.pauseJob(JobKey.jobKey(job.getName(), job.getGroup()));// 暂停任务
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 恢复任务
	 * 
	 * @param job
	 */
	public void resumeJob(InspectionScheduleJob job) {
		try {
			Scheduler sched = schedulerFactory.getScheduler();
			sched.resumeJob(JobKey.jobKey(job.getName(), job.getGroup()));// 恢复任务
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 删除一个任务
	 * 
	 * @param job
	 */
	public void deleteJob(InspectionScheduleJob job) {
		try {
			Scheduler sched = schedulerFactory.getScheduler();
			sched.deleteJob(JobKey.jobKey(job.getName(), job.getGroup()));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Description:启动所有定时任务
	 */
	public void startJobs() {
		try {
			Scheduler sched = schedulerFactory.getScheduler();
			sched.start();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * @Description:关闭所有定时任务
	 */
	public void shutdownJobs() {
		try {
			Scheduler sched = schedulerFactory.getScheduler();
			if (!sched.isShutdown()) {
				sched.shutdown();
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 立即执行一个定时任务
	 * 
	 * @param job
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public boolean executeJob(InspectionScheduleJob job) {
		Class clazz = null;
		try {
			clazz = Class.forName(job.getClassPath());
			return this.executeJob(job.getName(), job.getGroup(), job.getName(), job.getGroup(), clazz, job.getParam());
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
		}
		return false;
	}

	/**
	 * @Description: 立即执行一个定时任务
	 * 
	 * @param jobName
	 *            任务名
	 * @param jobGroupName
	 *            任务组名
	 * @param triggerName
	 *            触发器名
	 * @param triggerGroupName
	 *            触发器组名
	 * @param jobClass
	 *            任务
	 * @throws SchedulerException
	 */

	public boolean executeJob(String jobName, String jobGroupName, String triggerName, String triggerGroupName,
			Class<? extends Job> jobClass, String paramObj) {
		try {
			Scheduler sched = schedulerFactory.getScheduler();

			// 任务名，任务组，任务执行类
			JobKey jobKey = new JobKey(jobName, jobGroupName);
			JobDataMap data = new JobDataMap();
			data.put(PARA_OBJECT, paramObj);
			sched.triggerJob(jobKey, data);
			return true;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
