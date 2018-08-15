package com.company.jobServer.endpoints;

import com.company.jobServer.beans.DAGNodeDependency;
import com.company.jobServer.common.RestTools;
import com.company.jobServer.controllers.DAGNodeDependencyController;
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

@Path("/v1/dag-node-dependencies")
@Api(value = "DAGNodeDependencies",
  description = "Endpoint for managing DAGNodeDependencies")
@Slf4j
public class DAGNodeDependencies {
  private static final ObjectMapper mapper = new ObjectMapper();

  private static final DAGNodeDependencyController dagNodeDependencyController = new DAGNodeDependencyController();

  @Path("/")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get all DAGNodeDependencies", response = DAGNodeDependency.class, responseContainer = "List")
  public Response getAllDAGNodes() {
    try {
      List<DAGNodeDependency> dagNodes = dagNodeDependencyController.getAll();

      return Response.ok(dagNodes).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }
}
