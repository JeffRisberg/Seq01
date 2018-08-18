package com.company.jobServer.services;

import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobDependency;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.controllers.JobController;
import com.company.jobServer.controllers.JobDependencyController;
import com.company.jobServer.controllers.JobExecutionController;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class DagAnalyzer {

  protected List<Job> jobs;
  protected List<JobDependency> jobDependencies;
  protected List<JobExecution> jobExecutions;

  public DagAnalyzer() {
    try {
      JobController jc = new JobController();
      JobDependencyController jdc = new JobDependencyController();
      JobExecutionController jec = new JobExecutionController();

      this.jobs = jc.getAll();
      this.jobDependencies = jdc.getAll();
      this.jobExecutions = jec.getAll();

    } catch (Exception e) {
    }
  }

  public Optional<JobExecution> getJobExecution(Job job) {
    Optional<JobExecution> optJe = jobExecutions.stream().filter(
      je -> je.getJob().getId() == job.getId()).findFirst();
    return optJe;
  }

  public boolean areAllDependenciesMet(Job job) {
    List<JobDependency> deps = jobDependencies.stream().filter(
      jd -> jd.getTo().getId() == job.getId()).collect(Collectors.toList());

    for (JobDependency jd : deps) {
      Optional<JobExecution> optJe = getJobExecution(jd.getFrom());

      if (!optJe.isPresent() || !areAllDependenciesMet(optJe.get().getJob())) {
        return false;
      }
    }

    return true;
  }

  public List<Job> getNextJobs() {
    List<Job> nextJobs = jobs.stream().filter(
      job -> {
        Optional<JobExecution> optJe = getJobExecution(job);

        if (optJe.isPresent() && optJe.get().isDone()) {
          return false;
        }

        return areAllDependenciesMet(job);
      }
    ).collect(Collectors.toList());

    return nextJobs;
  }
}
