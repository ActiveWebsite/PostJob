package org.sumdonkus.postJob.web;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.sumdonkus.postJob.domain.JobRepository;
import org.sumdonkus.postJob.domain.JobRun;
import org.sumdonkus.postJob.domain.JobRunOutcome;
import org.sumdonkus.postJob.domain.JobRunOutcomeRepository;
import org.sumdonkus.postJob.domain.JobRunRepository;
import org.sumdonkus.postJob.service.JobRunOutcomeStats;
import org.sumdonkus.postJob.service.JobRunScheduler;
import org.sumdonkus.postJob.service.JobRunner;

@Controller
public class JobController {
	private static final Integer PAST_RUN_PAGE_SIZE = 20;
	private static final Integer FIRST_PAGE_NUMBER = 0;
	private static final Integer NUMBER_OF_OUTCOMES_PER_RUN = 10;
	
	@Autowired
	private JobRepository jobRepository;
	
	@Autowired
	private JobRunRepository jobRunRepository;
	
	@Autowired
	private JobRunner jobRunner;
	
	@Autowired
	private JobRunScheduler jobRunScheduler;
	
	@Autowired
	private JobRunOutcomeRepository jobRunOutcomeRepository;
	
	@Autowired
	private JobRunOutcomeStats jobRunOutcomeStats;
	
	@RequestMapping(path="/")
	public String welcome(Model model){
		model.addAttribute("jobs", jobRepository.findAll());
		return "index";
	}
	
	@RequestMapping(path="/job/outcome/{outcomeId}")
	public String showOutcome(Model model, @PathVariable Long outcomeId){
		model.addAttribute("outcome", jobRunOutcomeRepository.findOne(outcomeId));
		return "outcome";
	}
	
	@RequestMapping(path="/job/pastRuns/{jobId}")
	public String pastRuns(
			Model model, 
			@PathVariable Long jobId, 
			@RequestParam(name="pageNumber",required=false,defaultValue="0") Integer pageNumber){
		Pageable pageable = new PageRequest(pageNumber,PAST_RUN_PAGE_SIZE);
		List<JobRun> runs = jobRunRepository.findByJobOrderByScheduledTimestampDesc(jobRepository.findOne(jobId), pageable);
		model.addAttribute("runs", runs);
		model.addAttribute("outcomesForRun", getOutcomesForRuns(runs));
		model.addAttribute("pageNumber", pageNumber);
		return "jobRuns";
	}
	
	private Map<Long,List<JobRunOutcome>> getOutcomesForRuns(List<JobRun> runs){
		Map<Long,List<JobRunOutcome>> outcomesPerRun = new HashMap<Long,List<JobRunOutcome>>();
		Pageable pageable = new PageRequest(FIRST_PAGE_NUMBER,NUMBER_OF_OUTCOMES_PER_RUN);
		for(JobRun run : runs){
			outcomesPerRun.put(run.getId(), jobRunOutcomeRepository.findByJobRunOrderByStartTimestampDesc(run,pageable));
		}
		return outcomesPerRun;
	}
	
	@RequestMapping(path="/job/jobStats", produces="application/json")
	@ResponseBody
	public Map<JobRunOutcome.JobOutcomeType,Long> getJobStats(){
		Calendar startDate = Calendar.getInstance();
		//startDate.add(Calendar.HOUR, -48);
		Calendar endDate = Calendar.getInstance();
		endDate.add(Calendar.HOUR, 1);
		return jobRunOutcomeStats.getJobRunOutcomeStatsPerOutcomeType(startDate, endDate);
	}
}