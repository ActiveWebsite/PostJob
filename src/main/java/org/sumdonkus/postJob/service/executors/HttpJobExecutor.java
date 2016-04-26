package org.sumdonkus.postJob.service.executors;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.stereotype.Component;
import org.sumdonkus.postJob.domain.HttpJob;
import org.sumdonkus.postJob.domain.JobRun;
import org.sumdonkus.postJob.domain.JobRunOutcome;

@Component
public class HttpJobExecutor implements JobExecutor {
	private final static Integer CONNECTION_TIMEOUT = 60000;
	private final static String USER_AGENT = "PostJob/alphaAsFuck";
	private final static Boolean FOLLOW_REDIRECTS = true;
	private final static Boolean DO_OUTPUT = true;
	
	@Override
	public JobRunOutcome execute(JobRun jobRun) {
		String url = this.getUrl(jobRun);
		HttpURLConnection httpConnection = this.openHttpConnection(url);
		this.configureHttpConnection(httpConnection);
		JobRunOutcome outcome = this.getJobRunOutcomeForHttpConnection(httpConnection,jobRun);
		return outcome;
	}

	private String getUrl(JobRun jobRun){
		StrSubstitutor substitutor = new StrSubstitutor( jobRun.getArguments() );
		return substitutor.replace(((HttpJob) jobRun.getJob()).getUrl());
	}
	
	private HttpURLConnection openHttpConnection(String url){
		try {
			return (HttpURLConnection) (new URL(url)).openConnection();
		} catch (MalformedURLException e) {
			throw new InvalidJobConfigurationException(e);
		} catch (IOException e) {
			throw new JobResourceInaccessibleException(e);
		}
	}
	
	private void configureHttpConnection(HttpURLConnection httpConnection){
		httpConnection.setInstanceFollowRedirects(FOLLOW_REDIRECTS);
		httpConnection.setDoOutput(DO_OUTPUT);
		httpConnection.setConnectTimeout(CONNECTION_TIMEOUT);
		httpConnection.setReadTimeout(CONNECTION_TIMEOUT);
		httpConnection.addRequestProperty("User-Agent", USER_AGENT);
	}
	
	private JobRunOutcome getJobRunOutcomeForHttpConnection(HttpURLConnection httpConnection, JobRun jobRun){
		Integer responseCode;
		try {
			responseCode = httpConnection.getResponseCode();
		} catch (IOException e) {
			throw new JobResourceInaccessibleException(e);
		}
		return new JobRunOutcome(
			jobRun,
			responseCode == 200 ? JobRunOutcome.JobOutcomeType.Success : JobRunOutcome.JobOutcomeType.Error, 
			this.getBody(httpConnection), 
			responseCode.toString()
		);
	}
	
	private String getBody(HttpURLConnection httpConnection){
		StringBuilder body = new StringBuilder();
		BufferedReader reader;
		try {
			reader = new BufferedReader(new InputStreamReader(httpConnection.getInputStream()));
		} catch (IOException e) {
			throw new JobResourceInaccessibleException(e);
		}
		String line;
		try {
			while( (line = reader.readLine()) != null ){
				body.append(line);
			}
		} catch (IOException e) {
			throw new JobResourceInaccessibleException(e);
		}
		return body.toString();//.substring(0, 50);
	}
}
