package org.sumdonkus.postJob.service.executors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.sumdonkus.postJob.domain.EmailJob;
import org.sumdonkus.postJob.domain.HttpJob;
import org.sumdonkus.postJob.domain.Job;

@Component
public class JobExecutorFactory {
	@Autowired
	private EmailJobExecutor emailJobExecutor;
	
	@Autowired
	private HttpJobExecutor httpJobExecutor;
	
	public JobExecutor get(Job job){
		throw new UnknownJobTypeException(job.getClass().getName()+" is unknown to the executor");
	}
	public JobExecutor get(EmailJob job){
		return emailJobExecutor;
	}
	public JobExecutor get(HttpJob job){
		return httpJobExecutor;
	}
	
	public class UnknownJobTypeException extends RuntimeException{
		private static final long serialVersionUID = 1L;

		public UnknownJobTypeException(String message){ super(message); }
	}
}