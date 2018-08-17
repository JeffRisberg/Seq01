package com.company.jobServer.controllers;

import com.company.jobServer.beans.JobDependency;
import com.company.jobServer.controllers.DAO.JobDependencyDAO;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * JobDependency API's for managing JobDependency CRUD
 */
public class JobDependencyController extends BaseController {
  private static JobDependencyDAO dao = new JobDependencyDAO();

  public JobDependency getById(Long id) throws Exception {
    final AtomicReference<JobDependency> td = new AtomicReference<>();
    doWork(session -> td.set(dao.getById(id, session)));
    return td.get();
  }

  public JobDependency create(JobDependency dagNodeDependency) throws Exception {
    final AtomicReference<JobDependency> jobId = new AtomicReference<>();
    doWork(session -> jobId.set(dao.create(dagNodeDependency, session)));
    return jobId.get();
  }

  public boolean update(JobDependency updatedEntity) throws Exception {
    final AtomicReference<Boolean> updated = new AtomicReference<>();
    doWork(session -> updated.set(dao.update(updatedEntity, session)));
    return updated.get();
  }

  public boolean delete(Long id) throws Exception {
    final AtomicReference<Boolean> deleted = new AtomicReference<>();
    doWork(session -> deleted.set(dao.delete(id, session)));
    return deleted.get();
  }

  public List<JobDependency> getAll() throws Exception {
    final AtomicReference<List<JobDependency>> response = new AtomicReference<>();
    doWork(session -> response.set(dao.listAll(JobDependency.class, session)));
    return response.get();
  }
}
