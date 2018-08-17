package com.company.jobServer.controllers.DAO;

import com.company.jobServer.beans.JobDependency;
import lombok.NonNull;
import org.hibernate.Session;

public class JobDependencyDAO extends BaseDAOImpl {

  @Override
  public <T> T create(T obj, Class<T> type, Session session) {
    Long id = (Long) session.save(obj);
    obj = getById(type, id, session);
    return obj;
  }

  public JobDependency create(JobDependency obj, @NonNull Session session) {
    return super.create(obj, JobDependency.class, session);
  }

  public JobDependency getById(Long id, @NonNull Session session) {
    return super.getById(JobDependency.class, id, session);
  }

  public Boolean delete(Long id, @NonNull Session session) {
    return super.deleteById(JobDependency.class, id, session);
  }
}

