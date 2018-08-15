package com.company.jobServer.beans;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.sql.Timestamp;

@Data
@MappedSuperclass
@NoArgsConstructor
@AllArgsConstructor
public abstract class AbstractDatabaseItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  @ApiModelProperty(value = "Object Id")
  private Long id = null;

  @Column(name = "created_at")
  @NotNull
  @CreationTimestamp
  @ApiModelProperty(required = true, value = "Assigned by framework at Create Time")
  private Timestamp createdAt;

  @Column(name = "updated_at")
  @UpdateTimestamp
  @ApiModelProperty(required = false, value = "Assigned by framework at Update Time")
  private Timestamp updatedAt;
}
