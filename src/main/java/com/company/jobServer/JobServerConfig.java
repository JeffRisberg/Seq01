package com.company.jobServer;

import io.swagger.jaxrs.listing.ApiListingResource;
import io.swagger.jaxrs.listing.SwaggerSerializers;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.jersey.jackson.JacksonFeature;
import org.glassfish.jersey.server.ResourceConfig;

import javax.inject.Inject;

public class JobServerConfig extends ResourceConfig {
  @Inject
  public JobServerConfig(ServiceLocator serviceLocator) {

    packages(JobServerConfig.class.getPackage().getName())
      .register(JacksonFeature.class) // enable Jackson
      .register(ApiListingResource.class)
      .register(SwaggerSerializers.class);
  }
}
