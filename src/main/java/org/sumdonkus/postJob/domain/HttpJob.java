package org.sumdonkus.postJob.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.sumdonkus.postJob.service.executors.JobExecutor;
import org.sumdonkus.postJob.service.executors.JobExecutorFactory;

@Entity
@DiscriminatorValue("http")
public class HttpJob extends Job {
	private String url;

	public JobExecutor getExecutor(JobExecutorFactory factory){
		return factory.get(this);
	}
	
	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}
}
