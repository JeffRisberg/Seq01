package com.company.jobServer.common.orchestration;

public enum Status {
  Pending(0), Running(1), Succeeded(2), Failed(3), Unknown(4);

  Status(int status) {
    statusCode = status;
  }

  public int getStatusCode() {
    return statusCode;
  }

  private final int statusCode;

}
