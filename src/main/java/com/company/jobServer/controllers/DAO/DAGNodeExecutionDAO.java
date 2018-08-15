package com.company.jobServer.controllers.DAO;

import com.company.jobServer.beans.DAGNodeExecution;
import lombok.NonNull;
import org.hibernate.Session;

public class DAGNodeExecutionDAO extends BaseDAOImpl {

  @Override
  public <T> T create(T obj, Class<T> type, Session session) {
    Long id = (Long) session.save(obj);
    obj = getById(type, id, session);
    return obj;
  }

  public DAGNodeExecution create(DAGNodeExecution obj, @NonNull Session session) {
    return super.create(obj, DAGNodeExecution.class, session);
  }

  public DAGNodeExecution getById(Long id, @NonNull Session session) {
    return super.getById(DAGNodeExecution.class, id, session);
  }

  public Boolean delete(Long id, @NonNull Session session) {
    return super.deleteById(DAGNodeExecution.class, id, session);
  }
}

