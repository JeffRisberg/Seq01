package com.company.jobServer.controllers.DAO;

import com.company.jobServer.beans.Job;
import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JobDAO extends BaseDAOImpl {

  @Override
  public <T> T create(T obj, Class<T> type, Session session) {
    Long id = (Long) session.save(obj);
    obj = getById(type, id, session);
    return obj;
  }

  public Job create(Job obj, @NonNull Session session) {
    return super.create(obj, Job.class, session);
  }

  public Job getById(Long id, @NonNull Session session) {
    return super.getById(Job.class, id, session);
  }

  public List<Job> getByReferenceId(String referenceId, Session session) {
    try {
      CriteriaBuilder builder = session.getCriteriaBuilder();

      CriteriaQuery<Job> cq = builder.createQuery(Job.class);
      Root<Job> root = cq.from(Job.class);
      cq.select(root).where(builder.equal(root.get("referenceId"), referenceId));

      return session.createQuery(cq).getResultList();
    } catch (Exception e) {
      return null;
    }
  }

  public Boolean delete(Long id, @NonNull Session session) {
    return super.deleteById(Job.class, id, session);
  }
}

