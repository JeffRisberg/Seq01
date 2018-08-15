package com.company.jobServer.beans;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.sql.Timestamp;

@Data
@Entity
@Table(name = "job_stats_records")
@NoArgsConstructor
@AllArgsConstructor
public class JobExecutionSummary {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "id")
  @ApiModelProperty(value = "Provided by the framework. Set to 0 when registering new JobExecutionSummaries")
  private long id;

  @Column(name = "job_id")
  @ApiModelProperty(required = true, value = "Foreign key to Job")
  private long jobId;

  @Column(name = "handle")
  @ApiModelProperty(required = true, value = "Handle")
  private String handle;

  // This is a state rather than an enum because it was that way in the prior table.
  @Column(name = "state")
  @ApiModelProperty(required = true, value = "State")
  private int state;

  @Column(name = "start_datetime")
  @ApiModelProperty(required = true, value = "When did Job start")
  private Timestamp startDatetime;

  @Column(name = "end_datetime")
  @ApiModelProperty(required = true, value = "When did Job end")
  private Timestamp endDatetime;

  @Column(name = "ticket_count")
  @ApiModelProperty(required = true, value = "Count of tickets processed")
  private long ticketCount;

  @Column(name = "document_count")
  @ApiModelProperty(required = true, value = "Count of documents processed")
  private long documentCount;

  @Column(name = "external_document_count")
  @ApiModelProperty(required = true, value = "Count of external documents processed")
  private long externalDocumentCount;

  @Column(name = "faq_parsed")
  @ApiModelProperty(required = true, value = "Count of faq's processed")
  private long faqParsed;

  @Column(name = "knowledge_article_parsed")
  @ApiModelProperty(required = true, value = "Count of knowledge articles processed")
  private long knowledgeArticleParsed;

  @Column(name = "problem_count")
  @ApiModelProperty(required = true)
  private long problemCount;

  @Column(name = "alert_count")
  @ApiModelProperty(required = true)
  private long alertCount;

  @Column(name = "change_requests_count")
  @ApiModelProperty(required = true)
  private long changeRequestsCount;


}
