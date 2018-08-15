package com.company.jobServer;

import com.company.jobServer.beans.Job;

public class JobDescription {
  protected Job job;
  protected boolean needsToStart;

  public JobDescription(Job job) {
    this.job = job;
    this.needsToStart = true;
  }

  public Job getJob() {
    return job;
  }

  public void setJob(Job job) {
    this.job = job;
  }

  public boolean isNeedsToStart() {
    return needsToStart;
  }

  public void setNeedsToStart(boolean needsToStart) {
    this.needsToStart = needsToStart;
  }
}
