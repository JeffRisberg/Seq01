package com.company.jobServer.controllers.DAO;

import com.company.jobServer.beans.JobExecutionSummary;
import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JobExecutionSummaryDAO extends BaseDAOImpl {

  @Override
  public <T> T create(T obj, Class<T> type, Session session) {
    Long id = (Long) session.save(obj);
    obj = getById(type, id, session);
    return obj;
  }

  public JobExecutionSummary create(JobExecutionSummary obj, @NonNull Session session) {
    return super.create(obj, JobExecutionSummary.class, session);
  }

  public JobExecutionSummary getById(Long id, @NonNull Session session) {
    return super.getById(JobExecutionSummary.class, id, session);
  }

  public Boolean delete(Long id, @NonNull Session session) {
    return super.deleteById(JobExecutionSummary.class, id, session);
  }

  public List<JobExecutionSummary> getByHandle(String handle, Session session) {
    try {
      CriteriaBuilder builder = session.getCriteriaBuilder();

      CriteriaQuery<JobExecutionSummary> cq = builder.createQuery(JobExecutionSummary.class);
      Root<JobExecutionSummary> root = cq.from(JobExecutionSummary.class);
      cq.select(root).where(builder.equal(root.get("handle"), handle));

      return session.createQuery(cq).getResultList();
    } catch (Exception e) {
      return null;
    }
  }
}

