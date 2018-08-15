package com.company.jobServer.endpoints;

import com.company.jobServer.FilterDescription;
import com.company.jobServer.FilterOperator;
import com.company.jobServer.beans.DAGNode;
import com.company.jobServer.beans.DAGNodeDependency;
import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.beans.dto.AirflowResponseDto;
import com.company.jobServer.beans.dto.JobAirflowInfoDto;
import com.company.jobServer.beans.enums.DAGNodeType;
import com.company.jobServer.beans.enums.JobStateAction;
import com.company.jobServer.beans.enums.JobType;
import com.company.jobServer.common.ResourceLocator;
import com.company.jobServer.common.RestTools;
import com.company.jobServer.controllers.DAGNodeController;
import com.company.jobServer.controllers.DAGNodeDependencyController;
import com.company.jobServer.controllers.JobController;
import com.company.jobServer.controllers.JobExecutionController;
import com.company.jobServer.executors.CollectionJobExecutor;
import com.company.jobServer.executors.ConnectorJobExecutor;
import com.company.jobServer.executors.ModelJobExecutor;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.util.*;

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
    private static final DAGNodeController dagNodeController = new DAGNodeController();
    private static final DAGNodeDependencyController dagNodeDependencyController = new DAGNodeDependencyController();

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

    @Path("/create-if-not-exists")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Creates Job if not exists", response = Job.class)
    public Response createIfNotExists(Job givenJob) {
        try {
            Job job = jobController.createIfNotExists(givenJob);

            if (job != null) {
                return Response.ok(job).build();
            }

            return Response.serverError().build();
        } catch (Exception e) {
            log.error("Exception during request", e);
            return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
        }
    }

    @Path("/create-or-update")
    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Creates Job or Updates existing Job", response = Job.class)
    public Response createOrUpdate(Job givenJob) {
        try {
            Job job = jobController.createOrUpdate(givenJob);

            if (job != null) {
                return Response.ok(job).build();
            }

            return Response.serverError().build();
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

    @Path("/{jobId}/airflow-info/{tenantId}/{jobType}/{jobSubType}")
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Get Airflow info for a Job by Id", response = JobAirflowInfoDto.class, responseContainer = "List")
    public Response getJobAirflowInfo(@PathParam("jobId") long jobId,
                                      @PathParam("tenantId") String tenantId,
                                      @PathParam("jobType") JobType jobType,
                                      @PathParam("jobSubType") String jobSubType) {
        try {
            log.debug("MKN: Inside getJobAirflowInfo ");

            List<JobAirflowInfoDto> jobAirflowInfos = new ArrayList<>();
            JobAirflowInfoDto jobAirflowInfo = new JobAirflowInfoDto();
            String imageTag = ResourceLocator.getResource(DOCKER_IMAGE_TAG).orElse(DEFAULT_DOCKER_IMAGE_TAG);

            Job job = jobController.getById(jobId);

            if (job != null) {
                if (jobType == JobType.CONNECTOR) {

                    log.debug("MKN: Inside Connector ");
                    String dockerImageName = job.getDockerImageName();

                    if (dockerImageName == null) dockerImageName = "";

                    if (dockerImageName.endsWith(":")) {
                        dockerImageName = dockerImageName.substring(0, dockerImageName.length() - 1);
                    }

                    jobAirflowInfo.setId(job.getId());
                    jobAirflowInfo.setName(job.getName());
                    jobAirflowInfo.setJobType(job.getJobType());
                    jobAirflowInfo.setImage(dockerImageName + ":" + imageTag);
                    jobAirflowInfo.setOutputModel(job.getOutputModel());

                    List<String> functions = new ArrayList();
                    if (job.getFunctions() != null) {
                        job.getFunctions().forEach(func -> functions.add(func.toString()));
                    }
                    jobAirflowInfo.setFunctions(functions);

                    JSONObject vars = job.getRuntimeParams();
                    if (vars != null) {
                        Set<String> keySet = vars.keySet();
                        keySet.forEach(key -> {
                            String value = (String) vars.get(key);

                            jobAirflowInfo.getEnvVars().put(key, value);
                        });
                    }
                } else if (jobType == JobType.MODEL) {
                    /*
                    log.debug("MKN: Inside Model ");
                    ModelClient modelClient = new ModelClient();
                    // Params Algorithm name (=jobSubType), Tag, Tenant
                    TrainingExecutionParamsObject execParamsObj =
                            modelClient.getTrainingExecutionParams(jobSubType, imageTag, tenantId);

                    if (execParamsObj != null) {
                        log.debug("MKN: Got execParamsObj " + execParamsObj.getAlgorithmImage());

                        jobAirflowInfo.setId(job.getId());
                        jobAirflowInfo.setTenantId(tenantId);
                        jobAirflowInfo.setName(job.getName());
                        jobAirflowInfo.setJobType(job.getJobType());
                        jobAirflowInfo.setJobSubType(jobSubType);
                        jobAirflowInfo.setImage(execParamsObj.getAlgorithmImage());
                        jobAirflowInfo.setOutputModel(execParamsObj.getOutputModelLocation());
                        jobAirflowInfo.setContainerArgs(execParamsObj.getContainerArgs());
                        jobAirflowInfo.setContainerCommands(execParamsObj.getContainerCommands());

                        jobAirflowInfo.setEnvVars(execParamsObj.getEnvVars());
                    } else {
                        log.error("MKN: execParamsObj was null");
                    }
                    */
                } else if (jobType == JobType.COLLECTION) {
                    log.warn("No behavior for Collection jobType");
                } else {
                    log.warn("Unknown jobType");
                }

                jobAirflowInfos.add(jobAirflowInfo);
            }

            return Response.ok(jobAirflowInfos).build();
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

    @Path("/{jobId}/action")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Modifies the state of a Job", response = JobExecution.class)
    public Response modifyJobState(@PathParam("jobId") long jobId, JobStateAction action) {
        log.info("ModifyJobState jobId=" + jobId + ", action=" + action.getAction());

        try {
            Job job = jobController.getById(jobId);
            if (job == null) {
                log.error("Unknown Job");
                return Response.ok(new ArrayList<>()).build();
            }

            JobExecution jobExecution = new JobExecution();

            switch (action.getAction()) {
                case JobStateAction.PAUSE_ACTION:
                    return Response.ok().build();
                case JobStateAction.RESUME_ACTION:
                    return Response.ok().build();
                case JobStateAction.STOP_ACTION:
                    if (job.getJobType() == JobType.CONNECTOR) {
                        List<JobExecution> jobExecutions = jobExecutionController.getByJobId(jobId);
                        for (JobExecution je : jobExecutions) {
                            connectorJobExecutor.stop(je, true);
                        }
                    }
                    System.out.println("stop action");
                    return null; // this.stopJob(all);
                case JobStateAction.START_ACTION:
                    System.out.println("start action job name:" + job.getName() + " type:" + job.getJobType() + " computeResource:" + job.getComputeResource());

                    if (job.getName().contains("_dag")) {
                        log.info("Starting to make call to Airflow");
                        log.info("DAG name is: " + job.getDescription());
                        /*
                        String dagName = job.getDescription();
                        if (dagName == null || dagName.isEmpty())
                            dagName = "tutorial";
                        //String dagName = "full_major_incident_dag_" + job.getTenantId();

                        AirflowClient airflowClient = new AirflowClient();
                        AirflowResponseDto resp = airflowClient.triggerAirflowDAG(dagName, job.getTenantId(), job.getReferenceId());
                        log.info("Response from Airflow: -->");
                        log.info(resp.toString());
                        if (resp.getHttp_response_code() != 200) {
                            log.error("Error in calling Airflow. Response code:" + resp.getHttp_response_code());
                        }
                        */
                    } else {
                        if (job.getJobType() == JobType.CONNECTOR) {
                            jobExecution = connectorJobExecutor.start(job);
                        } else if (job.getJobType() == JobType.MODEL) {
                            jobExecution = modelJobExecutor.start(job);
                        } else if (job.getJobType() == JobType.COLLECTION) {
                            jobExecution = collectionJobExecutor.start(job);
                        } else {
                            log.error("Unknown JobType " + job.getJobType());
                        }
                    }
                    return Response.ok(jobExecution).build();
                default:
                    return Response.status(Response.Status.BAD_REQUEST).build();
            }
        } catch (Exception e) {
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

    @Path("/populate/{tenantId}/{dataSourceId}/{clusterName}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @ApiOperation(value = "Create set of jobs", response = Job.class)
    public Response populate(@PathParam("tenantId") String tenantId,
                             @PathParam("dataSourceId") Long dataSourceId,
                             @PathParam("clusterName") String clusterName) {
        // A parameter map might be a tree of maps, populated as:
        //  connector
        //    username
        //    password
        //    url
        //  model:
        //    input bucket
        //    output bucket
        //    recency hour
        try {
            String imageRepository = ResourceLocator.getResource(DOCKER_REPOSITORY_URI).orElse(DEFAULT_DOCKER_REPOSITORY_URI);
            if (!imageRepository.endsWith("/")) {
                imageRepository += "/";
            }

            if (clusterName == null) clusterName = "dev2";

            Job job1 = new Job();
            job1.setTenantId(tenantId);
            job1.setJobType(JobType.COLLECTION);
            job1.setName("main-" + tenantId);
            job1.setReferenceId("main-" + tenantId);

            JSONObject env1 = new JSONObject();
            job1.setRuntimeParams(env1);
            Job createdJob1 = jobController.create(job1);
            DAGNode dagNode1 = new DAGNode();
            dagNode1.setName(generateUniqueName("node1"));
            dagNode1.setType(DAGNodeType.EXECUTE);
            dagNode1.setJobId(createdJob1.getId());
            DAGNode createdDagNode1 = dagNodeController.create(dagNode1);

            Job job2 = new Job();
            DAGNode dagNode2 = new DAGNode();
            job2.setTenantId(tenantId);
            job2.setJobType(JobType.CONNECTOR);
            job2.setName("connector-" + tenantId);
            job2.setReferenceId("connector-" + tenantId);
            job2.setParent(job1);
            job2.setParentId(job1.getId());
            job2.setDockerImageName(imageRepository + "connector-snow");

            //JSONObject env2 = new JSONObject();
            //env2.put("company_io_python_pubsub_plugin", "kafka");
            //env2.put("company_kv_redis_host", "172.20.58.114");

            //env2.put("company_pubsub_host", "redis");
            //env2.put("company_pubsub_port", "6379");

            JSONObject config2 = new JSONObject();
            config2.put("tenantId", tenantId);
            config2.put("dataSourceId", dataSourceId);
            config2.put("serverhost", "connector-server");
            config2.put("serverport", 1234);
            config2.put("splash_url", "http://splash:8050");
            config2.put("sink_type", "queue");

            JSONArray functions2 = new JSONArray();
            functions2.add("LearnServiceCatalogs");
            config2.put("functions", functions2);

            JSONObject params2 = new JSONObject();
            params2.put("url", "https://autodeskdev.service-now.com");
            params2.put("password", "Ai$era312!");
            params2.put("username", "t_bulur");
            params2.put("table", "incident");
            config2.put("parameters", params2);

            //env2.put("company_config", config2.toJSONString());
            //env2.put("company_connector_config", config2.toJSONString());

            job2.setFunctions(functions2);
            job2.setRuntimeParams(config2);

            Job createdJob2 = jobController.create(job2);
            dagNode2.setName(generateUniqueName("node2"));
            dagNode2.setType(DAGNodeType.EXECUTE);
            dagNode2.setJobId(createdJob2.getId());
            DAGNode createdDagNode2 = dagNodeController.create(dagNode2);
            DAGNodeDependency dnd2 = new DAGNodeDependency();
            dnd2.assign(createdDagNode1, createdDagNode2);
            dagNodeDependencyController.create(dnd2);

            Job job3 = new Job();
            job3.setTenantId(tenantId);
            job3.setJobType(JobType.MODEL);
            job3.setName("model-" + tenantId);
            job3.setReferenceId("model-" + tenantId);
            job3.setParent(job1);
            job3.setParentId(job1.getId());
            job3.setDockerImageName(imageRepository + "incident-clustering");

            //JSONObject env3 = new JSONObject();
            //env3.put("company_io_python_pubsub_plugin", "kafka");
            //env3.put("company_kv_redis_host", "172.20.58.114");

            JSONObject config3 = new JSONObject();
            config3.put("input-bucket", "companytenants-" + clusterName);
            config3.put("input-path", tenantId + "/RawData/Incidents");

            config3.put("output-bucket", "companytenants-" + clusterName);
            config3.put("output-path", tenantId + "/ModelStore/incident-clustering-training-job/temp");

            config3.put("recency-hour", "100000");
            config3.put("tenant-id", tenantId);

            //env3.put("company_config", config3.toJSONString());
            //env3.put("company_training_input_config", config3.toJSONString());

            job3.setHyperParameters(new JSONObject());
            job3.setRuntimeParams(config3);

            Job createdJob3 = jobController.create(job3);
            DAGNode dagNode3 = new DAGNode();
            dagNode3.setName(generateUniqueName("node3"));
            dagNode3.setType(DAGNodeType.EXECUTE);
            dagNode3.setJobId(createdJob3.getId());
            DAGNode createdDagNode3 = dagNodeController.create(dagNode3);
            DAGNodeDependency dnd3 = new DAGNodeDependency();
            dnd3.assign(createdDagNode2, createdDagNode3);
            dagNodeDependencyController.create(dnd3);

            Job job4 = new Job();
            job4.setTenantId(tenantId);
            job4.setJobType(JobType.MODEL);
            job4.setName("model-" + tenantId);
            job4.setReferenceId("model-" + tenantId);
            job4.setParent(job1);
            job4.setParentId(job1.getId());
            job4.setDockerImageName(imageRepository + "major-incident");

            //JSONObject env4 = new JSONObject();
            //env4.put("company_io_python_pubsub_plugin", "kafka");
            //env4.put("company_kv_redis_host", "172.20.58.114");

            JSONObject config4 = new JSONObject();
            config4.put("input-bucket", "companytenants-" + clusterName);
            config4.put("input-path", tenantId + "/RawData/Incidents");

            config4.put("output-bucket", "companytenants-" + clusterName);
            config4.put("output-path", tenantId + "/ModelStore/major-incident-model/temp");

            config4.put("recency-hour", "20000");
            config4.put("tenant-id", tenantId);

            //env4.put("company_config", config4.toJSONString());
            //env4.put("company_training_input_config", config4.toJSONString());

            job4.setHyperParameters(new JSONObject());
            job4.setRuntimeParams(config4);

            Job createdJob4 = jobController.create(job4);
            DAGNode dagNode4 = new DAGNode();
            dagNode4.setName(generateUniqueName("node4"));
            dagNode4.setType(DAGNodeType.EXECUTE);
            dagNode4.setJobId(createdJob4.getId());
            DAGNode createdDagNode4 = dagNodeController.create(dagNode4);
            DAGNodeDependency dnd4 = new DAGNodeDependency();
            dnd4.assign(createdDagNode3, createdDagNode4);
            dagNodeDependencyController.create(dnd4);

            Job job5 = new Job();
            job5.setTenantId(tenantId);
            job5.setJobType(JobType.MODEL);
            job5.setName("model-" + tenantId);
            job5.setReferenceId("model-" + tenantId);
            job5.setParent(job1);
            job5.setParentId(job1.getId());
            job5.setDockerImageName(imageRepository + "entity-scores-model");

            //JSONObject env5 = new JSONObject();
            //env5.put("company_io_python_pubsub_plugin", "kafka");
            //env5.put("company_kv_redis_host", "172.20.58.114");

            JSONObject config5 = new JSONObject();
            //config5.put("input-bucket", "companytenants-" + clusterName);
            //config5.put("input-path", tenantId + "/RawData/Incidents");

            //config5.put("output-bucket", "companytenants-" + clusterName);
            //config5.put("output-path", tenantId + "/ModelStore/entity-score-model/temp");

            //config5.put("recency-hour", "20000");
            config5.put("tenant-id", tenantId);

            //env5.put("company_config", config5.toJSONString());
            //env5.put("company_training_input_config", config5.toJSONString());

            job5.setHyperParameters(new JSONObject());
            job5.setRuntimeParams(config5);

            Job createdJob5 = jobController.create(job5);
            DAGNode dagNode5 = new DAGNode();
            dagNode5.setName(generateUniqueName("node5"));
            dagNode5.setType(DAGNodeType.EXECUTE);
            dagNode5.setJobId(createdJob5.getId());
            DAGNode createdDagNode5 = dagNodeController.create(dagNode5);
            DAGNodeDependency dnd5 = new DAGNodeDependency();
            dnd5.assign(createdDagNode4, createdDagNode5);
            dagNodeDependencyController.create(dnd5);

            return Response.ok(job1).build();
        } catch (Exception e) {
            log.error("Exception during request", e);
            return Response.serverError().entity(RestTools.getErrorJson("Exception during request", false, Optional.of(e))).build();
        }
    }

    private String generateUniqueName(String name) {
        return name + "-" + System.currentTimeMillis() + "-" + random.nextInt(99);
    }
}
