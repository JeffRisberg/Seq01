package com.company.jobServer.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

// An input or output

@Data
@Entity
@Table(name = "algorithm_data_type")
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AlgorithmDataType extends AbstractDatabaseItem {

  @ManyToOne()
  @JoinColumn(name = "algorithm_id", insertable = false, updatable = false)
  @ApiModelProperty(required = true, value = "")
  @JsonIgnore
  private Algorithm algorithm;

  @Column(name = "algorithm_id")
  @NotNull
  @ApiModelProperty(required = true, value = "")
  private Long fromId;

  @ManyToOne()
  @JoinColumn(name = "data_type_id", insertable = false, updatable = false)
  @ApiModelProperty(required = true, value = "")
  @JsonIgnore
  private DataType to;

  @Column(name = "data_type_id")
  @NotNull
  @ApiModelProperty(required = true, value = "")
  private Long toId;

  @Column
  private Boolean produces;

  @Column
  private Boolean consumes;
}

