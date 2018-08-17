package com.company.jobServer.endpoints;

import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobDependency;
import com.company.jobServer.common.RestTools;
import com.company.jobServer.controllers.JobController;
import com.company.jobServer.controllers.JobDependencyController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/v1/job-dependencies")
@Api(value = "JobDependencies",
  description = "Endpoint for managing JobDependencies")
@Slf4j
public class JobDependencies {
  private static final ObjectMapper mapper = new ObjectMapper();

  private static final JobController jobController = new JobController();
  private static final JobDependencyController jobDependencyController = new JobDependencyController();

  @Path("/{fromJobId}/{toJobId}")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Creates a JobDependency", response = JobDependency.class)
  public Response createJobDependency(@PathParam("fromJobId") Long fromJobId, @PathParam("toJobId") Long toJobId) {
    try {
      Job fromJob = jobController.getById(fromJobId);
      Job toJob = jobController.getById(toJobId);

      if (fromJob == null || toJob == null) {
        log.error("Unknown Job");
        return Response.ok(new ArrayList<>()).build();
      }

      JobDependency jobDependency = new JobDependency();
      jobDependency.setCreatedAt(new Timestamp(System.currentTimeMillis()));
      jobDependency.setFrom(fromJob);
      jobDependency.setFromId(fromJobId);

      jobDependency.setTo(toJob);
      jobDependency.setToId(toJobId);

      JobDependency createdJobDependency = jobDependencyController.create(jobDependency);

      if (createdJobDependency != null) {
        return Response.ok(createdJobDependency).build();
      }

      return Response.serverError().build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get all JobDependencies", response = JobDependency.class, responseContainer = "List")
  public Response getAllDAGNodes() {
    try {
      List<JobDependency> jobDependencies = jobDependencyController.getAll();

      return Response.ok(jobDependencies).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }
}
