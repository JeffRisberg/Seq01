package com.company.jobServer.beans;

import com.company.jobServer.common.orchestration.ExecutionType;
import com.company.jobServer.beans.enums.JobStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/**
 * A JobExecution holds the information about one execution of a specific Job.
 */
@Data
@Entity
@Table(name = "job_executions")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JobExecution extends AbstractDatabaseItem {

  @ManyToOne()
  @JoinColumn(name = "job_id", insertable = false, updatable = false)
  @JsonIgnore
  private Job job;

  @Column(name = "job_id")
  @NotNull
  @ApiModelProperty(required = true, value = "Foreign key to Job")
  private Long jobId;

  @Column(name = "tenant_id")
  @ApiModelProperty(value = "Tenant Id")
  private String tenantId;

  @Column(name = "deployment_type")
  @ApiModelProperty(value = "ExecutionType")
  private ExecutionType deploymentType;

  @Convert(converter = JpaConverterJson.class)
  @Column(name = "runtime_params")
  @ApiModelProperty(required = true, value = "JSON Object of params to pass in at startup")
  private JSONObject runtimeParams;

  @Column(name = "last_update", insertable = false, updatable = false)
  @ApiModelProperty(required = false, value = "Time of last Execution start. Assigned by framework")
  private Timestamp lastUpdate;

  @Column(name = "deployment_handle")
  @ApiModelProperty(value = "Deployment handle") // Contains name of handle
  private String deploymentHandle;

  @Column(name = "handle_data")
  @ApiModelProperty(value = "Auto generated by framework") // JSON serialized
  private String handleData;

  @Column(name = "status")
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(value = "ExecutionStatus of the JobExecution")
  private JobStatus status;

  @Column(name = "started_at")
  @ApiModelProperty(value = "JobExecution start time")
  private Timestamp startedAt;

  @Column(name = "stopped_at")
  @ApiModelProperty(value = "JobExecution end time")
  private Timestamp stoppedAt;

  @Column(name = "output_location")
  @ApiModelProperty(value = "OutputLocation for this execution")
  private String outputLocation;
}
