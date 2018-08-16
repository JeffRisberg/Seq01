package com.company.jobServer.executors;

import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.beans.JobExecutionState;
import com.company.jobServer.beans.JobExecutionSummary;
import com.company.jobServer.beans.enums.JobStatus;
import com.company.jobServer.common.ResourceLocator;
import com.company.jobServer.common.orchestration.DeployableObject;
import com.company.jobServer.common.orchestration.DeploymentHandle;
import com.company.jobServer.common.orchestration.ExecutionType;
import com.company.jobServer.controllers.JobExecutionController;
import com.company.jobServer.controllers.JobExecutionStateController;
import com.company.jobServer.controllers.JobExecutionSummaryController;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;

@Slf4j
public class BaseJobExecutor implements IJobExecutor {
    private final static String DOCKER_IMAGE_TAG = "company.docker.image.tag";
    private final static String DEFAULT_DOCKER_IMAGE_TAG = "Milestone_8.1";

    protected final ObjectMapper mapper = new ObjectMapper();

    static JobExecutionController jobExecutionController = new JobExecutionController();
    static JobExecutionStateController jobExecutionStateController = new JobExecutionStateController();
    static JobExecutionSummaryController jobExecutionSummaryController = new JobExecutionSummaryController();

    @Override
    public JobExecution start(Job job, JSONObject runtimeParams) {
        log.info("startJob " + job.getId() + " " + job.getName());

        try {
            Timestamp deploymentStartDatetime = new Timestamp(new Date().getTime());
            JobExecution jobExecution = new JobExecution();

            // Add placeholder handle, if Kubernetes launches successfully it will be replaced with proper handle
            jobExecution.setDeploymentHandle("placeholder-handle-" + deploymentStartDatetime.toString()
                    .replace(":", "_").replace(" ", "_"));
            jobExecution.setJob(job);
            jobExecution.setJobId(job.getId());
            jobExecution.setHandleData("{}");
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

                deployableObject.setInputParams(mapper.writeValueAsString(runtimeParams));

                this.setupEnv(job, deployableObject);

                log.info("about to launch " + deployableObject);

                DeploymentHandle handle = null;// JobServer.containerService.launchDeployment(deployableObject);
                jobExecution.setDeploymentHandle(handle.getName());
                jobExecution.setHandleData(mapper.writeValueAsString(handle));
                jobExecution.setOutputLocation(job.getOutputModel());

                /*
                CompletionChecker completionChecker = new CompletionChecker(jobExecution, handle);

                ScheduledFuture<?> future = JobServer.executor.scheduleAtFixedRate
                        (completionChecker, 30, 30, TimeUnit.SECONDS);

                JobServer.currentTimers.put(jobExecution.getDeploymentHandle(), future);
                */

            } catch (Exception e) {
                log.error("Exception during deployment launch", e);
            }

            JobExecution createdJobExecution = jobExecutionController.create(jobExecution);
            if (createdJobExecution != null) {
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
            } else {
                log.error("createdJobExecution was null");
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
