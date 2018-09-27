package com.company.jobServer.controllers.DAO;

import com.company.jobServer.beans.JobExecutionCount;
import com.company.jobServer.common.job.JobServiceType;
import com.google.common.collect.ImmutableMap;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.query.NativeQuery;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;

@Slf4j
public class JobExecutionCountDAO extends BaseDAOImpl {

  public JobExecutionCount create(JobExecutionCount obj, @NonNull Session session) {
    return super.create(obj, JobExecutionCount.class, session);
  }

  public Boolean update(JobExecutionCount obj, @NonNull Session session) {
    return super.update(obj, session);
  }

  public List<JobExecutionCount> getByJobExecutionId(long jobExecutionId, Session session) {
    Map input = ImmutableMap.builder()
      .put("jobExecutionId", jobExecutionId)
      .build();
    return getByCriteria(JobExecutionCount.class, input, session);
  }

  public List<JobExecutionCount> getByServiceName(long jobExecutionId, JobServiceType svcName, Session session) {
    Map input = ImmutableMap.builder()
      .put("jobExecutionId", jobExecutionId)
      .put("serviceType", svcName)
      .build();
    return getByCriteria(JobExecutionCount.class, input, session);
  }

  public boolean incrementCount(long jobExecutionId, JobServiceType svcName, JobExecutionCount counts, Session session) {
    try {
      Timestamp updatedTime = new Timestamp(System.currentTimeMillis());

      String sql =
        "UPDATE job_execution_counts \n" +
          "SET input_valid = input_valid + :inputValid_count, \n" +
          "input_invalid = input_invalid + :inputInvalid_count, \n" +
          "output_valid = output_valid + :outputValid_count, \n" +
          "output_invalid = output_invalid + :outputInvalid_count, \n" +
          "updated_at = :updated_at \n" +
          "WHERE job_execution_id = :id AND service_type = :svcType";

      NativeQuery query = session.createNativeQuery(sql)
        .setParameter("inputValid_count", getValue(counts.getInputValid()))
        .setParameter("inputInvalid_count", getValue(counts.getInputInvalid()))
        .setParameter("outputValid_count", getValue(counts.getOutputValid()))
        .setParameter("outputInvalid_count", getValue(counts.getOutputInvalid()))
        .setParameter("updated_at", updatedTime)
        .setParameter("id", jobExecutionId)
        .setParameter("svcType", svcName.name())
        .addEntity(JobExecutionCount.class);

      int result = query.executeUpdate();
      return (result > 0 ? true : false);
    } catch (Exception e) {
      return false;
    }
  }

  private long getValue(Long val) {
    return val != null ? val.longValue() : 0;
  }
}
