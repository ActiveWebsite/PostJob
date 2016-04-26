package org.sumdonkus.postJob.service.executors;

public class InvalidJobConfigurationException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public InvalidJobConfigurationException(Throwable e){
		super(e);
	}
}
