package org.sumdonkus.postJob.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sumdonkus.postJob.domain.JobEventListener;
import org.sumdonkus.postJob.domain.JobEventListener.JobEventType;
import org.sumdonkus.postJob.domain.JobRun;
import org.sumdonkus.postJob.domain.JobRunOutcome;

@Service
public class JobEventListenerRunner {
	public static final String JOB_NAME_KEY = "jobName";
	public static final String RUN_TIME_KEY = "scheduledTime";
	public static final String RUN_ARGUMENTS_KEY = "arguments";
	public static final String OUTCOME_TYPE_KEY = "outcome";
	public static final String OUTCOME_CODE_KEY = "outcomeCode";
	public static final String OUTCOME_OUTPUT_KEY = "outcomeOutput";
	
	@Autowired
	private JobRunner jobRunner;
	
	public void run(JobEventType jobEventType, JobRun jobRun){
		for(Object listener : jobRun.getJob().getListeners()){
			if(((JobEventListener)listener).getJobEventType().equals(jobEventType)){
				run((JobEventListener)listener, jobRun);
			}
		}
	}
	
	public void run(JobEventListener jobEventListener, JobRun job){
		jobRunner.run(jobEventListener.getEventJob(), toMap(job));
	}
	
	private Map<String,String> toMap(JobRun jobRun){
		Map<String,String> map = new HashMap<String,String>();
		map.put(JOB_NAME_KEY, jobRun.getJob().getName());
		map.put(RUN_TIME_KEY, jobRun.getScheduledTimestamp().getTime().toString());
		map.put(RUN_ARGUMENTS_KEY, jobRun.getArguments().toString());
		return map;
	}
	
	public void run(JobEventType jobEventType, JobRunOutcome jobRunOutcome){
		for(Object listener : jobRunOutcome.getJobRun().getJob().getListeners()){
			if(((JobEventListener)listener).getJobEventType().equals(jobEventType)){
				run((JobEventListener)listener, jobRunOutcome);
			}
		}
	}
	
	public void run(JobEventListener jobEventListener, JobRunOutcome jobRunOutcome){
		jobRunner.run(jobEventListener.getEventJob(), toMap(jobRunOutcome));
	}
	
	private Map<String,String> toMap(JobRunOutcome jobRunOutcome){
		Map<String,String> map = toMap(jobRunOutcome.getJobRun());
		map.put(OUTCOME_TYPE_KEY, jobRunOutcome.getOutcome().toString());
		map.put(OUTCOME_CODE_KEY, jobRunOutcome.getRawReturnCode());
		map.put(OUTCOME_OUTPUT_KEY, jobRunOutcome.getRawOutput());
		return map;
	}
}
