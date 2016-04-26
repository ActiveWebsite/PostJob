package org.sumdonkus.postJob.domain;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.MapKeyColumn;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.sumdonkus.postJob.service.executors.JobExecutorFactory;

@Entity
public class JobRun {
	private final static Map<String,String> EmptyArguments = new HashMap<String,String>();
	
	@Id
	@GeneratedValue
	private Long Id;
	@ManyToOne
	private Job job;
	@ElementCollection(fetch=FetchType.EAGER)
	@MapKeyColumn(name="name")
	@Column(name="value")
	@Lob
	@CollectionTable(name="job_run_arguments", joinColumns=@JoinColumn(name="job_run_id"))
	private Map<String,String> arguments;
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar scheduledTimestamp;
	private String quartzJobId;
	private String quartzTriggerId;
	private JobState jobState;
	
	public enum JobState{
		Disabled,
		Enabled
	}
	
	public JobRun(){}
	
	public JobRun(Job job, Calendar scheduledTimestamp, Map<String,String> arguments){
		this(job,scheduledTimestamp);
		this.arguments = arguments;
	}
	
	public JobRun(Job job, Calendar scheduledTimestamp){
		this.job = job;
		this.scheduledTimestamp = scheduledTimestamp;
		this.arguments = EmptyArguments;
		this.jobState = JobState.Enabled;
	}
	
	public JobRunOutcome run(JobExecutorFactory factory){
		return job.getExecutor(factory).execute(this);
	}
	
	public Boolean isEnabled(){
		return jobState.equals(JobState.Enabled);
	}
	
	public String getName(){
		String template = job.getJobRunDescriptionTemplate();
		if( template == null || template.isEmpty() ){
			return job.getName();
		}
		else{
			StrSubstitutor substitutor = new StrSubstitutor(arguments);
			return substitutor.replace(template);
		}
	}
	
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public Job getJob() {
		return job;
	}
	public void setJob(Job job) {
		this.job = job;
	}
	public Map<String, String> getArguments() {
		return arguments;
	}
	public void setArguments(Map<String, String> arguments) {
		this.arguments = arguments;
	}
	public Calendar getScheduledTimestamp() {
		return scheduledTimestamp;
	}
	public void setScheduledTimestamp(Calendar scheduledTimestamp) {
		this.scheduledTimestamp = scheduledTimestamp;
	}
	public String getQuartzJobId() {
		return quartzJobId;
	}
	public void setQuartzJobId(String quartzJobId) {
		this.quartzJobId = quartzJobId;
	}
	public String getQuartzTriggerId() {
		return quartzTriggerId;
	}
	public void setQuartzTriggerId(String quartzTriggerId) {
		this.quartzTriggerId = quartzTriggerId;
	}
	public JobState getJobState() {
		return jobState;
	}
	public void setJobState(JobState jobState) {
		this.jobState = jobState;
	}
}
