package org.sumdonkus.postJob.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class JobEventListener {
	@Id
	@GeneratedValue
	private Long id;
	private JobEventType jobEventType;
	@ManyToOne
	private Job eventJob;
	@ManyToOne
	private Job sourceJob;
	
	public enum JobEventType{
		OnStart,
		OnFinish,
		OnSuccess,
		OnFailure
	}

	public JobEventListener(){}
	
	public JobEventListener(JobEventType jobEventType, Job eventJob, Job sourceJob){
		this.jobEventType = jobEventType;
		this.eventJob = eventJob;
		this.sourceJob = sourceJob;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public JobEventType getJobEventType() {
		return jobEventType;
	}

	public void setJobEventType(JobEventType jobEventType) {
		this.jobEventType = jobEventType;
	}

	public Job getEventJob() {
		return eventJob;
	}

	public void setEventJob(Job eventJob) {
		this.eventJob = eventJob;
	}

	public Job getSourceJob() {
		return sourceJob;
	}

	public void setSourceJob(Job sourceJob) {
		this.sourceJob = sourceJob;
	}	
}
