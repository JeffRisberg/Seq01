package com.company.jobServer.controllers.DAO;

import com.company.jobServer.beans.DAGNode;
import lombok.NonNull;
import org.hibernate.Session;

public class DAGNodeDAO extends BaseDAOImpl {

  @Override
  public <T> T create(T obj, Class<T> type, Session session) {
    Long id = (Long) session.save(obj);
    obj = getById(type, id, session);
    return obj;
  }

  public DAGNode create(DAGNode obj, @NonNull Session session) {
    return super.create(obj, DAGNode.class, session);
  }

  public DAGNode getById(Long id, @NonNull Session session) {
    return super.getById(DAGNode.class, id, session);
  }

  public Boolean delete(Long id, @NonNull Session session) {
    return super.deleteById(DAGNode.class, id, session);
  }
}

