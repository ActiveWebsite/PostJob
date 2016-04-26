package org.sumdonkus.postJob.service.executors;

public class JobResourceInaccessibleException extends RuntimeException {
	private static final long serialVersionUID = 1L;
	
	public JobResourceInaccessibleException(Throwable e){
		super(e);
	}
}
