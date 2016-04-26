package org.sumdonkus.postJob.domain;

import java.util.Set;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.OneToMany;

import org.sumdonkus.postJob.service.executors.JobExecutor;
import org.sumdonkus.postJob.service.executors.JobExecutorFactory;

@Entity
@Inheritance
@DiscriminatorColumn(name="type")
abstract public class Job {
	@Id
	@GeneratedValue
	private Long id;
	private String name;
	private String jobRunDescriptionTemplate;
	@OneToMany(fetch=FetchType.EAGER,mappedBy="sourceJob")
	Set<JobEventListener> listeners;
	
	abstract public JobExecutor getExecutor(JobExecutorFactory factory);
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getJobRunDescriptionTemplate(){
		return jobRunDescriptionTemplate;
	}
	public void setJobRunDescriptionTemplate(String jobRunDescriptionTemplate){
		this.jobRunDescriptionTemplate = jobRunDescriptionTemplate;
	}
	public Set<JobEventListener> getListeners() {
		return listeners;
	}
	public void setListeners(Set<JobEventListener> listeners) {
		this.listeners = listeners;
	}
	public void addListener(JobEventListener listener){
		this.getListeners().add(listener);
	}
}
