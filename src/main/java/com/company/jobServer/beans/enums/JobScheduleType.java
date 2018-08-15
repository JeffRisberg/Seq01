package com.company.jobServer.beans.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum JobScheduleType {
  CRON("Cron"),
  ONDEMAND("On Demand"),
  EVENTDRIVEN("Event Driven");

  private String value;

  JobScheduleType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static JobScheduleType fromValue(String text) {
    for (JobScheduleType b : JobScheduleType.values()) {
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

