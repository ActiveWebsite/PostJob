package org.sumdonkus.postJob.service;

import java.util.Calendar;
import java.util.Map;

import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.JobKey;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.stereotype.Service;
import org.sumdonkus.postJob.domain.JobRepository;
import org.sumdonkus.postJob.domain.JobRun;
import org.sumdonkus.postJob.domain.JobRunRepository;

@Service
public class JobRunScheduler {
	private final static String JOB_MAP_ID_KEY = "JobMapID";
	private final static String JOB_GROUP = "PostJob-Job";
	private final static String TRIGGER_GROUP = "PostJob-Trigger";
	
	@Autowired
	private SchedulerFactoryBean schedulerFactoryBean;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private JobRunRepository jobRunRepository;
	
	public void schedule(JobRun jobRun){
		jobRun.setQuartzJobId(JobKey.createUniqueName(JOB_GROUP));
		JobDetail job = JobBuilder.newJob( JobRunSchedulerJob.class )
				.withIdentity( jobRun.getQuartzJobId(), JOB_GROUP )
				.usingJobData(JOB_MAP_ID_KEY,jobRun.getId().toString())
				.build();
		jobRun.setQuartzTriggerId(TriggerKey.createUniqueName(TRIGGER_GROUP));
		Trigger trigger = TriggerBuilder.newTrigger()
				.withIdentity( jobRun.getQuartzTriggerId(), TRIGGER_GROUP )
				.startAt(jobRun.getScheduledTimestamp().getTime())
				.build();
		try {
			schedulerFactoryBean.getScheduler().scheduleJob(job, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException(e);
		}
	}
	
	public JobRun schedule(Long jobId, Integer offsetSeconds, Map<String,String> arguments){
		Calendar datetime = Calendar.getInstance();
		datetime.add(Calendar.SECOND, offsetSeconds);
		JobRun jobRun = new JobRun(jobRepository.findOne(jobId), datetime, arguments);
		jobRunRepository.save(jobRun);
		schedule(jobRun);
		jobRunRepository.save(jobRun);
		return jobRun;
	}
	
	public static class JobRunSchedulerJob implements org.quartz.Job{
		@Autowired
		private JobRunner jobRunner;
		
		public void execute(JobExecutionContext context) throws JobExecutionException {
			jobRunner.runForJobRunId(getJobRunId(context));
		}
		
		private Long getJobRunId(JobExecutionContext context){
			return context.getJobDetail().getJobDataMap().getLong(JOB_MAP_ID_KEY);
		}
	}
}
