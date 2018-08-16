package com.company.jobServer.executors;

import com.company.jobServer.JobServer;
import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.beans.enums.JobType;
import com.company.jobServer.common.orchestration.DeploymentHandle;
import com.company.jobServer.common.orchestration.ExecutionStatus;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Future;

@Slf4j
public class CompletionChecker implements Runnable {

  protected JobExecution jobExecution;
  protected DeploymentHandle handle;

  public CompletionChecker(JobExecution jobExecution, DeploymentHandle handle) {
    this.jobExecution = jobExecution;
    this.handle = handle;
  }

  @Override
  public void run() {
    try {
      ExecutionStatus status = null;// JobServer.containerService.getDeploymentStatus(handle);

      log.info("status on " + handle.getName() + " is " + status);

      if (status != ExecutionStatus.Pending && status != ExecutionStatus.Running) {
        log.info("handle " + handle.getName() + " has finished");

        Job job = jobExecution.getJob();
        JobType jobType = job.getJobType();

        if (jobType == JobType.CONNECTOR) {
          IJobExecutor jobExecutor = new ConnectorJobExecutor();

          jobExecutor.stop(jobExecution, false);
        } else if (jobType == JobType.MODEL) {
          IJobExecutor jobExecutor = new ModelJobExecutor();

          jobExecutor.stop(jobExecution, false);
        } else if (jobType == JobType.COLLECTION) {
          IJobExecutor jobExecutor = new CollectionJobExecutor();

          //jobExecutor.stop(jobExecution, false);
        } else {
          log.info("Unknown jobType");
        }

        // Cancel the timer now that it is done
        Future future = JobServer.currentTimers.get(jobExecution.getDeploymentHandle());
        if (future != null) {
          future.cancel(true);
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
      // keep going
    }
  }
}
