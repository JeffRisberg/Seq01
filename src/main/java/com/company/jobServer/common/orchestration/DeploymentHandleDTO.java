package com.company.jobServer.common.orchestration;

import lombok.Data;

@Data
public class DeploymentHandleDTO implements DeploymentHandle {
  private String namespace;
  private String name;
  private String uuid;
  private ExecutionType deploymentType = ExecutionType.CronJob;
  private String containerService;
  private String internalEndpoint;
  private String externalEndpoint;

  @Override
  public String getNamespace() {
    return namespace;
  }

  @Override
  public void setNamespace(String namespace) {
    this.namespace = namespace;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public void setName(String name) {
    this.name = name;
  }

  public String getUUID() {
    return uuid;
  }

  public void setUUID(String uuid) {
    this.uuid = uuid;
  }

  @Override
  public ExecutionType getDeploymentType() {
    return deploymentType;
  }

  @Override
  public void setDeploymentType(ExecutionType deploymentType) {
    this.deploymentType = deploymentType;
  }

  @Override
  public String getContainerService() {
    return containerService;
  }

  @Override
  public void setContainerService(String containerService) {
    this.containerService = containerService;
  }

  @Override
  public String getInternalEndpoint() {
    return internalEndpoint;
  }

  @Override
  public void setInternalEndpoint(String internalEndpoint) {
    this.internalEndpoint = internalEndpoint;
  }

  @Override
  public String getExternalEndpoint() {
    return externalEndpoint;
  }

  @Override
  public void setExternalEndpoint(String externalEndpoint) {
    this.externalEndpoint = externalEndpoint;
  }
}
