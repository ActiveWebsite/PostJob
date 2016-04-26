package org.sumdonkus.postJob.domain;

import java.util.Calendar;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
public class JobRunOutcome {
	public final static String EXCEPTION_OCCURRED_RAW_RETURN_CODE = "Exception Occurred";
	
	@Id
	@GeneratedValue
	private Long Id;
	private String rawReturnCode;
	@Lob
	private String rawOutput;
	private JobOutcomeType outcome;
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar startTimestamp;
	@Temporal(TemporalType.TIMESTAMP)
	private Calendar finishTimestamp;
	@ManyToOne
	private JobRun jobRun;
	
	public enum JobOutcomeType{
		Success,
		Error,
		Disabled
	}

	public JobRunOutcome(){}
	
	public JobRunOutcome(JobRun jobRun, JobOutcomeType outcome, String rawOutput, String rawReturnCode){
		this(jobRun,outcome,rawOutput);
		this.rawReturnCode = rawReturnCode;
	}
	
	public JobRunOutcome(JobRun jobRun, JobOutcomeType outcome, String rawOutput){
		this(jobRun,outcome);
		this.rawOutput = rawOutput;
	}
	
	public JobRunOutcome(JobRun jobRun, JobOutcomeType outcome){
		this.setJobRun( jobRun );
		this.outcome = outcome;
		this.finishTimestamp = Calendar.getInstance();
	}
	
	public Long getRuntimeInMilliseconds(){
		if(this.startTimestamp == null ){
			return new Long(-1);
		}
		return this.finishTimestamp.getTimeInMillis() - this.startTimestamp.getTimeInMillis();
	}
	
	public Long getId() {
		return Id;
	}

	protected void setId(Long id) {
		Id = id;
	}

	public String getRawReturnCode() {
		return rawReturnCode;
	}

	public void setRawReturnCode(String rawReturnCode) {
		this.rawReturnCode = rawReturnCode;
	}

	public String getRawOutput() {
		return rawOutput;
	}

	public void setRawOutput(String rawOutput) {
		this.rawOutput = rawOutput;
	}

	public JobOutcomeType getOutcome() {
		return outcome;
	}

	public void setOutcome(JobOutcomeType outcome) {
		this.outcome = outcome;
	}

	public Calendar getStartTimestamp() {
		return startTimestamp;
	}

	public void setStartTimestamp(Calendar startTimestamp) {
		this.startTimestamp = startTimestamp;
	}
	
	public Calendar getFinishTimestamp() {
		return finishTimestamp;
	}

	public void setFinishTimestamp(Calendar finishTimestamp) {
		this.finishTimestamp = finishTimestamp;
	}

	public JobRun getJobRun() {
		return jobRun;
	}

	public void setJobRun(JobRun jobRun) {
		this.jobRun = jobRun;
	}
	
}
