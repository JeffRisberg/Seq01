package com.company.jobServer.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

/**
 *
 */
@Data
@Entity
@Table(name = "job_dependencies")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JobDependency extends AbstractDatabaseItem {

  @ManyToOne()
  @JoinColumn(name = "from_id", insertable = false, updatable = false)
  @ApiModelProperty(required = true, value = "Id of 'from' Node")
  @JsonIgnore
  private Job from;

  @Column(name = "from_id")
  @NotNull
  @ApiModelProperty(required = true, value = "Foreign key to upstream dag node")
  private Long fromId;

  @ManyToOne()
  @JoinColumn(name = "to_id", insertable = false, updatable = false)
  @ApiModelProperty(required = true, value = "Id of 'to' Node")
  @JsonIgnore
  private Job to;

  @Column(name = "to_id")
  @NotNull
  @ApiModelProperty(required = true, value = "Foreign key to downstream dag node")
  private Long toId;

  /**
   * Helpful setup method
   *
   * @param from
   * @param to
   */
  public void assign(Job from, Job to) {
    this.from = from;
    this.fromId = from.getId();
    this.to = to;
    this.toId = to.getId();
  }
}

