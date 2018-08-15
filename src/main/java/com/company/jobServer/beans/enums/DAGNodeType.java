package com.company.jobServer.beans.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum DAGNodeType {  // like "operator"
  EXECUTE("Execute"),
  WAIT("Wait");

  private String value;

  DAGNodeType(String value) {
    this.value = value;
  }

  @Override
  @JsonValue
  public String toString() {
    return String.valueOf(value);
  }

  @JsonCreator
  public static DAGNodeType fromValue(String text) {
    for (DAGNodeType b : DAGNodeType.values()) {
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
