package com.company.jobServer.beans.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JobStatus {
  PENDING("PENDING"),
  RUNNING("RUNNING"),
  SUCCEEDED("SUCCEEDED"),
  FAILED("FAILED"),
  KILLED("KILLED"),
  UNKNOWN("UNKNOWN");

  private String value;

  JobStatus(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static JobStatus fromValue(String text) {
    for (JobStatus b : JobStatus.values()) {
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

