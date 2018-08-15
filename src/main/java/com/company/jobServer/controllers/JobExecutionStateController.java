package com.company.jobServer.controllers;

import com.company.jobServer.beans.JobExecutionState;
import com.company.jobServer.controllers.DAO.JobExecutionStateDAO;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * JobExecution API's for managing JobExecutionState CRUD
 */
public class JobExecutionStateController extends BaseController {
  private static JobExecutionStateDAO dao = new JobExecutionStateDAO();

  public JobExecutionState getById(Long id) throws Exception {
    final AtomicReference<JobExecutionState> td = new AtomicReference<>();
    doWork(session -> td.set(dao.getById(id, session)));
    return td.get();
  }

  public JobExecutionState create(JobExecutionState jobExecutionState) throws Exception {
    final AtomicReference<JobExecutionState> jobExecutionId = new AtomicReference<>();
    doWork(session -> jobExecutionId.set(dao.create(jobExecutionState, session)));
    return jobExecutionId.get();
  }

  public boolean delete(Long id) throws Exception {
    final AtomicReference<Boolean> deleted = new AtomicReference<>();
    doWork(session -> deleted.set(dao.delete(id, session)));
    return deleted.get();
  }

  public List<JobExecutionState> getAll() throws Exception {
    final AtomicReference<List<JobExecutionState>> response = new AtomicReference<>();
    doWork(session -> response.set(dao.listAll(JobExecutionState.class, session)));
    return response.get();
  }

  public List<JobExecutionState> getByHandle(String handle) throws Exception {
    final AtomicReference<List<JobExecutionState>> response = new AtomicReference<>();
    Map<String, Object> params = new HashMap<>();
    params.put("handle", handle);
    String sql = "SELECT * FROM job_execution_states WHERE deployment_handle=:handle";
    doWork(session -> response.set(dao.getBySQL(JobExecutionState.class, sql, params, session)));
    return response.get();
  }
}
