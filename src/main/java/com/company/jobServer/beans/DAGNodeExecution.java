package com.company.jobServer.beans;

import com.company.jobServer.beans.enums.JobStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

/*
 * A DAGNodeExecution holds the information about one execution of a DAGNode which has an associated Job.
 */
@Data
@Entity
@Table(name = "dag_node_executions")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DAGNodeExecution extends AbstractDatabaseItem {

  @ManyToOne()
  @JoinColumn(name = "dag_node_id", insertable = false, updatable = false)
  @JsonIgnore
  private DAGNode dagNode;

  @Column(name = "dag_node_id")
  @NotNull
  @ApiModelProperty(required = true, value = "Foreign key to dag node")
  private Long dagNodeId;

  @ApiModelProperty(value = "ExecutionStatus of the DagNodeJob")
  @Enumerated(EnumType.STRING)
  private JobStatus status;

  @Column(name = "started_at")
  @ApiModelProperty(value = "DagNodeJob start time")
  private Timestamp startedAt;

  @Column(name = "stopped_at")
  @ApiModelProperty(value = "DagNodeJob end time")
  private Timestamp stoppedAt;

}

