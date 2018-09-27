package com.company.jobServer.common.job;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JobServiceType {
  ParserService("Parser Service"),
  AnalyticsService("Analytics Service"),
  GraphFunction("Graph Function"),
  IncidentService("Incident Service"),
  IndexerService("Indexer Service"),
  NLPService("NLP Service");

  private String value;

  JobServiceType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static JobServiceType fromValue(String text) {
    for (JobServiceType b : JobServiceType.values()) {
      if (String.valueOf(b.value).equals(text)) {
        return b;
      }
      if (b.name().equals(text)) {
        return b;
      }
    }
    return null;
  }
}
