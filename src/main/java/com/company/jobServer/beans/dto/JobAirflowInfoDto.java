package com.company.jobServer.beans.dto;

import com.company.jobServer.beans.enums.JobType;
import lombok.Data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class JobAirflowInfoDto {

  protected Long id;

  protected String tenantId;

  protected String name;

  protected String step;

  protected JobType jobType;

  protected String jobSubType;

  protected List<String> functions;

  protected String image;

  protected Map<String, String> envVars = new HashMap<String, String>();

  protected String outputModel;

  protected List<String> containerArgs;

  protected List<String> containerCommands;

}
