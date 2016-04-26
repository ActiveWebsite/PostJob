package org.sumdonkus.postJob.service;

import java.util.Calendar;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sumdonkus.postJob.domain.Job;
import org.sumdonkus.postJob.domain.JobEventListener;
import org.sumdonkus.postJob.domain.JobRepository;
import org.sumdonkus.postJob.domain.JobRun;
import org.sumdonkus.postJob.domain.JobRunOutcome;
import org.sumdonkus.postJob.domain.JobRunOutcomeRepository;
import org.sumdonkus.postJob.domain.JobRunRepository;
import org.sumdonkus.postJob.service.executors.JobExecutorFactory;

@Service
public class JobRunner {
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private JobRunRepository jobRunRepository;
	
	@Autowired
	private JobRunOutcomeRepository jobRunOutcomeRepository;
	
	@Autowired
	private JobExecutorFactory jobExecutorFactory;
	
	@Autowired
	private JobEventListenerRunner jobEventListenerRunner;
	
	public JobRunOutcome run(Job job, Map<String,String> arguments){
		JobRun jobRun = new JobRun(job, Calendar.getInstance(), arguments);
		jobRunRepository.save(jobRun);
		return run(jobRun);
	}
	public JobRunOutcome run(JobRun jobRun){
		Calendar startTimestamp = Calendar.getInstance();
		JobRunOutcome outcome;
		if(jobRun.isEnabled()){
			jobEventListenerRunner.run(JobEventListener.JobEventType.OnStart, jobRun);
			try{
				outcome = jobRun.run(jobExecutorFactory);
			}
			catch(RuntimeException e){
				outcome = new JobRunOutcome(
					jobRun,
					JobRunOutcome.JobOutcomeType.Error,
					e.toString(),
					JobRunOutcome.EXCEPTION_OCCURRED_RAW_RETURN_CODE
				);
			}
		}
		else{
			outcome = new JobRunOutcome(jobRun,JobRunOutcome.JobOutcomeType.Disabled);
		}
		outcome.setStartTimestamp(startTimestamp);
		jobRunOutcomeRepository.save(outcome);
		JobRunOutcome.JobOutcomeType jobOutcomeType = outcome.getOutcome();
		if(jobOutcomeType != JobRunOutcome.JobOutcomeType.Disabled){
			jobEventListenerRunner.run(JobEventListener.JobEventType.OnFinish, outcome);
		}
		if(jobOutcomeType == JobRunOutcome.JobOutcomeType.Success){
			jobEventListenerRunner.run(JobEventListener.JobEventType.OnSuccess, outcome);
		}
		else if(jobOutcomeType == JobRunOutcome.JobOutcomeType.Error){
			jobEventListenerRunner.run(JobEventListener.JobEventType.OnFailure, outcome);
		}
		return outcome;
	}

	public JobRunOutcome runForJobId(Long jobId, Map<String,String> arguments){
		return run(jobRepository.findOne(jobId), arguments);
	}
	
	public JobRunOutcome runForJobRunId(Long jobRunId){
		return run(jobRunRepository.findOne(jobRunId));
	}
	
	public JobRunOutcome forceRunForJobRunId(Long jobRunId){
		JobRun jobRun = jobRunRepository.findOne(jobRunId);
		jobRun.setJobState(JobRun.JobState.Enabled);
		return run(jobRun);
	}
	
	public JobRun cancelJobRun(JobRun jobRun){
		jobRun.setJobState(JobRun.JobState.Disabled);
		jobRunRepository.save(jobRun);
		return jobRun;
	}
	
	public JobRun cancelJobRun(Long jobRunId){
		return cancelJobRun(jobRunRepository.findOne(jobRunId));
	}
}