package com.company.jobServer.endpoints;

import com.company.jobServer.beans.DAGNodeExecution;
import com.company.jobServer.common.RestTools;
import com.company.jobServer.controllers.DAGNodeExecutionController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/v1/dag-node-executions")
@Api(value = "DAGNodeExecutions",
  description = "Endpoint for managing DAGNodeExecutions")
@Slf4j
public class DAGNodeExecutions {
  private static final ObjectMapper mapper = new ObjectMapper();

  private static final DAGNodeExecutionController dagNodeExecutionController = new DAGNodeExecutionController();

  @Path("/")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get all DAGNodeExecutions", response = DAGNodeExecution.class, responseContainer = "List")
  public Response getAllDAGNodes() {
    try {
      List<DAGNodeExecution> dagNodeExecutions = dagNodeExecutionController.getAll();

      return Response.ok(dagNodeExecutions).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/{id}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get DAGNodeExecution by Id", response = DAGNodeExecution.class, responseContainer = "List")
  public Response getDAGNode(@PathParam("id") long id) {
    try {
      List<DAGNodeExecution> dagNodeExecutions = new ArrayList<>();

      DAGNodeExecution data = dagNodeExecutionController.getById(id);
      if (data != null) {
        dagNodeExecutions.add(data);
      }
      return Response.ok(dagNodeExecutions).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }
}
