package org.sumdonkus.postJob.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.sumdonkus.postJob.domain.EmailJob;
import org.sumdonkus.postJob.domain.HttpJob;
import org.sumdonkus.postJob.domain.Job;
import org.sumdonkus.postJob.domain.JobEventListener;
import org.sumdonkus.postJob.domain.JobEventListenerRepository;
import org.sumdonkus.postJob.domain.JobRepository;

@Controller
public class JobConfigurationController {
	private static final String TYPE_ARGUMENT_HTTP = "http";
	private static final String TYPE_ARGUMENT_EMAIL = "email";
	
	@Autowired
	private JobRepository jobRepository;
	@Autowired
	private JobEventListenerRepository jobEventListenerRepository;
	
	@RequestMapping(path="/job/new")
	public String newForm(Model model, @RequestParam("type") String type){
		Job job = getDefaultJobByTypeArgument(type);
		model.addAttribute("job", job);
		return getViewNameForJobType(job);
	}
	
	private Job getDefaultJobByTypeArgument(String type){
		Job defaultJob;
		if(type.equalsIgnoreCase(TYPE_ARGUMENT_HTTP)){
			defaultJob = new HttpJob();
			((HttpJob)defaultJob).setUrl("http://${domain}/");
		}
		else{
			defaultJob = new EmailJob();
			((EmailJob) defaultJob).setToAddress("dev@localhost");
			((EmailJob) defaultJob).setFromAddress("dev@localhost");
			((EmailJob) defaultJob).setSubject("Test Subject");
			((EmailJob) defaultJob).setBodyTemplatePath("outcome.email");
		}
		defaultJob.setName("Test " + type);
		return defaultJob;
	}
	
	private String getViewNameForJobType(Job job){
		if(job instanceof HttpJob){
			return "jobForm-http";
		}
		else if(job instanceof EmailJob){
			return "jobForm-email";
		}
		else{
			throw new RuntimeException("View does not exist for job type " + job.getClass().getCanonicalName());
		}
	}
	
	@RequestMapping(path="/job/edit/{jobId}")
	public String editForm(Model model, @PathVariable Long jobId){
		Job job = findJobById(jobId);
		model.addAttribute("job", job);
		return getViewNameForJobType(job);
	}
	
	private Job findJobById(Long jobId){
		return jobRepository.findOne(jobId);
	}
	
	@RequestMapping(path="/job/save/http",method=RequestMethod.POST)
	public String save(@ModelAttribute HttpJob job){
		return saveJob(job);
	}
	
	private String saveJob(Job job){
		jobRepository.save( job );
		return "redirect:/?"+job.getId();
	}
	
	@RequestMapping(path="/job/save/email",method=RequestMethod.POST)
	public String save(@ModelAttribute EmailJob job){
		return saveJob(job);
	}
	
	@RequestMapping(path="/job/addListener/{jobId}")
	public String addListener(
			@PathVariable Long jobId, 
			@RequestParam("listenerType") JobEventListener.JobEventType listenerType, 
			@RequestParam("listenerJobId") Long listenerJobId){
		JobEventListener listener = new JobEventListener(listenerType, findJobById(listenerJobId), findJobById(jobId));
		jobEventListenerRepository.save(listener);
		return "redirect:/job/edit/"+listener.getSourceJob().getId();
	}
	
	@RequestMapping(path="/job/deleteListener/{jobListenerId}")
	public String deleteListener(@PathVariable Long jobListenerId){
		JobEventListener listener = jobEventListenerRepository.findOne(jobListenerId);
		System.out.println(listener);
		Long jobId = listener.getSourceJob().getId();
		jobEventListenerRepository.delete(listener);
		return "redirect:/job/edit/"+jobId;
	}
}
