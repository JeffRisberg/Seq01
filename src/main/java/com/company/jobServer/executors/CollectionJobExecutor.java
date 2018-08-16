package com.company.jobServer.executors;

import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;

@Slf4j
public class CollectionJobExecutor extends BaseJobExecutor {

  @Override
  public JobExecution start(Job job, JSONObject envVars) {
    return super.start(job, envVars);
  }

  @Override
  public void stop(JobExecution jobExecution, boolean force) {
    super.stop(jobExecution, force);
  }

  @Override
  public void destroy(JobExecution jobExecution) {
    super.destroy(jobExecution);
  }
}
