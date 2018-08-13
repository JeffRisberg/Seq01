package com.company.common.services.jersey;

import com.company.common.services.MicroserviceModule;
import com.google.inject.Inject;
import org.glassfish.jersey.CommonProperties;
//import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import java.util.List;

/**
 * This configures Jersey.  The {@link MicroserviceModule}, which is implemented by the
 * microservice's main Guice Module (e.g. Jersey04Module), produces the List of Resource (Controller) classes
 * which are injected here and registered with Jersey.
 */
public class Jersey2App extends ResourceConfig {
    @Inject
    public Jersey2App(ObjectMapperContextResolver objectMapperContextResolver, @ResourceList List<Class<?>> resources) {
        register(objectMapperContextResolver);

        for (Class<?> resource : resources) {
            register(resource);
        }

        //register(JacksonFeature.class);

        property(CommonProperties.METAINF_SERVICES_LOOKUP_DISABLE, true);
        property(CommonProperties.FEATURE_AUTO_DISCOVERY_DISABLE, true);
        property(CommonProperties.JSON_PROCESSING_FEATURE_DISABLE, true);
        property(CommonProperties.MOXY_JSON_FEATURE_DISABLE, true);
    }
}
