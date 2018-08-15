package com.company.jobServer.endpoints;

import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.beans.JobExecutionState;
import com.company.jobServer.beans.enums.JobStatus;
import com.company.jobServer.common.RestTools;
import com.company.jobServer.controllers.JobExecutionController;
import com.company.jobServer.controllers.JobExecutionSummaryController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/v1/job-executions")
@Api(value = "JobExecutions",
  description = "Endpoint for managing JobExecutions")
@Slf4j
public class JobExecutions {
  private static final JobExecutionController jobExecutionController = new JobExecutionController();
  private static final JobExecutionSummaryController jobExecutionSummaryController = new JobExecutionSummaryController();

  @Path("/")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get all JobExecutions", response = JobExecution.class, responseContainer = "List")
  public Response getAllJobExecutions() {
    try {
      List<JobExecution> jobExecutions = jobExecutionController.getAll();

      return Response.ok(jobExecutions).build();
    } catch (Exception e) {
      return Response.serverError().build();
    }
  }

  @Path("/{id}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get JobExecution by Id", response = JobExecution.class, responseContainer = "List")
  public Response getJobExecution(@PathParam("id") long id) {
    try {
      List<JobExecution> jobExecutions = new ArrayList<>();

      JobExecution data = jobExecutionController.getById(id);
      if (data != null) {
        jobExecutions.add(data);
      }
      return Response.ok(jobExecutions).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/handle/{handle}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get JobExecutions by deployment handle", response = JobExecution.class, responseContainer = "List")
  public Response getJobExecutionsByHandle(@PathParam("handle") String handle) {
    try {
      List<JobExecution> jobExecutions = jobExecutionController.getByHandle(handle);

      return Response.ok(jobExecutions).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/{id}")
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Removes a JobExecution by Id")
  public Response deleteJobExecution(@PathParam("id") long id) {
    try {
      JobExecution data = jobExecutionController.getById(id);
      if (data == null)
        return Response.serverError().entity(RestTools.getErrorJson("JobExecution does not exist in DB", false, Optional.empty())).build();

      if (jobExecutionController.delete(id)) {
        return Response.ok().build();
      } else
        return Response.serverError().entity(RestTools.getErrorJson("Unable to unregister JobExecution", false, Optional.empty())).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/")
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Update a JobExecution's state", response = JobExecution.class)
  public Response updateJobExecutionState(JobExecutionState jobExecutionState) {
    try {
      if (jobExecutionController.updateState(jobExecutionState)) {
        JobExecution jobExecution;
        List<JobExecution> jobExecutions = jobExecutionController.getByHandle(jobExecutionState.getHandle());
        if (jobExecutions != null && jobExecutions.size() == 1) {
          jobExecution = jobExecutions.get(0);
        } else {
          return Response.serverError().build();
        }

        if (jobExecution != null) {
          jobExecutionSummaryController.updateSummaryCounts(jobExecutionState, jobExecution.getJobId());
        }

        return Response.ok(jobExecution).build();
      } else return Response.serverError().build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/{jobExecutionId}/update-final-status/{jobStatus}")
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Update a JobExecution's status", response = JobExecution.class)
  public Response updateFinalJobExecutionStatus(@PathParam("jobExecutionId") Long jobExecutionId,
                                                @PathParam("jobStatus") String jobStatus) {
    try {

      JobExecution jobExecution = jobExecutionController.getById(jobExecutionId);
      if (jobExecution != null) {
        JobStatus js = JobStatus.valueOf(jobStatus);
        jobExecution = jobExecutionController.updateFinalStatus(jobExecution, js);
        return Response.ok(jobExecution).build();
      } else {
        return Response.serverError().build();
      }
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }
}
