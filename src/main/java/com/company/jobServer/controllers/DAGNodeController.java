package com.company.jobServer.controllers;

import com.company.jobServer.beans.DAGNode;
import com.company.jobServer.controllers.DAO.DAGNodeDAO;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DAGNode API's for managing DAGNode CRUD
 */
public class DAGNodeController extends BaseController {
  private static DAGNodeDAO dao = new DAGNodeDAO();

  public DAGNode getById(Long id) throws Exception {
    final AtomicReference<DAGNode> td = new AtomicReference<>();
    doWork(session -> td.set(dao.getById(id, session)));
    return td.get();
  }

  public DAGNode create(DAGNode dagNode) throws Exception {
    final AtomicReference<DAGNode> jobId = new AtomicReference<>();
    doWork(session -> jobId.set(dao.create(dagNode, session)));
    return jobId.get();
  }

  public boolean update(DAGNode updatedEntity) throws Exception {
    final AtomicReference<Boolean> updated = new AtomicReference<>();
    doWork(session -> updated.set(dao.update(updatedEntity, session)));
    return updated.get();
  }

  public boolean delete(Long id) throws Exception {
    final AtomicReference<Boolean> deleted = new AtomicReference<>();
    doWork(session -> deleted.set(dao.delete(id, session)));
    return deleted.get();
  }

  public List<DAGNode> getAll() throws Exception {
    final AtomicReference<List<DAGNode>> response = new AtomicReference<>();
    doWork(session -> response.set(dao.listAll(DAGNode.class, session)));
    return response.get();
  }
}
