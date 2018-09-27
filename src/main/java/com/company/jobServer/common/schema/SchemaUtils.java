package com.company.jobServer.common.schema;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 */
public class SchemaUtils {

  // temporary store until we get these into database
  static protected Map<ConnectorTypeEnum, List<Field>> schemaByConnectorType = null;

  public SchemaUtils() {
    if (schemaByConnectorType == null) {
      schemaByConnectorType = new HashMap<ConnectorTypeEnum, List<Field>>();

      List<Field> salesforceFields = new ArrayList<Field>();
      salesforceFields.add
        (new Field(null, "id", FieldType.Integer, "", false));
      salesforceFields.add
        (new Field(null, "title", FieldType.String, "", false));
      salesforceFields.add
        (new Field(null, "description", FieldType.String, "", false));
      salesforceFields.add
        (new Field(null, "priority", FieldType.String, "", false));
      salesforceFields.add
        (new Field(null, "solution", FieldType.String, "", false));
      salesforceFields.add
        (new Field(null, "creationDate", FieldType.Date, "", true));
      salesforceFields.add
        (new Field(null, "lastUpdateDate", FieldType.Date, "", true));
      salesforceFields.add
        (new Field(null, "LinkedContent", FieldType.String, "", true));
      salesforceFields.add
        (new Field(null, "Answer__c", FieldType.String, "", true));
      salesforceFields.add
        (new Field(null, "Question__c", FieldType.String, "", true));
      schemaByConnectorType.put(ConnectorTypeEnum.Salesforce, salesforceFields);

      List<Field> jiraFields = new ArrayList<Field>();
      jiraFields.add
        (new Field(null, "id", FieldType.Integer, "", false));
      jiraFields.add
        (new Field(null, "title", FieldType.String, "", true));
      jiraFields.add
        (new Field(null, "description", FieldType.String, "", true));
      jiraFields.add
        (new Field(null, "priority", FieldType.String, "", true));
      jiraFields.add
        (new Field(null, "solution", FieldType.String, "", false));
      jiraFields.add
        (new Field(null, "status", FieldType.String, "", false));
      jiraFields.add
        (new Field(null, "creationDate", FieldType.Date, "", true));
      jiraFields.add
        (new Field(null, "lastUpdateDate", FieldType.Date, "", true));
      schemaByConnectorType.put(ConnectorTypeEnum.Jira, jiraFields);

      List<Field> snowFields = new ArrayList<Field>();
      snowFields.add
        (new Field(null, "id", FieldType.Integer, "", false));
      snowFields.add
        (new Field(null, "title", FieldType.String, "", true));
      snowFields.add
        (new Field(null, "description", FieldType.String, "", true));
      snowFields.add
        (new Field(null, "priority", FieldType.String, "", true));
      snowFields.add
        (new Field(null, "solution", FieldType.String, "", false));
      snowFields.add
        (new Field(null, "status", FieldType.String, "", false));
      snowFields.add
        (new Field(null, "creationDate", FieldType.Date, "", true));
      snowFields.add
        (new Field(null, "lastUpdateDate", FieldType.Date, "", true));
      schemaByConnectorType.put(ConnectorTypeEnum.ServiceNow, snowFields);
    }
  }

  static public List<Field> getFields(ConnectorTypeEnum connectorType) {
    return schemaByConnectorType.get(connectorType);
  }
}
