package com.company.jobServer.endpoints;

import com.company.jobServer.beans.JobDependency;
import com.company.jobServer.common.RestTools;
import com.company.jobServer.controllers.JobDependencyController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.List;
import java.util.Optional;

@Path("/v1/job-dependencies")
@Api(value = "JobDependencies",
  description = "Endpoint for managing JobDependencies")
@Slf4j
public class JobDependencies {
  private static final ObjectMapper mapper = new ObjectMapper();

  private static final JobDependencyController jobDependencyController = new JobDependencyController();

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
