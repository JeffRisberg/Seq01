package com.company.jobServer.executors;

import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.common.orchestration.DeployableObject;

public interface IJobExecutor {

  public JobExecution start(Job job);

  public void setupEnv(Job job, DeployableObject deployableObject);

  public void stop(JobExecution jobExecution, boolean force);

  public void destroy(JobExecution jobExecution);
}
