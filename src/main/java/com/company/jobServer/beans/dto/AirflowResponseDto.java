package com.company.jobServer.beans.dto;

import lombok.Data;

@Data
public class AirflowResponseDto {

  protected Integer http_response_code;

  protected String response_time;

  protected String status;

  protected String runId;
}
