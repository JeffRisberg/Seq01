package com.company.jobServer.beans.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum ClusterType {
  KUBERNETES("Kubernetes"),
  DOCKER("Docker");

  private String value;

  ClusterType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static ClusterType fromValue(String text) {
    for (ClusterType b : ClusterType.values()) {
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

