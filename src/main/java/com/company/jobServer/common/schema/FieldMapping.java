package com.company.jobServer.common.schema;

import lombok.AllArgsConstructor;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table(name = "field_mappings")
@Data
@AllArgsConstructor
public class FieldMapping {
  @Id
  @Column(name = "id")
  protected Long id;

  // Scoping information

  @Column(name = "tenant_id")
  @NotNull
  protected String tenantId;

  @Column(name = "entity_name")
  @NotNull
  protected String entityName;

  @Column(name = "version")
  protected Integer version;

  @Column(name = "seq_num")
  @NotNull
  protected int seqNum;

  @Column(name = "mandatory")
  protected boolean mandatory;

  // Aisera-side information

  @Column(name = "field_name")
  protected String fieldName;

  @Column(name = "field_type")
  protected FieldType fieldType;

  @Column(name = "field_enum_values")
  protected String fieldNumValues;

  // External-side information

  @Column(name = "external_field_name")
  protected String externalFieldName;

  @Column(name = "external_field_type")
  protected FieldType externalFieldType;

  @Column(name = "external_field_enum_values")
  protected String externalFieldNumValues;
}
