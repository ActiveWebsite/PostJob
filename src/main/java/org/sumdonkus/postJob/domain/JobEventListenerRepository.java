package org.sumdonkus.postJob.domain;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface JobEventListenerRepository extends PagingAndSortingRepository<JobEventListener, Long> {

}
