package org.sumdonkus.postJob.domain;

import org.springframework.data.repository.PagingAndSortingRepository;

public interface EmailJobRepository extends PagingAndSortingRepository<EmailJob, Long> {

}
