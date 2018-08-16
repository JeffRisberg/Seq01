package com.company.jobServer.common.orchestration;

public interface DeploymentHandle {

  public String getNamespace();

  public void setNamespace(String namespace);

  public String getName();

  public void setName(String name);

  public String getUUID();

  public void setUUID(String UUID);

  public ExecutionType getDeploymentType();

  public void setDeploymentType(ExecutionType deploymentType);

  public String getContainerService();

  public void setContainerService(String containerService);

  public String getInternalEndpoint();

  public void setInternalEndpoint(String internalEndpoint);

  public String getExternalEndpoint();

  public void setExternalEndpoint(String externalEndpoint);
}
