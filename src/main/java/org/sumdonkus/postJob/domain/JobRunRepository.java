package org.sumdonkus.postJob.domain;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;

public interface JobRunRepository extends PagingAndSortingRepository<JobRun, Long> {
	List<JobRun> findByJobOrderByScheduledTimestampDesc(Job job, Pageable pageable);
}
