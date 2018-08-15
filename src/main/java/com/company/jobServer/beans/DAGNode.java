package com.company.jobServer.beans;

import com.company.jobServer.beans.enums.DAGNodeType;
import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

/**
 * A DAGNode is modeled after the data structures in Airflow.
 *
 * A Node can have an associated Job, which it will run, or it can be a WAIT node, with no Job.
 */
@Data
@Entity
@Table(name = "dag_nodes")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DAGNode extends AbstractDatabaseItem {

  @Column(name = "name")
  @ApiModelProperty(required = true, value = "Name of this node")
  private String name;

  @Column(name ="type")
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(required = true, value = "Type of node")
  private DAGNodeType type;

  @Column(name = "parameters")
  @Convert(converter = JpaConverterJson.class)
  @ApiModelProperty(value = "Parameters")
  private JSONObject parameters;

  @Column(name = "job_id")
  // This can be null
  @ApiModelProperty(required = true, value = "Foreign key to DAGNode's Job")
  private Long jobId;

  @Transient
  @JsonIgnore
  private List<DAGNode> upstreamDAGNodes = new ArrayList<DAGNode>();

  @Transient
  @JsonIgnore
  private List<DAGNode> downstreamDAGNodes = new ArrayList<DAGNode>();
}

