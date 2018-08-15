package com.company.jobServer.controllers;

import com.company.jobServer.beans.DAGNodeExecution;
import com.company.jobServer.controllers.DAO.DAGNodeExecutionDAO;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * DAGNodeExecution API's for managing DAGNodeExecution CRUD
 */
public class DAGNodeExecutionController extends BaseController {
  private static DAGNodeExecutionDAO dao = new DAGNodeExecutionDAO();

  public DAGNodeExecution getById(Long id) throws Exception {
    final AtomicReference<DAGNodeExecution> td = new AtomicReference<>();
    doWork(session -> td.set(dao.getById(id, session)));
    return td.get();
  }

  public DAGNodeExecution create(DAGNodeExecution dagNodeExecution) throws Exception {
    final AtomicReference<DAGNodeExecution> dagNodeExecutionId = new AtomicReference<>();
    doWork(session -> dagNodeExecutionId.set(dao.create(dagNodeExecution, session)));
    return dagNodeExecutionId.get();
  }

  public boolean delete(Long id) throws Exception {
    final AtomicReference<Boolean> deleted = new AtomicReference<>();
    doWork(session -> deleted.set(dao.delete(id, session)));
    return deleted.get();
  }

  public List<DAGNodeExecution> getAll() throws Exception {
    final AtomicReference<List<DAGNodeExecution>> response = new AtomicReference<>();
    doWork(session -> response.set(dao.listAll(DAGNodeExecution.class, session)));
    return response.get();
  }
}
