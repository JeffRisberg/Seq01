package com.company.jobServer.controllers.DAO;

import com.company.jobServer.JobServer;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.beans.enums.JobStatus;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;

import java.sql.Timestamp;

@Slf4j
public class JobExecutionDAO extends BaseDAOImpl {

  @Override
  public <T> T create(T obj, Class<T> type, Session session) {
    Long id = (Long) session.save(obj);
    obj = getById(type, id, session);
    return obj;
  }

  public JobExecution create(JobExecution obj, @NonNull Session session) {
    return super.create(obj, JobExecution.class, session);
  }

  public Boolean update(JobExecution obj, @NonNull Session session) {
    return super.update(obj, session);
  }

  public JobExecution getById(Long id, @NonNull Session session) {
    return super.getById(JobExecution.class, id, session);
  }

  public Boolean delete(Long id, @NonNull Session session) {
    return super.deleteById(JobExecution.class, id, session);
  }

  public boolean updateById(JobExecution jobExecution, JobStatus jobStatus, Timestamp endDateTime) {
    Session session = null;
    Transaction tx = null;
    try {
      session = JobServer.sessionFactory.openSession();
      tx = session.beginTransaction();

      String sql = "UPDATE job_executions SET status=:state, stopped_at=:end_datetime WHERE id=:id";
      NativeQuery query = session.createNativeQuery(sql)
        .setParameter("state", jobStatus.name())
        .setParameter("end_datetime", endDateTime)
        .setParameter("id", jobExecution.getId())
        .addEntity(JobExecution.class);

      int result = query.executeUpdate();
      tx.commit();
      return (result > 0 ? true : false);
    } catch (Exception e) {
      if (tx != null)
        tx.rollback();
      return false;
    }
  }
}

