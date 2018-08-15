package com.company.jobServer.controllers.DAO;

import com.company.jobServer.beans.JobExecutionState;
import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JobExecutionStateDAO extends BaseDAOImpl {

  @Override
  public <T> T create(T obj, Class<T> type, Session session) {
    Long id = (Long) session.save(obj);
    obj = getById(type, id, session);
    return obj;
  }

  public JobExecutionState create(JobExecutionState obj, @NonNull Session session) {
    return super.create(obj, JobExecutionState.class, session);
  }

  public JobExecutionState getById(Long id, @NonNull Session session) {
    return super.getById(JobExecutionState.class, id, session);
  }

  public Boolean delete(Long id, @NonNull Session session) {
    return super.deleteById(JobExecutionState.class, id, session);
  }

  public List<JobExecutionState> getByHandle(String handle, Session session) {
    try {
      CriteriaBuilder builder = session.getCriteriaBuilder();

      CriteriaQuery<JobExecutionState> cq = builder.createQuery(JobExecutionState.class);
      Root<JobExecutionState> root = cq.from(JobExecutionState.class);
      cq.select(root).where(builder.equal(root.get("handle"), handle));

      return session.createQuery(cq).getResultList();
    } catch (Exception e) {
      return null;
    }
  }
}

