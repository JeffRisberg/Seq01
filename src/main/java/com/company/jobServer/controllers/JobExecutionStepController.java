package com.company.jobServer.controllers;


import com.company.jobServer.beans.JobExecutionStep;
import com.company.jobServer.controllers.DAO.JobExecutionStepDAO;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class JobExecutionStepController extends BaseController {
  private static JobExecutionStepDAO dao = new JobExecutionStepDAO();

  public JobExecutionStep create(JobExecutionStep jobExecutionStep) throws Exception {
    final AtomicReference<JobExecutionStep> jobExecutionStepId = new AtomicReference<>();
    doWork(session -> jobExecutionStepId.set(dao.create(jobExecutionStep, session)));
    return jobExecutionStepId.get();
  }

  public List<JobExecutionStep> getByJobExecutionId(long jobExecutionId) throws Exception {
    final AtomicReference<List<JobExecutionStep>> jobExecutionSteps = new AtomicReference<>();
    doWork(session -> jobExecutionSteps.set(dao.getByJobExecutionId(jobExecutionId, session)));
    return jobExecutionSteps.get();
  }

  public List<JobExecutionStep> getByStepName(long jobExecutionId, String stepName) throws Exception {
    final AtomicReference<List<JobExecutionStep>> jobExecutionSteps = new AtomicReference<>();
    doWork(session -> jobExecutionSteps.set(dao.getByStepName(jobExecutionId, stepName, session)));
    return jobExecutionSteps.get();
  }
}
