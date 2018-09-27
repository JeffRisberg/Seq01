package com.company.jobServer.common.job;

import lombok.Data;

@Data
public class JobExecutionCountDto {
  protected long inputValid;
  protected long inputInvalid;
  protected long outputValid;
  protected long outputInvalid;

  public void incrementInputValid(long count) { this.inputValid += count; }
  public void incrementInputInValid(long count) { this.inputInvalid += count; }
  public void incrementOutputValid(long count) { this.outputValid += count; }
  public void incrementOutputInValid(long count) { this.outputInvalid += count; }
}
