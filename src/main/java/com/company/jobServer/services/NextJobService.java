package com.company.jobServer.services;

import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobDependency;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.controllers.JobController;
import com.company.jobServer.controllers.JobDependencyController;
import com.company.jobServer.controllers.JobExecutionController;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class NextJobService {

  protected JobExecution targetExecution;
  protected List<Job> jobs;
  protected List<JobDependency> jobDependencies;
  protected List<JobExecution> jobExecutions;
  protected Set<Job> candidateJobs;

  public NextJobService(JobExecution targetExecution) {
    this.targetExecution = targetExecution;
    Job targetJob = targetExecution.getJob();

    try {
      JobController jc = new JobController();
      JobDependencyController jdc = new JobDependencyController();
      JobExecutionController jec = new JobExecutionController();

      this.jobs = jc.getAll();
      this.jobDependencies = jdc.getAll();
      this.jobExecutions = jec.getAll();

      this.candidateJobs = new HashSet<>();

      for (Job job : jobs) {
        if (job.getParent() != null && job.getParent().getId() == targetJob.getId())
          candidateJobs.add(job);
      }
    } catch (Exception e) {
    }
  }

  Set<Job> queryDependencies(Set<Job> dependencies, Job target) {
    if (dependencies.contains(target)) {
      return dependencies;
    }

    dependencies.add(target);

    for (JobDependency jd : jobDependencies) {
      if (jd.getToId() == target.getId())
        queryDependencies(dependencies, jd.getFrom());
    }
    return dependencies;
  }

  public Optional<JobExecution> getJobExecution(Job job) {
    return jobExecutions.stream().filter(
      je -> je.getJob().getId() == job.getId()).findFirst();
  }

  public List<JobExecution> getJobExecutions(Job job) {
    return jobExecutions.stream().filter(
      je -> je.getJob().getId() == job.getId()).collect(Collectors.toList());
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
    return candidateJobs.stream().filter(
      candidateJob -> {
        List<JobExecution> jeList = getJobExecutions(candidateJob);

        for (JobExecution je : jeList) {
          JobExecution parentJE = je.getParentExecution();

          // Make sure this execution is within the target execution
          if (parentJE.getId() != targetExecution.getId()) {
            continue;
          }

          // This job is already executing within the target execution, so it is not a candidate
          return false;
        }

        return areAllDependenciesMet(candidateJob);
      }
    ).collect(Collectors.toList());
  }
}
