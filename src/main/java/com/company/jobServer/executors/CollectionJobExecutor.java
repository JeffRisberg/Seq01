package com.company.jobServer.executors;

import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.beans.enums.JobType;
import com.company.jobServer.services.NextJobService;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;

import java.util.List;

@Slf4j
public class CollectionJobExecutor extends BaseJobExecutor {

  @Override
  public JobExecution start(Job rootJob, JobExecution parentExecution, JSONObject envVars) {

    try {
      List<JobExecution> jeList = jobExecutionController.getByJobId(rootJob.getId());
      JobExecution jobExecution = null;

      if (jeList.size() == 0) {
        jobExecution = new JobExecution();
        jobExecution.setJob(rootJob);
        jobExecution.setJobId(rootJob.getId());
        jobExecution.setParentExecution(parentExecution);
        jobExecution.setParentExecutionId(parentExecution != null ? parentExecution.getId() : null);
        jobExecutionController.create(jobExecution);
      } else {
        jobExecution = jeList.get(0);
      }

      NextJobService nextJobService = new NextJobService(jobExecution);

      List<Job> nextJobs = nextJobService.getNextJobs();

      for (Job nextJob : nextJobs) {
        System.out.println("starting job " + nextJob.getName());

        if (nextJob.getJobType() == JobType.CONNECTOR) {
          ConnectorJobExecutor jobExecutor = new ConnectorJobExecutor();

          jobExecutor.start(nextJob, jobExecution, envVars);
        }
      }
      return jobExecution;
    }
    catch (Exception e) {
      e.printStackTrace();
      return null;
    }
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
