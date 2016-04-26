package org.sumdonkus.postJob.domain;

import java.util.Calendar;
import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface JobRunOutcomeRepository extends PagingAndSortingRepository<JobRunOutcome, Long> {
	List<JobRunOutcome> findByJobRunOrderByStartTimestampDesc(JobRun jobRun, Pageable pageable);
	@Query(value="select count(jro), jro.outcome from JobRunOutcome jro where jro.startTimestamp between ?1 and ?2 group by jro.outcome",
			countQuery="select count(1) from JobRunOutcome jro where jro.startTimestamp between ?1 and ?2 group by jro.outcome")
	List<Object[]> findCountPerTypeByStartTimestampRange(Calendar startDate, Calendar endDate);
}
