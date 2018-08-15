package com.company.jobServer.controllers.DAO;

import com.company.jobServer.beans.DAGNodeDependency;
import lombok.NonNull;
import org.hibernate.Session;

public class DAGNodeDependencyDAO extends BaseDAOImpl {

  @Override
  public <T> T create(T obj, Class<T> type, Session session) {
    Long id = (Long) session.save(obj);
    obj = getById(type, id, session);
    return obj;
  }

  public DAGNodeDependency create(DAGNodeDependency obj, @NonNull Session session) {
    return super.create(obj, DAGNodeDependency.class, session);
  }

  public DAGNodeDependency getById(Long id, @NonNull Session session) {
    return super.getById(DAGNodeDependency.class, id, session);
  }

  public Boolean delete(Long id, @NonNull Session session) {
    return super.deleteById(DAGNodeDependency.class, id, session);
  }
}

