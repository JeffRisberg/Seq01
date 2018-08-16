package com.company.jobServer.services;

import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.common.orchestration.DeployableObject;
import com.company.jobServer.common.orchestration.DeploymentHandle;
import com.company.jobServer.common.orchestration.DeploymentHandleDTO;
import com.company.jobServer.common.orchestration.ExecutionType;
import com.squareup.okhttp.Credentials;
import io.kubernetes.client.ApiClient;
import io.kubernetes.client.Configuration;
import io.kubernetes.client.apis.BatchV1Api;
import io.kubernetes.client.auth.ApiKeyAuth;
import io.kubernetes.client.auth.HttpBasicAuth;
import io.kubernetes.client.models.*;
import io.kubernetes.client.util.Config;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class JobLaunchService {

  static Random random = new Random();

  public JobLaunchService() {
  }

  public DeploymentHandle launchJob(Job job, JobExecution jobExecution) {
    try {
      String configFile = "/Users/jeff/.kube/config";
      ApiClient client = Config.fromConfig(configFile);

      ApiKeyAuth BearerToken = (ApiKeyAuth) client.getAuthentication("BearerToken");

      if (BearerToken.getApiKey() == null) {
        System.out.println("Setting up AppKey");

        BearerToken.setApiKey(Credentials.basic(
          ((HttpBasicAuth) client.getAuthentications().get("BasicAuth")).getUsername(),
          ((HttpBasicAuth) client.getAuthentications().get("BasicAuth")).getPassword()));
      }
      client.setDebugging(true);
      Configuration.setDefaultApiClient(client);

      String namespace = "default";
      String pretty = "TRUE";

      BatchV1Api batchV1Api = new BatchV1Api();

      DeployableObject deployableObject = new DeployableObject();
      deployableObject.setNamespace(namespace);
      deployableObject.setDockerContainerName(job.getDockerImageName());
      deployableObject.setDeploymentType(ExecutionType.Job);
      deployableObject.setName(job.getName());
      deployableObject.setEnv(jobExecution.getEffectiveEnvVars());

      V1Job v1Job = createJob(deployableObject);

      batchV1Api.createNamespacedJob(deployableObject.getNamespace(), v1Job, pretty);

      DeploymentHandle deploymentHandle = new DeploymentHandleDTO();
      deploymentHandle.setName(job.getName());
      deploymentHandle.setNamespace(namespace);

      return deploymentHandle;

    } catch (Exception e) {
      e.printStackTrace();
      return null;
    }
  }

  private V1Job createJob(DeployableObject deployableObject) {
    V1Job job = new V1Job();
    V1ObjectMeta metadata = new V1ObjectMeta();
    metadata.setName(generateUniqueName(deployableObject.getName()));
    metadata.setNamespace(deployableObject.getNamespace());
    job.setMetadata(metadata);

    V1JobSpec jobSpec = new V1JobSpec();
    V1PodTemplateSpec podTemplate = new V1PodTemplateSpec();
    V1PodSpec podSpec = new V1PodSpec();
    podSpec.setRestartPolicy("Never");

    List<V1Container> containers = new ArrayList<>();
    V1Container container = new V1Container();
    container.setImage(deployableObject.getDockerContainerName());
    container.setName(deployableObject.getName());

    List<V1EnvVar> env = createEnv(deployableObject, metadata);
    container.setEnv(env);
    //setEnvFromConfigMap(container, "configMap123");

    if (deployableObject.getCommand() != null && deployableObject.getCommand().size() > 0) {
      container.setCommand(deployableObject.getCommand());
      container.setArgs(deployableObject.getArgs());
    }
    container.imagePullPolicy("Always");

    containers.add(container);

    podSpec.setContainers(containers);
    podTemplate.setSpec(podSpec);
    jobSpec.setTemplate(podTemplate);
    job.setSpec(jobSpec);
    return job;
  }

  private void addPubsubConfigToEnv(List<V1EnvVar> env) {
    /* Set the container's pubsub env's to that
     * of the host that's launching it !
     */
    V1EnvVar envVar1 = new V1EnvVar();
    envVar1.setName("company_pubsub_host_ports");
    envVar1.setValue("kafka:9092");
    env.add(envVar1);

    V1EnvVar envVar2 = new V1EnvVar();
    envVar2.setName("company_io_python_pubsub_plugin");
    envVar2.setValue("kafka");
    env.add(envVar2);
  }

  private List<V1EnvVar> createEnv(DeployableObject deployableObject, V1ObjectMeta metadata) {
    List<V1EnvVar> env = new ArrayList<V1EnvVar>();
    addPubsubConfigToEnv(env);
    V1EnvVar envVar = new V1EnvVar();

        /*
        envVar.setName(company_INPUT_CONFIG);
        envVar.setValue(deployableObject.getInputParams());
        env.add(envVar);

        envVar = new V1EnvVar();
        envVar.setName(company_CONNECTOR_CONFIG);
        envVar.setValue(deployableObject.getInputParams());
        env.add(envVar);

        envVar = new V1EnvVar();
        envVar.setName(company_TRAINING_INPUT_CONFIG);
        envVar.setValue(deployableObject.getInputParams());
        env.add(envVar);

        envVar = new V1EnvVar();
        envVar.setName(company_DEPLOYMENT_HANDLE);
        envVar.setValue(metadata.getName());
        env.add(envVar);

        envVar = new V1EnvVar();
        envVar.setName("company_kv_redis_host");
        envVar.setValue("redis");
        env.add(envVar);
        */

    if (deployableObject.getEnv() != null) {
      for (Map.Entry<String, String> e : deployableObject.getEnv().entrySet()) {
        envVar = new V1EnvVar();
        envVar.setName(e.getKey());
        envVar.setValue(e.getValue());
        env.add(envVar);
      }
    }
    return env;
  }

  private void setEnvFromConfigMap(V1Container container, String name) {
    List<V1EnvFromSource> envFrom = container.getEnvFrom();
    if (envFrom == null)
      envFrom = new ArrayList<>();
    V1EnvFromSource envSourceConfigMap = new V1EnvFromSource();
    V1ConfigMapEnvSource configMap = new V1ConfigMapEnvSource();
    configMap.setName(name);
    envSourceConfigMap.configMapRef(configMap);
    envFrom.add(envSourceConfigMap);
    container.setEnvFrom(envFrom);
  }

  private String generateUniqueName(String name) {
    return name + "-" + System.currentTimeMillis() + "-" + random.nextInt(99);
  }
}
