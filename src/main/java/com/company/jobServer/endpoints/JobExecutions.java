package com.company.jobServer.endpoints;

import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.common.RestTools;
import com.company.jobServer.controllers.JobController;
import com.company.jobServer.controllers.JobExecutionController;
import com.company.jobServer.executors.ConnectorJobExecutor;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

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
    private static final JobController jobController = new JobController();
    private static final JobExecutionController jobExecutionController = new JobExecutionController();


    ObjectMapper mapper = new ObjectMapper();
    JSONParser parser = new JSONParser();

    @Path("/{jobId}/")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Begins a Job Execution", response = JobExecution.class)
    public Response beginJobExecution(@PathParam("jobId") long jobId, String requestBody) {
        log.info("beginJobExecution jobId=" + jobId + " " + requestBody);

        try {
            Job job = jobController.getById(jobId);
            if (job == null) {
                log.error("Unknown Job");
                return Response.ok(new ArrayList<>()).build();
            }

            JSONObject envVars = (JSONObject) parser.parse(requestBody);

            System.out.println("Env Vars " + envVars);

            ConnectorJobExecutor jobExecutor = new ConnectorJobExecutor();

            JobExecution jobExecution = jobExecutor.start(job, envVars);

            return Response.ok(jobExecution).build();
        } catch (Exception e) {
            return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
        }
    }

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
            JobExecution jobExecution = jobExecutionController.getById(id);
            if (jobExecution == null)
                return Response.serverError().entity(RestTools.getErrorJson("JobExecution does not exist in DB", false, Optional.empty())).build();

            ConnectorJobExecutor jobExecutor = new ConnectorJobExecutor();

            jobExecutor.stop(jobExecution, true);
            if (jobExecutionController.delete(id)) {
                return Response.ok().build();
            } else
                return Response.serverError().entity(RestTools.getErrorJson("Unable to unregister JobExecution", false, Optional.empty())).build();
        } catch (Exception e) {
            log.error("Exception during request", e);
            return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
        }
    }
}
