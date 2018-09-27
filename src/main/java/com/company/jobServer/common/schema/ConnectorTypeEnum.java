package com.company.jobServer.common.schema;

import com.aisera.common.OutputConnectorType;
import lombok.NonNull;

import java.util.Arrays;

/**
 * Enum for Information about Connector Types. Id and Name of ConnectorType are used across UI, back
 * end and DB.  These can't be changed just in code. Any change here - requires changes across the stack.
 */
public enum ConnectorTypeEnum {
  Salesforce(1, OutputConnectorType.SalesForce),
  Zendesk(2, OutputConnectorType.Zendesk),
  Jira(3, OutputConnectorType.Jira),
  Confluence(4, OutputConnectorType.Confluence),
  Crawler(5, OutputConnectorType.Crawler),
  BMC(6, OutputConnectorType.BMC),
  ServiceNow(7, OutputConnectorType.ServiceNow),
  SharePoint(8, OutputConnectorType.SharePoint),
  QnA(9, OutputConnectorType.QnA),
  Upload(10, OutputConnectorType.Upload),
  CustomAction(11, OutputConnectorType.CustomAction);

  private int id; // matches database id for the Connector Type
  private OutputConnectorType outputConnectorType;

  ConnectorTypeEnum(@NonNull Integer id, @NonNull OutputConnectorType outputConnectorType) {
    this.id = id;
    this.outputConnectorType = outputConnectorType;
  }

  public int getId() {
    return id;
  }

  public OutputConnectorType getOutputConnectorType() {
    return outputConnectorType;
  }

  public static ConnectorTypeEnum getConnectorType(int id) {
    return Arrays.stream(ConnectorTypeEnum.values()).filter(type -> type.id == id).findFirst().get();
  }

  public static ConnectorTypeEnum getConnectorType(String name) {
    return Arrays.stream(ConnectorTypeEnum.values()).filter(type -> type.name().equalsIgnoreCase(name)).findFirst().get();
  }

  public static ConnectorTypeEnum getConnectorType(OutputConnectorType outputConnectorType) {
    return Arrays.stream(ConnectorTypeEnum.values()).filter(type -> type.getOutputConnectorType().equals(outputConnectorType)).findFirst().get();
  }
}
