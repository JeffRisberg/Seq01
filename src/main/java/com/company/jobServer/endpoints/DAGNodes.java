package com.company.jobServer.endpoints;

import com.company.jobServer.beans.DAGNode;
import com.company.jobServer.common.RestTools;
import com.company.jobServer.controllers.DAGNodeController;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Path("/v1/dag-nodes")
@Api(value = "DAGNodes",
  description = "Endpoint for managing DAGNodes")
@Slf4j
public class DAGNodes {
  private static final ObjectMapper mapper = new ObjectMapper();

  private static final DAGNodeController dagNodeController = new DAGNodeController();

  @Path("/")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Creates a DAGNode", response = DAGNode.class)
  public Response createDAGNode(DAGNode dagNode) {
    try {
      DAGNode data = dagNodeController.create(dagNode);

      if (data != null) {
        return Response.ok(data).build();
      } else return Response.serverError().build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get all DAGNodes", response = DAGNode.class, responseContainer = "List")
  public Response getAllDAGNodes() {
    try {
      List<DAGNode> dagNodes = dagNodeController.getAll();

      return Response.ok(dagNodes).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/{id}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get DAGNode by Id", response = DAGNode.class, responseContainer = "List")
  public Response getDAGNode(@PathParam("id") long id) {
    try {
      List<DAGNode> dagNodes = new ArrayList<>();

      DAGNode data = dagNodeController.getById(id);
      if (data != null) {
        dagNodes.add(data);
      }
      return Response.ok(dagNodes).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }
}
