package org.sumdonkus.postJob.service.executors;

import javax.mail.Message;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.stereotype.Component;
import org.sumdonkus.postJob.domain.EmailJob;
import org.sumdonkus.postJob.domain.JobRun;
import org.sumdonkus.postJob.domain.JobRunOutcome;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Component
public class EmailJobExecutor implements JobExecutor {
	@Autowired
	private JavaMailSender mailSender;
	
	@Autowired
	private TemplateEngine templateEngine;
	
	@Override
	public JobRunOutcome execute(JobRun jobRun) {
		JobRunOutcome.JobOutcomeType outcomeType = null;
		String rawReturnCode = "0";
		String rawReturnString = "Success";
		try{
			mailSender.send(getMessagePreparator(jobRun));
			outcomeType = JobRunOutcome.JobOutcomeType.Success;
		}
		catch(MailException ex){
			outcomeType = JobRunOutcome.JobOutcomeType.Error;
			rawReturnCode = "-1";
			rawReturnString = ex.toString();
		}
		return new JobRunOutcome(jobRun,outcomeType,rawReturnString,rawReturnCode);
	}

	private MimeMessagePreparator getMessagePreparator(final JobRun jobRun){
		EmailJob emailJob = ((EmailJob) jobRun.getJob());
		final StrSubstitutor substitutor = new StrSubstitutor(jobRun.getArguments());
		final InternetAddress toAddress = getEmailAddress(emailJob.getToAddress(), substitutor); 
		final InternetAddress fromAddress = getEmailAddress(emailJob.getFromAddress(), substitutor);
		final String subject = substitutor.replace(emailJob.getSubject());
		final String body = templateEngine.process(getTemplateName(jobRun, substitutor), getTemplateContext(jobRun));
		return new MimeMessagePreparator(){
			public void prepare(MimeMessage mimeMessage) throws Exception{
				mimeMessage.setRecipient(Message.RecipientType.TO, toAddress);
				mimeMessage.setFrom(fromAddress);
				mimeMessage.setSubject(subject);
				mimeMessage.setText(body);
			}
		};
	}
	
	private String getTemplateName(JobRun jobRun, StrSubstitutor substitutor){
		return substitutor.replace(((EmailJob) jobRun.getJob()).getBodyTemplatePath());
	}
	
	private Context getTemplateContext(JobRun jobRun){
		Context templateContext = new Context();
		templateContext.setVariables(jobRun.getArguments());
		return templateContext;
	}
	
	private final InternetAddress getEmailAddress(String emailAddress, StrSubstitutor substitutor){
		try {
			return new InternetAddress(substitutor.replace(emailAddress));
		} catch (AddressException e) {
			throw new RuntimeException(e);
		}
	}
}
