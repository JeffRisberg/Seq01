package com.company.jobServer.common.orchestration;

public enum ExecutionType {
  // not that these are in the order of the chapters in the Kubernetes book.
  Pod, Service, Job, CronJob, Deployment;
}
