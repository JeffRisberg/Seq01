package com.company.jobServer.controllers;

import com.company.jobServer.JobServer;
import com.company.jobServer.beans.JobExecutionState;
import com.company.jobServer.beans.JobExecutionSummary;
import com.company.jobServer.controllers.DAO.JobExecutionSummaryDAO;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.NativeQuery;
import org.json.simple.JSONObject;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * JobExecution API's for managing JobExecutionSummaries CRUD
 */
@Slf4j
public class JobExecutionSummaryController extends BaseController {
  private static JobExecutionSummaryDAO dao = new JobExecutionSummaryDAO();

  public JobExecutionSummary getById(Long id) throws Exception {
    final AtomicReference<JobExecutionSummary> td = new AtomicReference<>();
    doWork(session -> td.set(dao.getById(id, session)));
    return td.get();
  }

  public JobExecutionSummary create(JobExecutionSummary jobExecutionSummary) throws Exception {
    final AtomicReference<JobExecutionSummary> routeId = new AtomicReference<>();
    doWork(session -> routeId.set(dao.create(jobExecutionSummary, session)));
    return routeId.get();
  }

  public boolean update(JobExecutionSummary updatedEntity) throws Exception {
    final AtomicReference<Boolean> updated = new AtomicReference<>();
    doWork(session -> updated.set(dao.update(updatedEntity, session)));
    return updated.get();
  }

  public boolean delete(Long id) throws Exception {
    final AtomicReference<Boolean> deleted = new AtomicReference<>();
    doWork(session -> deleted.set(dao.delete(id, session)));
    return deleted.get();
  }

  public List<JobExecutionSummary> getAll() throws Exception {
    final AtomicReference<List<JobExecutionSummary>> response = new AtomicReference<>();
    doWork(session -> response.set(dao.listAll(JobExecutionSummary.class, session)));
    return response.get();
  }

  public boolean updateSummaryCounts(JobExecutionState state, long jobId) {
    Session session = null;
    Transaction tx = null;
    try {
      session = JobServer.sessionFactory.openSession();
      tx = session.beginTransaction();

      NativeQuery query;

      JSONObject stateInfo = state.getStateInfo();
      int jobStatus;

      switch (state.getState().toString()) {
        case "PENDING":
          jobStatus = 0;
          break;
        case "RUNNING":
          jobStatus = 1;
          break;
        case "SUCCEEDED":
          jobStatus = 2;
          break;
        case "FAILED":
          jobStatus = 3;
          break;
        case "KILLED":
          jobStatus = 4;
          break;
        default:
          jobStatus = 5;
      }

      log.info("JobStatus: " + jobStatus);
      log.info("StateInfo: " + stateInfo.toJSONString());

      if (stateInfo != null && !stateInfo.toJSONString().equals("{}")) {
        Long ticketCount = null;
        Long documentCount = null;
        Long externalDocumentCount = null;
        Long faqParsed = null;
        Long knowledgeArticleParsed = null;
        Long problemCount = null;
        Long alertCount = null;
        Long changeRequestsCount = null;

        if (stateInfo.containsKey("ticketCount"))
          ticketCount = Long.parseLong(stateInfo.get("ticketCount").toString());
        if (stateInfo.containsKey("documentCount"))
          documentCount = Long.parseLong(stateInfo.get("documentCount").toString());
        if (stateInfo.containsKey("externalDocumentCount"))
          externalDocumentCount = Long.parseLong(stateInfo.get("externalDocumentCount").toString());
        if (stateInfo.containsKey("faqParsed"))
          faqParsed = Long.parseLong(stateInfo.get("faqParsed").toString());
        if (stateInfo.containsKey("knowledgeArticleParsed"))
          knowledgeArticleParsed = Long.parseLong(stateInfo.get("knowledgeArticleParsed").toString());
        if (stateInfo.containsKey("problemCount"))
          problemCount = Long.parseLong(stateInfo.get("problemCount").toString());
        if (stateInfo.containsKey("alertCount"))
          alertCount = Long.parseLong(stateInfo.get("alertCount").toString());
        if (stateInfo.containsKey("changeRequestsCount"))
          changeRequestsCount = Long.parseLong(stateInfo.get("changeRequestsCount").toString());

        String handle = state.getHandle();

        String sqlQuery = "UPDATE job_stats_records SET ticket_count = COALESCE(:ticketCount, ticket_count)," +
          "document_count = COALESCE(:documentCount, document_count)," +
          "external_document_count = COALESCE(:externalDocumentCount, external_document_count)," +
          "faq_parsed = COALESCE(:faqParsed, faq_parsed)," +
          "knowledge_article_parsed = COALESCE(:knowledgeArticleParsed, knowledge_article_parsed)," +
          "problem_count = COALESCE(:problemCount, problem_count)," +
          "alert_count = COALESCE(:alertCount, alert_count)," +
          "change_requests_count = COALESCE(:changeRequestsCount, change_requests_count)," +
          "state = :state, " +
          "end_datetime = :endDatetime " +
          "WHERE handle = :handle AND job_id = :jobId";

        log.info("faqParsed: " + faqParsed);
        log.info("handle: " + handle);
        log.info("jobId: " + jobId);

        query = session.createNativeQuery(sqlQuery)
          .setParameter("ticketCount", ticketCount)
          .setParameter("documentCount", documentCount)
          .setParameter("externalDocumentCount", externalDocumentCount)
          .setParameter("faqParsed", faqParsed)
          .setParameter("knowledgeArticleParsed", knowledgeArticleParsed)
          .setParameter("problemCount", problemCount)
          .setParameter("alertCount", alertCount)
          .setParameter("changeRequestsCount", changeRequestsCount)
          .setParameter("state", jobStatus)
          .setParameter("endDatetime", state.getStateDatetime())
          .setParameter("handle", handle)
          .setParameter("jobId", jobId);

        query.executeUpdate();
        tx.commit();

        return true;
      } else {
        return false;
      }
    } catch (Exception e) {
      e.printStackTrace();
      if (tx != null)
        tx.rollback();
      return false;
    } finally {
      session.close();
    }
  }

  public List<JobExecutionSummary> getJobExecutionSummaryByHandle(String handle) {
    try {
      final AtomicReference<List<JobExecutionSummary>> response = new AtomicReference<>();
      doWork(session -> response.set(dao.getByHandle(handle, session)));
      return response.get();
    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }
}
