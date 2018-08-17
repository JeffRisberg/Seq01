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
@Table(name = "Algorithm", uniqueConstraints = {@UniqueConstraint(columnNames = {"name", "image_tag"})})
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Algorithm extends AbstractDatabaseItem {

  @Column(name = "name", columnDefinition = "TEXT")
  @NotNull
  @ApiModelProperty(required = true, value = "Algorithm name")
  private String name = null;

  @Column(name = "description", columnDefinition = "TEXT")
  @ApiModelProperty(value = "Algorithm description text")
  private String description = null;

  @Column(name = "algorithm_class", columnDefinition = "TEXT")
  @ApiModelProperty(value = "Algorithm class")
  private String algorithmClass = null;

  @Column(name = "ensamble")
  @ApiModelProperty(value = "Is the model an ensamble")
  private Boolean ensamble = null;

  @Column(name = "image_id", columnDefinition = "TEXT")
  @NotNull
  @ApiModelProperty(required = true, value = "Image to run the Model")
  private String imageId = null;

  @Column(name = "image_tag", columnDefinition = "TEXT")
  @ApiModelProperty(value = "Tag for the image (e.g. latest)")
  private String imageTag = null;

  @Column(name = "tenant_based", columnDefinition = "boolean default false")
  @ApiModelProperty(value = "Boolean indicating if Algorithm is tenant-based or not")
  private Boolean tenantBased = false;
}

