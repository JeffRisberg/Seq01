package com.company.jobServer.controllers;

import com.company.jobServer.FilterDescription;
import com.company.jobServer.beans.Job;
import com.company.jobServer.controllers.DAO.JobDAO;
import org.glassfish.jersey.internal.guava.UncheckedExecutionException;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Job API's for managing Job CRUD
 */
public class JobController extends BaseController {
  private static JobDAO dao = new JobDAO();

  public Job getById(Long id) throws Exception {
    final AtomicReference<Job> j = new AtomicReference<>();
    doWork(session -> j.set(dao.getById(id, session)));
    return j.get();
  }

  public Job create(Job job) throws Exception {
    final AtomicReference<Job> j = new AtomicReference<>();
    doWork(session -> j.set(dao.create(job, session)));
    return j.get();
  }

  public Job createIfNotExists(Job job) throws Exception {
    String referenceId = job.getReferenceId();

    final AtomicReference<Job> tJIReference = new AtomicReference<>();
    doWork(session -> {
      try {
        if (dao.getByReferenceId(referenceId, session).size() == 0) {
          Job createdJob = dao.create(job, session);
          tJIReference.set(createdJob);
        }
      } catch (Exception e) {
        throw new UncheckedExecutionException(e);
      }
    });
    return tJIReference.get();
  }

  public Job createOrUpdate(Job job) throws Exception {
    String referenceId = job.getReferenceId();

    final AtomicReference<Job> tJIReference = new AtomicReference<>();
    doWork(session -> {
      try {
        List<Job> existingJobs = dao.getByReferenceId(referenceId, session);

        if (existingJobs.size() == 0) {
          Job createdJob = dao.create(job, session);
          tJIReference.set(createdJob);

        } else {
          job.setId(existingJobs.get(0).getId());
          dao.update(job, session);

          tJIReference.set(dao.getByReferenceId(referenceId, session).get(0));
        }
      } catch (Exception e) {
        throw new UncheckedExecutionException(e);
      }
    });
    return tJIReference.get();
  }

  public boolean update(Job updatedEntity) throws Exception {
    final AtomicReference<Boolean> updated = new AtomicReference<>();
    doWork(session -> updated.set(dao.update(updatedEntity, session)));
    return updated.get();
  }

  public boolean delete(Long id) throws Exception {
    final AtomicReference<Boolean> deleted = new AtomicReference<>();
    doWork(session -> deleted.set(dao.delete(id, session)));
    return deleted.get();
  }

  public List<Job> getAll() throws Exception {
    final AtomicReference<List<Job>> response = new AtomicReference<>();
    doWork(session -> response.set(dao.listAll(Job.class, session)));
    return response.get();
  }

  public List<Job> getByReferenceId(String referenceId) throws Exception {
    final AtomicReference<List<Job>> response = new AtomicReference<>();
    doWork(session -> response.set(dao.getByReferenceId(referenceId, session)));
    return response.get();
  }

  public List<Job> getByCriteria(List<FilterDescription> filterDescriptions) throws Exception {
    final AtomicReference<List<Job>> response = new AtomicReference<>();
    doWork(session -> {
      List<Job> jobs;
      try {
        jobs = dao.getByCriteria(Job.class, filterDescriptions, session);
      } catch (Exception e) {
        throw new UncheckedExecutionException(e);
      }
      response.set(jobs);
    });
    return response.get();
  }
}
