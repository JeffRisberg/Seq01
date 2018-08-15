package com.company.jobServer.controllers;

import com.company.jobServer.beans.DAGNodeDependency;
import com.company.jobServer.controllers.DAO.DAGNodeDependencyDAO;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DAGNodeDependency API's for managing DAGNodeDependency CRUD
 */
public class DAGNodeDependencyController extends BaseController {
  private static DAGNodeDependencyDAO dao = new DAGNodeDependencyDAO();

  public DAGNodeDependency getById(Long id) throws Exception {
    final AtomicReference<DAGNodeDependency> td = new AtomicReference<>();
    doWork(session -> td.set(dao.getById(id, session)));
    return td.get();
  }

  public DAGNodeDependency create(DAGNodeDependency dagNodeDependency) throws Exception {
    final AtomicReference<DAGNodeDependency> jobId = new AtomicReference<>();
    doWork(session -> jobId.set(dao.create(dagNodeDependency, session)));
    return jobId.get();
  }

  public boolean update(DAGNodeDependency updatedEntity) throws Exception {
    final AtomicReference<Boolean> updated = new AtomicReference<>();
    doWork(session -> updated.set(dao.update(updatedEntity, session)));
    return updated.get();
  }

  public boolean delete(Long id) throws Exception {
    final AtomicReference<Boolean> deleted = new AtomicReference<>();
    doWork(session -> deleted.set(dao.delete(id, session)));
    return deleted.get();
  }

  public List<DAGNodeDependency> getAll() throws Exception {
    final AtomicReference<List<DAGNodeDependency>> response = new AtomicReference<>();
    doWork(session -> response.set(dao.listAll(DAGNodeDependency.class, session)));
    return response.get();
  }
}
