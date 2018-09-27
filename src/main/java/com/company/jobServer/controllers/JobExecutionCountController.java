package com.company.jobServer.controllers;

import com.company.jobServer.beans.JobExecutionCount;
import com.company.jobServer.common.job.JobServiceType;
import com.company.jobServer.controllers.DAO.JobExecutionCountDAO;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class JobExecutionCountController extends BaseController {
  private static JobExecutionCountDAO dao = new JobExecutionCountDAO();

  public JobExecutionCount create(JobExecutionCount jobExecutionCount) throws Exception {
    final AtomicReference<JobExecutionCount> jobExecutionStageId = new AtomicReference<>();
    doWork(session -> jobExecutionStageId.set(dao.create(jobExecutionCount, session)));
    return jobExecutionStageId.get();
  }

  public JobExecutionCount create(Long jobExecutionId, JobServiceType serviceType, JobExecutionCount counts) throws Exception {
    counts.setJobExecutionId(jobExecutionId);
    counts.setServiceType(serviceType);

    final AtomicReference<JobExecutionCount> jobExecutionStageId = new AtomicReference<>();
    doWork(session -> jobExecutionStageId.set(dao.create(counts, session)));
    return jobExecutionStageId.get();
  }

  public Boolean incrementCount(Long jobExecutionId, JobServiceType serviceType, JobExecutionCount counts) throws Exception {
    final AtomicReference<Boolean> jobExecutionStageId = new AtomicReference<>();
    doWork(session -> jobExecutionStageId.set(dao.incrementCount(jobExecutionId, serviceType, counts, session)));
    return jobExecutionStageId.get();
  }

  public List<JobExecutionCount> getByJobExecutionId(long jobExecutionId) throws Exception {
    final AtomicReference<List<JobExecutionCount>> jobExecutionStages = new AtomicReference<>();
    doWork(session -> jobExecutionStages.set(dao.getByJobExecutionId(jobExecutionId, session)));
    return jobExecutionStages.get();
  }

  public List<JobExecutionCount> getByServiceName(long jobExecutionId, JobServiceType serviceType) throws Exception {
    final AtomicReference<List<JobExecutionCount>> jobExecutionStages = new AtomicReference<>();
    doWork(session -> jobExecutionStages.set(dao.getByServiceName(jobExecutionId, serviceType, session)));
    return jobExecutionStages.get();
  }
}
