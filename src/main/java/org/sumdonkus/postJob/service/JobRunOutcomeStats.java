package org.sumdonkus.postJob.service;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.sumdonkus.postJob.domain.JobRunOutcome;
import org.sumdonkus.postJob.domain.JobRunOutcomeRepository;

@Service
public class JobRunOutcomeStats {
	@Autowired
	private JobRunOutcomeRepository jobRunOutcomeRepository;
	
	public Map<JobRunOutcome.JobOutcomeType,Long> getJobRunOutcomeStatsPerOutcomeType(Calendar startDate, Calendar endDate){
		Map<JobRunOutcome.JobOutcomeType,Long> results = new HashMap<JobRunOutcome.JobOutcomeType,Long>();
		List<Object[]> rawResults = jobRunOutcomeRepository.findCountPerTypeByStartTimestampRange(startDate, endDate);
		for(Object[] row : rawResults){
			results.put(
				(JobRunOutcome.JobOutcomeType) row[1],
				(Long) row[0]);
		}
		return results;
	}
}
