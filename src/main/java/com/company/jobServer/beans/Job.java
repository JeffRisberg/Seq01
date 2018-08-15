package com.company.jobServer.beans;

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
  private ClusterType computeResource = ClusterType.DOCKER;

  @Column(name = "job_schedule_type")
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(required = true, value = "JobScheduleType")
  private JobScheduleType jobScheduleType = JobScheduleType.CRON;

  @Column(name = "cron_schedule", columnDefinition = "TEXT")
  @ApiModelProperty(value = "Cron schedule string")
  private String cronSchedule = null; // applies only for scheduleType="CRON"

  @Convert(converter = JpaConverterJson.class)
  @Column(name = "runtime_params")
  @ApiModelProperty(required = true, value = "JSON Object of params to pass in at startup")
  private JSONObject runtimeParams;

  @Column(name = "last_update", insertable = false, updatable = false)
  @ApiModelProperty(required = false, value = "Time of last Execution start. Assigned by framework")
  private Timestamp lastUpdate;

  @Column(name = "hyper_parameters")
  @Convert(converter = JpaConverterJson.class)
  @ApiModelProperty(value = "Hyper-Parameters")
  private JSONObject hyperParameters;

  @Column(name = "output_model")
  @ApiModelProperty(value = "Output Model location")
  private String outputModel;

  // None of these are actually used yet, but we will leave the columns anyway
  @Column(name = "cpu", columnDefinition = "TEXT")
  @ApiModelProperty(value = "Specifies CPU reservation. Format for Kubernetes")
  private String cpu = null;

  @Column(name = "memory", columnDefinition = "TEXT")
  @ApiModelProperty(value = "Specifies memory reservation. Format for Kubernetes")
  private String memory = null;

  @Column(name = "volume_size", columnDefinition = "TEXT")
  @ApiModelProperty(value = "The size of the ML storage volume that you want to provision")
  private String volumeSizeInGB = null;

  @Column(name = "max_concurrent_requests_per_container")
  @ApiModelProperty(value = "Maximum number of concurrent requests. Minimum value: 1")
  private Long maxConcurrentRequestsPerContainer = null;

  @Column(name = "autoscale_enabled")
  @ApiModelProperty(value = "Enable or disable the autoscaler.")
  private Boolean autoscaleEnabled = null;

  @Column(name = "max_replicas")
  @ApiModelProperty(value = "Maximum number of pod replicas to scale up to. Minimum value: 1")
  private Long maxReplicas = null;

  @Column(name = "min_replicas")
  @ApiModelProperty(value = "Minimum number of pod replicas to scale down to. Minimum value: 0")
  private Long minReplicas = null;

  @Column(name = "refresh_period_in_seconds")
  @ApiModelProperty(value = "Refresh time for autoscaling trigger. Minimum value: 1")
  private Long refreshPeriodInSeconds = null;

  @Column(name = "target_utilization")
  @ApiModelProperty(value = "Utilization percentage that triggers autoscaling. Minimum value: 0. Maximum value: 100")
  private Long targetUtilization = null;
}

