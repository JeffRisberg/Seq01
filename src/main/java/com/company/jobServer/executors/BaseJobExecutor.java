package com.company.jobServer.executors;

import com.company.jobServer.JobServer;
import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.beans.enums.JobStatus;
import com.company.jobServer.beans.enums.JobType;
import com.company.jobServer.common.ResourceLocator;
import com.company.jobServer.common.orchestration.DeployableObject;
import com.company.jobServer.common.orchestration.DeploymentHandle;
import com.company.jobServer.common.orchestration.ExecutionType;
import com.company.jobServer.controllers.JobController;
import com.company.jobServer.controllers.JobExecutionController;
import com.company.jobServer.services.JobLaunchService;
import com.company.jobServer.services.NextJobService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BaseJobExecutor implements IJobExecutor {
  private final static String DOCKER_IMAGE_TAG = "company.docker.image.tag";
  private final static String DEFAULT_DOCKER_IMAGE_TAG = "Milestone_8.2";

  protected final ObjectMapper mapper = new ObjectMapper();

  static JobController jobController = new JobController();
  static JobExecutionController jobExecutionController = new JobExecutionController();
  static JobLaunchService jobLaunchService = new JobLaunchService();

  @Override
  public JobExecution start(Job job, JobExecution parentExecution, JSONObject envVars) {
    log.info("startJob " + job.getId() + " " + job.getName());

    try {
      JSONObject effectiveEnvVars = new JSONObject();
      if (job.getEnvVars() != null) {
        effectiveEnvVars.putAll(job.getEnvVars());
      }
      if (envVars != null) {
        effectiveEnvVars.putAll(envVars);
      }

      System.out.println("effectiveEnvVars:  " + effectiveEnvVars);

      Timestamp deploymentStartDatetime = new Timestamp(new Date().getTime());
      JobExecution jobExecution = new JobExecution();
      jobExecution.setCreatedAt(new Timestamp(System.currentTimeMillis()));

      // Add placeholder handle, if Kubernetes launches successfully it will be replaced with proper handle
      jobExecution.setDeploymentHandle("placeholder-handle-" + deploymentStartDatetime.toString()
        .replace(":", "_").replace(" ", "_"));
      jobExecution.setJob(job);
      jobExecution.setJobId(job.getId());
      jobExecution.setParentExecution(parentExecution);
      jobExecution.setParentExecutionId(parentExecution != null ? parentExecution.getId() : null);
      jobExecution.setHandleData("{}");
      jobExecution.setEnvVars(envVars);
      jobExecution.setEffectiveEnvVars(effectiveEnvVars);
      jobExecution.setStatus(JobStatus.PENDING);
      jobExecution.setTenantId(job.getTenantId());
      jobExecution.setStartedAt(deploymentStartDatetime);
      jobExecution.setDeploymentType(ExecutionType.Job);

      try {
        String imageTag = ResourceLocator.getResource(DOCKER_IMAGE_TAG).orElse(DEFAULT_DOCKER_IMAGE_TAG);
        String dockerImageName = job.getDockerImageName();
        DeployableObject deployableObject = new DeployableObject();

        deployableObject.setNamespace("default");
        deployableObject.setName(job.getName());

        if (dockerImageName == null) dockerImageName = "";
        if (dockerImageName.endsWith(":")) {
          dockerImageName = dockerImageName.substring(0, dockerImageName.length() - 1);
        }

        deployableObject.setDockerContainerName(dockerImageName + ":" + imageTag);
        deployableObject.setDescription(job.getName());

        if (job.getCronSchedule() == null || job.getCronSchedule().isEmpty() || job.getCronSchedule().equals("string"))
          deployableObject.setDeploymentType(ExecutionType.Job);
        else {
          deployableObject.setDeploymentType(ExecutionType.CronJob);
          deployableObject.setCronSchedule(job.getCronSchedule());
        }

        deployableObject.setInputParams(mapper.writeValueAsString(effectiveEnvVars));

        this.setupEnv(job, deployableObject);

        log.info("about to launch " + deployableObject);

        JobExecution createdJobExecution = jobExecutionController.create(jobExecution);
        if (createdJobExecution == null) {
          log.error("createdJobExecution was null");
          return null;
        }

        // Put the jobExecutionId as an environment variable passed via DeploymentObject
        deployableObject.getEnv().put("job_execution_id", String.valueOf(createdJobExecution.getId()));

        DeploymentHandle handle = jobLaunchService.launchJob(job, jobExecution);

        if (handle != null) {
          jobExecution.setDeploymentHandle(handle.getName());
          jobExecution.setHandleData(mapper.writeValueAsString(handle));
          jobExecution.setOutputLocation(job.getOutputModel());

          /*
          JobExecutionState jobExecutionState = new JobExecutionState();
          jobExecutionState.setHandle(createdJobExecution.getDeploymentHandle());
          jobExecutionState.setStateDatetime(createdJobExecution.getStartedAt());
          jobExecutionState.setStateInfo(mapper.readValue("{}", JSONObject.class));
          jobExecutionState.setState(createdJobExecution.getStatus());

          if (jobExecutionState.getHandle() != null) {
            jobExecutionStateController.create(jobExecutionState);
          }

          JobExecutionSummary jobExecutionSummary = new JobExecutionSummary();
          jobExecutionSummary.setJobId(job.getId());
          jobExecutionSummary.setHandle(createdJobExecution.getDeploymentHandle());
          jobExecutionSummary.setStartDatetime(createdJobExecution.getStartedAt());
          jobExecutionSummary.setEndDatetime(createdJobExecution.getStartedAt());
          jobExecutionSummaryController.create(jobExecutionSummary);
          */

          CompletionChecker completionChecker = new CompletionChecker(jobExecution, handle);

          ScheduledFuture<?> future = JobServer.executor.scheduleAtFixedRate
            (completionChecker, 30, 30, TimeUnit.SECONDS);

          JobServer.currentTimers.put(jobExecution.getDeploymentHandle(), future);
        } else {
          log.error("Could not start Kubernetes pod");
          return null;
        }
      } catch (Exception e) {
        log.error("Exception during pod launch", e);
      }

      return jobExecution;
    } catch (Exception e) {
      log.error("Exception in startJob", e);
      return null;
    }
  }

  @Override
  public void setupEnv(Job job, DeployableObject deployableObject) {
    HashMap<String, String> env = new HashMap<>();

    deployableObject.setEnv(env);
  }

  @Override
  public void stop(JobExecution jobExecution, boolean force) {
    try {
      String handle = jobExecution.getDeploymentHandle();

      log.info("JobExecution " + handle + " is done");

      JobExecution parentExecution = jobExecution.getParentExecution();

      JSONObject envVars = parentExecution.getEnvVars();

      try {
        jobExecution.setDone(true);
        jobExecution.setStatus(JobStatus.SUCCEEDED);
        jobExecutionController.update(jobExecution);

        log.info("recorded done in database");

        NextJobService nextJobService = new NextJobService(parentExecution);

        List<Job> nextJobs = nextJobService.getNextJobs();

        if (nextJobs.size() > 0) {
          for (Job nextJob : nextJobs) {
            System.out.println("starting job " + nextJob.getName());

            if (nextJob.getJobType() == JobType.CONNECTOR) {
              ConnectorJobExecutor jobExecutor = new ConnectorJobExecutor();

              jobExecutor.start(nextJob, parentExecution, envVars);
            }
            if (nextJob.getJobType() == JobType.MODEL) {
              ModelJobExecutor jobExecutor = new ModelJobExecutor();

              jobExecutor.start(nextJob, parentExecution, envVars);
            }
          }
        } else {
          parentExecution.setDone(true);
          parentExecution.setStatus(JobStatus.SUCCEEDED);
          jobExecutionController.update(parentExecution);
        }
      } catch (Exception e) {
        e.printStackTrace();
      }
      /*
      JobClient jobClient = new JobClient(handle);

      if (jobClient != null) {
        jobClient.updateJobExecution(JobStatus.SUCCEEDED, "{}");
      }

      DeploymentHandle deploymentHandle = new DeploymentHandleDTO();
      // deploymentHandle.setContainerService();
      deploymentHandle.setDeploymentType(jobExecution.getDeploymentType());
      deploymentHandle.setName(handle);
      deploymentHandle.setNamespace("default");
      // deploymentHandle.setUUID();
      // deploymentHandle.setInternalEndpoint();
      // deploymentHandle.setExternalEndpoint();

      JobServer.containerService.destroyDeployment(deploymentHandle);
      */
    } catch (Exception e) {
      log.error("Exception during request", e);
    }
  }

  @Override
  public void destroy(JobExecution jobExecution) {
    try {
      jobExecutionController.delete(jobExecution.getId());
    } catch (Exception e) {
      // what to do?
    }
  }
}
