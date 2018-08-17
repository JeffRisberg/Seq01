package com.company.jobServer.executors;

import com.company.jobServer.JobServer;
import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.beans.enums.JobStatus;
import com.company.jobServer.common.ResourceLocator;
import com.company.jobServer.common.orchestration.DeployableObject;
import com.company.jobServer.common.orchestration.DeploymentHandle;
import com.company.jobServer.common.orchestration.ExecutionType;
import com.company.jobServer.controllers.JobExecutionController;
import com.company.jobServer.services.JobLaunchService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

@Slf4j
public class BaseJobExecutor implements IJobExecutor {
    private final static String DOCKER_IMAGE_TAG = "company.docker.image.tag";
    private final static String DEFAULT_DOCKER_IMAGE_TAG = "Milestone_8.1";

    protected final ObjectMapper mapper = new ObjectMapper();

    static JobExecutionController jobExecutionController = new JobExecutionController();
    static JobLaunchService jobLaunchService = new JobLaunchService();

    @Override
    public JobExecution start(Job job, JSONObject envVars) {
        log.info("startJob " + job.getId() + " " + job.getName());

        try {
            JSONObject effectiveEnvVars = new JSONObject();
            effectiveEnvVars.putAll(job.getEnvVars());
            effectiveEnvVars.putAll(envVars);

            System.out.println("effectiveEnvVars:  "+ effectiveEnvVars);

            Timestamp deploymentStartDatetime = new Timestamp(new Date().getTime());
            JobExecution jobExecution = new JobExecution();
            jobExecution.setCreatedAt(new Timestamp(System.currentTimeMillis()));

            // Add placeholder handle, if Kubernetes launches successfully it will be replaced with proper handle
            jobExecution.setDeploymentHandle("placeholder-handle-" + deploymentStartDatetime.toString()
                    .replace(":", "_").replace(" ", "_"));
            jobExecution.setJob(job);
            jobExecution.setJobId(job.getId());
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

                DeploymentHandle handle = jobLaunchService.launchJob(job, jobExecution);

                jobExecution.setDeploymentHandle(handle.getName());
                jobExecution.setHandleData(mapper.writeValueAsString(handle));
                jobExecution.setOutputLocation(job.getOutputModel());

                CompletionChecker completionChecker = new CompletionChecker(jobExecution, handle);

                ScheduledFuture<?> future = JobServer.executor.scheduleAtFixedRate
                        (completionChecker, 30, 30, TimeUnit.SECONDS);

                JobServer.currentTimers.put(jobExecution.getDeploymentHandle(), future);

            } catch (Exception e) {
                log.error("Exception during deployment launch", e);
            }

            JobExecution createdJobExecution = jobExecutionController.create(jobExecution);

            return createdJobExecution;
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
