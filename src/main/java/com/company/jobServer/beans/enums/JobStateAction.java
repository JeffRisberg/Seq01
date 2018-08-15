package com.company.jobServer.beans.enums;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class JobStateAction {
  public static final String PAUSE_ACTION = "pause";
  public static final String RESUME_ACTION = "resume";
  public static final String STOP_ACTION = "stop";
  public static final String START_ACTION = "start";

  @ApiModelProperty(required = true, value = "Set to pause/resume/start/stop", allowableValues = "start,stop,pause,resume")
  private String action;

  public JobStateAction() {
  }

  public JobStateAction(String action) {
    this.action = action;
  }

  public String getAction() {
    return action;
  }

  public void setAction(String action) {
    this.action = action;
  }
}
