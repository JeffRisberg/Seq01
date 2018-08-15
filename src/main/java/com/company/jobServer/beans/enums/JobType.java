package com.company.jobServer.beans.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JobType {
  COLLECTION("Collection"),
  CONNECTOR("Connector"),
  MODEL("Model");

  private String value;

  JobType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static JobType fromValue(String text) {
    for (JobType b : JobType.values()) {
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
