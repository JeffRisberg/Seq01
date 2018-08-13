package com.company.common.endpoints;

import com.company.common.datatypes.HealthInfo;
import com.company.common.datatypes.VersionInfo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;

/**
 * @author Jeff Risberg
 * @since 10/02/17
 */
public abstract class SystemEndpointBase {
    private static final Logger logger = LoggerFactory.getLogger(SystemEndpointBase.class);

    private final VersionInfo versionInfo;
    private final HealthInfo healthInfo;

    protected SystemEndpointBase(URL propertiesUrl) {
        this.versionInfo = loadVersionInfo(propertiesUrl);
        this.healthInfo = new HealthInfo();
    }

    @GET
    @Path("health")
    public HealthInfo getHealth() {
        logger.info("Getting Service health.");
        healthInfo.isShutdown = false;
        return healthInfo;
    }

    @GET
    @Path("version")
    public VersionInfo getVersion() {
        logger.info("Getting version info.");
        return versionInfo;
    }

    private VersionInfo loadVersionInfo(URL propertiesUrl) {
        logger.info("Getting version info.");

        Properties properties = new Properties();
        try {
            if (propertiesUrl == null) {
                throw new IOException();
            }
            properties.load(propertiesUrl.openStream());
        } catch (IOException e) {
            throw new IllegalStateException("Application was not built correctly. Missing app.properties in classpath.");
        }

        VersionInfo versionInfo = new VersionInfo();
        versionInfo.artifact = properties.getProperty("artifactId");
        versionInfo.group = properties.getProperty("groupId");
        versionInfo.version = properties.getProperty("version");

        return versionInfo;
    }
}
