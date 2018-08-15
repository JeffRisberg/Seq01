package com.company.jobServer.controllers.DAO;

import com.company.jobServer.beans.JobExecutionStep;
import lombok.NonNull;
import org.hibernate.Session;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import java.util.List;

public class JobExecutionStepDAO extends BaseDAOImpl {
  public JobExecutionStep create(JobExecutionStep obj, @NonNull Session session) {
    return super.create(obj, JobExecutionStep.class, session);
  }

  public List<JobExecutionStep> getByJobExecutionId(long jobExecutionId, Session session) {
    try {
      CriteriaBuilder builder = session.getCriteriaBuilder();

      CriteriaQuery<JobExecutionStep> cq = builder.createQuery(JobExecutionStep.class);
      Root<JobExecutionStep> root = cq.from(JobExecutionStep.class);
      cq.select(root).where(builder.equal(root.get("jobExecutionId"), jobExecutionId));

      return session.createQuery(cq).getResultList();
    } catch (Exception e) {
      return null;
    }
  }

  public List<JobExecutionStep> getByStepName(long jobExecutionId, String stepName, Session session) {
    try {
      CriteriaBuilder builder = session.getCriteriaBuilder();

      CriteriaQuery<JobExecutionStep> cq = builder.createQuery(JobExecutionStep.class);
      Root<JobExecutionStep> root = cq.from(JobExecutionStep.class);
      cq.select(root).where(builder.and(
        builder.equal(root.get("jobExecutionId"), jobExecutionId),
        builder.equal(root.get("step"), stepName)));

      return session.createQuery(cq).getResultList();
    } catch (Exception e) {
      return null;
    }
  }
}
