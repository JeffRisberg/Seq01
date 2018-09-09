package com.company.jobServer.endpoints;

import com.company.jobServer.FilterDescription;
import com.company.jobServer.FilterOperator;
import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.beans.enums.JobType;
import com.company.jobServer.common.RestTools;
import com.company.jobServer.controllers.JobController;
import com.company.jobServer.controllers.JobDependencyController;
import com.company.jobServer.controllers.JobExecutionController;
import com.company.jobServer.executors.CollectionJobExecutor;
import com.company.jobServer.executors.ConnectorJobExecutor;
import com.company.jobServer.executors.ModelJobExecutor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Path("/v1/jobs")
@Api(value = "Jobs",
  description = "Endpoint for managing Jobs")
@Slf4j
public class Jobs {
  private final static String DOCKER_REPOSITORY_URI = "company.docker.repository.uri";
  private final static String DEFAULT_DOCKER_REPOSITORY_URI = "934101271236.dkr.ecr.us-west-2.amazonaws.com";

  private final static String DOCKER_IMAGE_TAG = "company.docker.image.tag";
  private final static String DEFAULT_DOCKER_IMAGE_TAG = "Milestone_8.2";

  private static final Random random = new Random();
  private static final JobController jobController = new JobController();
  private static final JobExecutionController jobExecutionController = new JobExecutionController();
  private static final JobDependencyController dagNodeDependencyController = new JobDependencyController();

  private static final ConnectorJobExecutor connectorJobExecutor = new ConnectorJobExecutor();
  private static final ModelJobExecutor modelJobExecutor = new ModelJobExecutor();
  private static final CollectionJobExecutor collectionJobExecutor = new CollectionJobExecutor();

  @Path("/")
  @POST
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Creates a Job", response = Job.class)
  public Response createJob(Job givenJob) {
    try {
      Job data = jobController.create(givenJob);

      if (data != null) {
        return Response.ok(data).build();
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
  @ApiOperation(value = "Get all Jobs", response = Job.class, responseContainer = "List")
  public Response getAllJobs() {
    try {
      List<Job> jobs = jobController.getAll();

      return Response.ok(jobs).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/{jobId}")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get Job by Id", response = Job.class, responseContainer = "List")
  public Response getJob(@PathParam("jobId") long jobId) {
    try {
      List<Job> jobs = new ArrayList<>();

      Job data = jobController.getById(jobId);
      if (data != null) {
        jobs.add(data);
      }
      return Response.ok(jobs).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/reference-id/{id}")
  @GET
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get Job by Reference Id", response = Job.class)
  public Response getJobByReferenceIdAndJobType(@PathParam("id") String referenceId) {
    try {
      List<Job> jobs = jobController.getByReferenceId(referenceId);
      Job data = null;
      if (jobs != null && jobs.size() > 0) {
        data = jobs.get(0);
      }
      return Response.ok(data).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/query")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Query for Jobs using criteria", response = Job.class, responseContainer = "List")
  public Response queryJobs(@QueryParam("name") String name,
                            @QueryParam("referenceId") String refId,
                            @QueryParam("jobType") JobType type,
                            @QueryParam("createdAtAfter") RESTTimestampParam createdAtAfter,
                            @QueryParam("createdAtBefore") RESTTimestampParam createdAtBefore,
                            @QueryParam("updatedAtAfter") RESTTimestampParam updatedAtAfter,
                            @QueryParam("updatedAtBefore") RESTTimestampParam updatedAtBefore,
                            @QueryParam("cpu") String cpu,
                            @QueryParam("memory") String memory) {
    try {
      List<FilterDescription> filterDescriptions = new ArrayList<>();

      if (name != null) {
        filterDescriptions.add(new FilterDescription("name", FilterOperator.like, name));
      }
      if (refId != null) {
        filterDescriptions.add(new FilterDescription("referenceId", FilterOperator.eq, refId));
      }
      if (type != null) {
        filterDescriptions.add(new FilterDescription("jobType", FilterOperator.eq, type));
      }
      if (createdAtAfter != null) {
        filterDescriptions.add(new FilterDescription("createdAt", FilterOperator.timestamp_gte, createdAtAfter.getTimestamp()));
      }
      if (createdAtBefore != null) {
        filterDescriptions.add(new FilterDescription("createdAt", FilterOperator.timestamp_lte, createdAtBefore.getTimestamp()));
      }
      if (updatedAtAfter != null) {
        filterDescriptions.add(new FilterDescription("updatedAt", FilterOperator.timestamp_gte, updatedAtAfter.getTimestamp()));
      }
      if (updatedAtBefore != null) {
        filterDescriptions.add(new FilterDescription("updatedAt", FilterOperator.timestamp_lte, updatedAtBefore.getTimestamp()));
      }
      if (cpu != null) {
        filterDescriptions.add(new FilterDescription("cpu", FilterOperator.like, cpu));
      }
      if (memory != null) {
        filterDescriptions.add(new FilterDescription("memory", FilterOperator.like, memory));
      }

      List<Job> jobs = jobController.getByCriteria(filterDescriptions);
      return Response.ok(jobs).build();
    } catch (Exception e) {
      log.error("Exception during queryJobs request, exception=" + e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/{jobId}/job-executions")
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Get JobExecutions by JobId", response = JobExecution.class, responseContainer = "List")
  public Response getJobExecutionsByJobId(@PathParam("jobId") long jobId) {
    try {
      List<JobExecution> data = jobExecutionController.getByJobId(jobId);

      return Response.ok(data).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/{jobId}")
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Updates a Job identified by jobId")
  @ApiImplicitParams({
    @ApiImplicitParam(name = "job", value = "Job to update", required = true, dataType = "com.company.jobServer.beans.Job", paramType = "body"),
  })
  public Response updateJob(@PathParam("jobId") long jobId, @ApiParam(hidden = true) String requestBody) {
    ObjectMapper objectMapper = new ObjectMapper();

    try {
      JsonNode jsonNode = objectMapper.readValue(requestBody, JsonNode.class);
      Job priorJob = jobController.getById(jobId);

      if (priorJob == null)
        return Response.serverError().entity(RestTools.getErrorJson("jobId does not exist in DB", false, Optional.empty())).build();

      Job updatedJob = objectMapper.readerForUpdating(priorJob).readValue(jsonNode);

      if (jobController.update(updatedJob)) {
        return Response.ok().build();
      } else return Response.serverError().build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  @Path("/{jobId}")
  @DELETE
  @Produces(MediaType.APPLICATION_JSON)
  @ApiOperation(value = "Delete a Job by Id")
  public Response deleteJobById(@PathParam("jobId") long jobId) {
    try {
      Job job = jobController.getById(jobId);
      if (job == null) {
        log.error("Unknown Job");
        return Response.serverError().entity(RestTools.getErrorJson("job does not exist in DB", false, Optional.empty())).build();
      }

      if (jobController.delete(jobId)) {
        return Response.ok().build();
      } else
        return Response.serverError().entity(RestTools.getErrorJson("unable to unregister Job", false, Optional.empty())).build();
    } catch (Exception e) {
      log.error("Exception during request", e);
      return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
    }
  }

  private String generateUniqueName(String name) {
    return name + "-" + System.currentTimeMillis() + "-" + random.nextInt(99);
  }
}
