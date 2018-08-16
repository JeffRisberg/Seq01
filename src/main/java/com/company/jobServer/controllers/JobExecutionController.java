package com.company.jobServer.controllers;

import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.beans.enums.JobStatus;
import com.company.jobServer.controllers.DAO.JobExecutionDAO;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * JobExecution API's for managing JobExecution CRUD
 */
public class JobExecutionController extends BaseController {
  private static JobExecutionDAO dao = new JobExecutionDAO();

  public JobExecution getById(Long id) throws Exception {
    final AtomicReference<JobExecution> td = new AtomicReference<>();
    doWork(session -> td.set(dao.getById(id, session)));
    return td.get();
  }

  public JobExecution create(JobExecution jobExecution) throws Exception {
    final AtomicReference<JobExecution> jobExecutionId = new AtomicReference<>();
    doWork(session -> jobExecutionId.set(dao.create(jobExecution, session)));
    return jobExecutionId.get();
  }

  public boolean delete(Long id) throws Exception {
    final AtomicReference<Boolean> deleted = new AtomicReference<>();
    doWork(session -> deleted.set(dao.delete(id, session)));
    return deleted.get();
  }

  public List<JobExecution> getAll() throws Exception {
    final AtomicReference<List<JobExecution>> response = new AtomicReference<>();
    doWork(session -> response.set(dao.listAll(JobExecution.class, session)));
    return response.get();
  }

  public List<JobExecution> getByHandle(String handle) throws Exception {
    final AtomicReference<List<JobExecution>> response = new AtomicReference<>();
    Map<String, Object> params = new HashMap<>();
    params.put("handle", handle);
    String sql = "SELECT * FROM job_executions WHERE deployment_handle=:handle";
    doWork(session -> response.set(dao.getBySQL(JobExecution.class, sql, params, session)));
    return response.get();
  }

  public List<JobExecution> getByJobId(Long jobId) throws Exception {
    final AtomicReference<List<JobExecution>> response = new AtomicReference<>();
    Map<String, Object> params = new HashMap<>();
    params.put("jobId", jobId);
    String sql = "SELECT * FROM job_executions WHERE job_id=:jobId";
    doWork(session -> response.set(dao.getBySQL(JobExecution.class, sql, params, session)));
    return response.get();
  }
}
