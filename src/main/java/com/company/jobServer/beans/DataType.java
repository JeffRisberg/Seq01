package com.company.jobServer.beans;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table(name = "data_type", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "image_tag"})})
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class DataType extends AbstractDatabaseItem {

  @Column(name = "name", columnDefinition = "TEXT")
  @NotNull
  @ApiModelProperty(required = true, value = "Name")
  private String name = null;

  @Column(name = "description", columnDefinition = "TEXT")
  @ApiModelProperty(value = "Description text")
  private String description = null;
}

