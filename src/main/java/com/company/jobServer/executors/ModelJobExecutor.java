package com.company.jobServer.executors;

import com.company.jobServer.JobServer;
import com.company.jobServer.beans.Job;
import com.company.jobServer.beans.JobExecution;
import com.company.jobServer.common.ResourceLocator;
import com.company.jobServer.common.orchestration.DeployableObject;
import lombok.extern.slf4j.Slf4j;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class ModelJobExecutor extends BaseJobExecutor {
    private static final String TRAINING_HYPERPRAMS = "company_training_hyperparams";

    private static final String TRAINING_SQL_HOST = "company_datastores_sql_host";
    private static final String TRAINING_SQL_PORT = "company_datastores_sql_port";
    private static final String TRAINING_SQL_USER = "company_sql_user";
    private static final String TRAINING_SQL_PASSWORD = "company_sql_password";

    public static String BRANCH = ResourceLocator.getResource("company_algorithms_image_tag").orElse("latest");

    public static JSONParser jsonParser = new JSONParser();

    @Override
    public JobExecution start(Job job, JobExecution parentExecution, JSONObject envVars) {
        /*
        ModelClient modelClient = new ModelClient();

        // Ask for algorithm by name (where name is referenceId string of the job, the tag is the value of BRANCH
        Algorithm algorithm = modelClient.getAlgorithmByNameAndTag(job.getReferenceId(), BRANCH);

        if (algorithm != null) {
            log.info("Fetched the Algorithm from ModelServer");

            String name = algorithm.getName();
            String imageId = algorithm.getImageId();
            String imageTag = algorithm.getImageTag();

            log.info("algorithm name " + name);
            log.info("algorithm imageId " + imageId);
            log.info("algorithm imageTag " + imageTag);

            job.setDockerImageName(imageId);
        } else {
            log.info("Could not find Algorithm");
        }

        // Ask for TrainingJobConfig from ModelServer, because that contains information that:
        //  1) changes from execution from execution
        //  2) relies on information that should only be in the modelServer.
        TrainingJobConfig trainingJobConfig =
                modelClient.getTrainingJobConfigByAlgorithmNameTagAndTenant(job.getReferenceId(), BRANCH, job.getTenantId());

        if (trainingJobConfig != null) {
            log.info("Fetched the TrainingJobConfig from ModelServer");

            String configString = trainingJobConfig.getConfig();
            String outputLocation = trainingJobConfig.getOutputLocation();

            log.info("configString " + configString);
            log.info("outputLocation " + outputLocation);

            try {
                JSONObject configStringAsJsonObject = (JSONObject) jsonParser.parse(configString);

                JSONObject inputParams = (JSONObject) configStringAsJsonObject.get("inputParams");
                JSONObject hyperParameters = (JSONObject) configStringAsJsonObject.get("hyperParameters");

                log.info("inputParams " + inputParams.toJSONString());
                log.info("hyperParameters " + hyperParameters.toJSONString());

                job.setRuntimeParams(inputParams);
                job.setHyperParameters(hyperParameters);
            } catch (Exception e) {
                throw new RuntimeException("ModelJobExecutor: Unable to parse configuration " + configString);
            }

            job.setOutputModel(outputLocation);
        } else {
            log.info("Could not find TrainingJobConfig");
        }

        if (algorithm == null) {
            throw new RuntimeException("ModelJobExecutor: Could not find algorithm " + job.getReferenceId());
        }
        if (trainingJobConfig == null) {
            throw new RuntimeException("ModelJobExecutor: Could not find trainingJobConfig for " + job.getReferenceId());
        }
        */

        return super.start(job, parentExecution, envVars);
    }

    @Override
    public void setupEnv(Job job, DeployableObject deployableObject) {
        HashMap<String, String> env = new HashMap<>();

        // Set up the training entry-point
        List<String> command = new ArrayList<>();
        command.add("/bin/bash");
        command.add("-c");

        List<String> args = new ArrayList<>();
        args.add("chmod +x run_training; ./run_training");

        deployableObject.setCommand(command);
        deployableObject.setArgs(args);

        try {
            env.put(TRAINING_HYPERPRAMS, mapper.writeValueAsString(job.getHyperParameters()));
        } catch (Exception e) {
            log.error(e.getMessage());
        }

        // Set sql source information (host, port, user, password)
        env.put(TRAINING_SQL_HOST, ResourceLocator.getResource(JobServer.DB_HOST).orElse(JobServer.DEFAULT_DB_HOST));
        env.put(TRAINING_SQL_PORT, ResourceLocator.getResource(JobServer.DB_PORT).orElse(JobServer.DEFAULT_DB_PORT));
        env.put(TRAINING_SQL_USER, ResourceLocator.getResource(JobServer.DB_USER).orElse(JobServer.DEFAULT_DB_USER));
        env.put(TRAINING_SQL_PASSWORD, ResourceLocator.getResource(JobServer.DB_PASSWORD).orElse(JobServer.DEFAULT_DB_PASSWORD));

        deployableObject.setEnv(env);
    }

    @Override
    public void stop(JobExecution jobExecution, boolean force) {
        Job job = jobExecution.getJob();

    /*
    ModelClient modelClient = new ModelClient();

    modelClient.deployModelForTenantForAlgorithm
      (jobExecution.getTenantId(), job.getReferenceId(), jobExecution.getOutputLocation());
    */
        super.stop(jobExecution, force);
    }

    @Override
    public void destroy(JobExecution jobExecution) {
        super.destroy(jobExecution);
    }

}
