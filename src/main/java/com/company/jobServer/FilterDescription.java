package com.company.jobServer;

public class FilterDescription {
  protected String field;
  protected FilterOperator operator;
  protected Object value;

  public FilterDescription(String field, FilterOperator operator, Object value) {
    this.field = field;
    this.operator = operator;
    this.value = value;
  }

  public String getField() {
    return field;
  }

  public FilterOperator getOperator() {
    return operator;
  }

  public Object getValue() {
    return value;
  }
}
