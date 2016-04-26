package org.sumdonkus.postJob.service.executors;

import org.sumdonkus.postJob.domain.JobRun;
import org.sumdonkus.postJob.domain.JobRunOutcome;

public interface JobExecutor {
	JobRunOutcome execute(JobRun jobRun);
}
