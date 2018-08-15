package com.company.jobServer.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "job_execution_steps")
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JobExecutionStep extends AbstractDatabaseItem {

  @ManyToOne()
  @JoinColumn(name = "job_execution_id", insertable = false, updatable = false)
  @JsonIgnore
  private JobExecution jobExecution;

  @Column(name = "job_execution_id")
  @NotNull
  @ApiModelProperty(required = true, value = "Foreign key to JobExecution")
  private Long jobExecutionId;

  @Column(name = "step")
  @ApiModelProperty(value = "Step")
  private String step;

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

  @Column(name = "done")
  @ApiModelProperty(value = "Done")
  private boolean done;

  @Column(name = "last_update")
  @ApiModelProperty(value = "Last update")
  private Timestamp lastUpdate;
}
