package com.company.common.endpoints;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.company.common.base.binding.annotation.RedshiftP;
import com.company.common.base.config.DatabaseConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.util.HashMap;
import java.util.Map;

import static com.company.common.constants.EnvironmentKeys.REDSHIFT_CLUSTER_MAXIMUM_CONNECTIONS;
import static com.company.common.constants.EnvironmentKeys.REDSHIFT_CLUSTER_MINIMUM_IDLE;

/**
 * This endpoint is to be implemented by all servers.  It provides basic health
 * and configuration information.
 *
 * @author Jeff Risberg
 * @since 10/02/17
 */
@Singleton
@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class SystemEndpoint extends SystemEndpointBase {
    private static final Logger logger = LoggerFactory.getLogger(SystemEndpoint.class);

    private static final String REDSHIFT_SERVER = "REDSHIFT_SERVER";

    private final DatabaseConfig databaseConfig;

    @Inject
    SystemEndpoint(@RedshiftP DatabaseConfig databaseConfig) {
        super(SystemEndpoint.class.getResource("/app.properties"));

        this.databaseConfig = databaseConfig;
    }

    @GET
    @Path("info")
    public Map<String, Object> getInfo() throws JsonProcessingException {
        logger.info("Getting redshift config info.");

        Map<String, Object> envValues = new HashMap<>();
        envValues.put(REDSHIFT_SERVER, databaseConfig.getServer());
        envValues.put(REDSHIFT_CLUSTER_MINIMUM_IDLE, databaseConfig.getConnectionMin());
        envValues.put(REDSHIFT_CLUSTER_MAXIMUM_CONNECTIONS, databaseConfig.getConnectionMax());
        //envValues.put(REDSHIFT_CLUSTER_CONNECTION_TIMEOUT, databaseConfig.getConnectionTimeoutMs());
        //envValues.put(REDSHIFT_CLUSTER_IDLE_TIMEOUT, databaseConfig.getConnectionIdleTimeoutMs());
        //envValues.put(REDSHIFT_CLUSTER_CONNECTION_MAX_LIFETIME, databaseConfig.getConnectionMaxLifetimeMs());

        return envValues;
    }
}
