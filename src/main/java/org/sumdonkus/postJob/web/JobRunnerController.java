package org.sumdonkus.postJob.web;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sumdonkus.postJob.domain.JobRun;
import org.sumdonkus.postJob.domain.JobRunOutcome;
import org.sumdonkus.postJob.service.JobRunScheduler;
import org.sumdonkus.postJob.service.JobRunner;

@Controller
public class JobRunnerController {
	@Autowired
	private JobRunner jobRunner;
	
	@Autowired
	private JobRunScheduler jobRunScheduler;
	
	@RequestMapping(path="/job/execute/{jobId}", produces="text/html")
	public String executeForHtml(Model model, @PathVariable Long jobId, HttpServletRequest request){
		JobRunOutcome outcome = executeForRequest(jobId, request);
		return "redirect:/job/outcome/"+outcome.getId();
	}
	
	@RequestMapping(path="/job/execute/{jobId}", produces="application/json")
	@ResponseBody
	public JobRunOutcome executeForJson(Model model, @PathVariable Long jobId, HttpServletRequest request){
		JobRunOutcome outcome = executeForRequest(jobId, request);
		return outcome;
	}
	
	private JobRunOutcome executeForRequest(Long jobId, HttpServletRequest request){
		return jobRunner.runForJobId(jobId, getArgumentsFromRequest(request));
	}
	
	private Map<String,String> getArgumentsFromRequest(HttpServletRequest request){
		Map<String,String> arguments = new HashMap<String,String>();
		for(String key : request.getParameterMap().keySet()){
			//if(!key.equals("offsetSeconds")){
				arguments.put(key, request.getParameter(key));
			//}
		}
		return arguments;
	}
	
	@RequestMapping(path="/job/schedule/{jobId}/{offsetSeconds}", produces="text/html")
	public String scheduleForHtml(Model model, @PathVariable Long jobId, @PathVariable Integer offsetSeconds, HttpServletRequest request){
		scheduleForRequest(jobId, offsetSeconds, request);
		return "redirect:/job/pastRuns/"+jobId+"?scheduled=1";
	}
	
	@RequestMapping(path="/job/schedule/{jobId}/{offsetSeconds}", produces="application/json")
	@ResponseBody
	public JobRun scheduleForJson(Model model, @PathVariable Long jobId, @PathVariable Integer offsetSeconds, HttpServletRequest request){
		return scheduleForRequest(jobId, offsetSeconds, request);
	}
	
	private JobRun scheduleForRequest(Long jobId, Integer offsetSeconds, HttpServletRequest request){
		return jobRunScheduler.schedule(jobId, offsetSeconds, getArgumentsFromRequest(request));
	}
	
	@RequestMapping(path="/job/cancel/{jobRunId}", produces="text/html")
	public String cancelJobRunForHtml(@PathVariable Long jobRunId){
		JobRun jobRun = cancelJobRunForRequest(jobRunId);
		return "redirect:/job/pastRuns/"+jobRun.getJob().getId()+"?cancelled=1";
	}
	
	@RequestMapping(path="/job/cancel/{jobRunId}", produces="application/json")
	@ResponseBody
	public JobRun cancelJobRunForJson(@PathVariable Long jobRunId){
		return cancelJobRunForRequest(jobRunId);
	}
	
	private JobRun cancelJobRunForRequest(Long jobRunId){
		return jobRunner.cancelJobRun(jobRunId);
	}
	
	@RequestMapping(path="/job/trigger-jobrun/{jobRunId}", produces="text/html")
	public String triggerJobRunForHtml(@PathVariable Long jobRunId){
		JobRunOutcome jobRunOutcome = triggerJobRunForRequest(jobRunId);
		return "redirect:/job/pastRuns/"+jobRunOutcome.getJobRun().getJob().getId()+"?triggered=1";
	}
	
	@RequestMapping(path="/job/trigger-jobrun/{jobRunId}", produces="application/json")
	@ResponseBody
	public JobRunOutcome triggerJobRunForJson(@PathVariable Long jobRunId){
		return triggerJobRunForRequest(jobRunId);
	}
	
	private JobRunOutcome triggerJobRunForRequest(Long jobRunId){
		return jobRunner.forceRunForJobRunId(jobRunId);
	}
}
