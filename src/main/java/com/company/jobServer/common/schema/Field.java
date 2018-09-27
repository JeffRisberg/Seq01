package com.company.jobServer.common.schema;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "fields")
@Data
@AllArgsConstructor
public class Field {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  private Long id;

  private String name;
  private FieldType type;
  private String description;
  private boolean isRequired;

  //private ValueField[] values;
}
