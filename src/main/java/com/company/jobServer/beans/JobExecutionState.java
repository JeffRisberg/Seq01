package com.company.jobServer.beans;

import com.company.jobServer.beans.enums.JobStatus;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.json.simple.JSONObject;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "job_execution_states")
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionState {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "job_state_id")
  private long jobStateId;

  @Column(name = "handle")
  @ApiModelProperty(required = true)
  private String handle;

  @Column(name = "state_datetime")
  @ApiModelProperty(required = true)
  private Timestamp stateDatetime;

  // this contains counts
  @Convert(converter = JpaConverterJson.class)
  @Column(name = "state_info")
  @ApiModelProperty(value = "JSON as string")
  private JSONObject stateInfo;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  @ApiModelProperty(required = true)
  private JobStatus state;
}
