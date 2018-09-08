package com.company.jobServer.beans;

import com.company.jobServer.beans.dto.StreamDataType;
import com.company.jobServer.beans.enums.ClusterType;
import com.company.jobServer.beans.enums.JobScheduleType;
import com.company.jobServer.beans.enums.JobType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "jobs")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Job extends AbstractDatabaseItem {

  @ManyToOne()
  @JoinColumn(name = "parent_id", insertable = false, updatable = false)
  @ApiModelProperty(required = true, value = "Id of parent Job")
  @JsonIgnore
  private Job parent;

  @Column(name = "parent_id")
  @ApiModelProperty(required = true, value = "Foreign key to parent Job")
  private Long parentId;

  @Column(name = "name", unique = true)
  @ApiModelProperty(required = true, value = "Job name")
  private String name;

  @Column(name = "description", columnDefinition = "TEXT")
  @ApiModelProperty(required = true, value = "Job Description")
  private String description = null;

  @Column(name = "reference_id")
  @ApiModelProperty(value = "Reference id")
  private String referenceId = null; // may be null

  @Column(name = "tenant_id")
  @ApiModelProperty(value = "TenantId")
  protected String tenantId; // may be null

  @Column(name = "job_type")
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(required = true, value = "JobType")
  protected JobType jobType;

  @Column(name = "docker_image_name")
  @ApiModelProperty(value = "Docker image name")
  private String dockerImageName; //  may be null

  @Convert(converter = JpaConverterJsonArray.class)
  @Column(name = "functions")
  @ApiModelProperty(value = "Functions")
  private JSONArray functions; // This is an array of strings, not an object

  @Column(name = "compute_resource")
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(value = "Compute Resource")
  private ClusterType computeResource = ClusterType.KUBERNETES;

  @Column(name = "job_schedule_type")
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(required = true, value = "JobScheduleType")
  private JobScheduleType jobScheduleType = JobScheduleType.ONDEMAND;

  @Column(name = "cron_schedule", columnDefinition = "TEXT")
  @ApiModelProperty(value = "Cron schedule string")
  private String cronSchedule = null; // applies only for scheduleType="CRON"

  @Convert(converter = JpaConverterJson.class)
  @Column(name = "env_vars")
  @ApiModelProperty(required = true, value = "JSON Object of params to pass in at startup")
  private JSONObject envVars;

  @Column(name = "hyper_parameters")
  @Convert(converter = JpaConverterJson.class)
  @ApiModelProperty(value = "Hyper-Parameters")
  private JSONObject hyperParameters;

  @Column(name = "output_model")
  @ApiModelProperty(value = "Output Model location")
  private String outputModel;

  @Column(name = "input_data_type")
  @Enumerated(EnumType.STRING)
  private StreamDataType inputDataType;

  @Column(name = "output_data_type")
  @Enumerated(EnumType.STRING)
  private StreamDataType outputDataType;

  public Job(Job parent, String name, String description, JobType jobType, String dockerImageName) {
    this.parent = parent;
    this.parentId = parent != null ? parent.getId() : null;
    this.name = name;
    this.description = description;
    this.jobType = jobType;
    this.dockerImageName = dockerImageName;
  }
}

