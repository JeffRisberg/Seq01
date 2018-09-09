package com.company.jobServer.common.orchestration;

public enum ExecutionType {
  // note that these are in the order of the chapters in the Kubernetes book.
  Pod, Service, Job, CronJob, ReplicaSet, Deployment
}
