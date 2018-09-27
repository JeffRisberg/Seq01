package com.company.jobServer.beans;

import com.company.jobServer.common.job.JobServiceType;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "job_execution_counts")
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionCount extends AbstractDatabaseItem {
  @Column(name = "job_execution_id")
  @NotNull
  @ApiModelProperty(required = true, value = "Foreign key to JobExecution")
  private Long jobExecutionId;

  @Column(name = "service_type")
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(required = true, value = "JobServiceType")
  protected JobServiceType serviceType;

  @Column(name = "input_valid")
  @ApiModelProperty(value = "Input valid")
  private Long inputValid;

  @Column(name = "input_invalid")
  @ApiModelProperty(value = "Input Invalid")
  private Long inputInvalid;

  @Column(name = "output_valid")
  @ApiModelProperty(value = "Output valid")
  private Long outputValid;

  @Column(name = "output_invalid")
  @ApiModelProperty(value = "Output Invalid")
  private Long outputInvalid;
}
