package org.sumdonkus.postJob.domain;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import org.sumdonkus.postJob.service.executors.JobExecutor;
import org.sumdonkus.postJob.service.executors.JobExecutorFactory;

@Entity
@DiscriminatorValue("email")
public class EmailJob extends Job {
	private String toAddress;
	private String fromAddress;
	private String subject;
	private String bodyTemplatePath;

	@Override
	public JobExecutor getExecutor(JobExecutorFactory factory) {
		return factory.get(this);
	}

	public String getToAddress() {
		return toAddress;
	}

	public void setToAddress(String toAddress) {
		this.toAddress = toAddress;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getBodyTemplatePath() {
		return bodyTemplatePath;
	}

	public void setBodyTemplatePath(String bodyTemplatePath) {
		this.bodyTemplatePath = bodyTemplatePath;
	}
}
