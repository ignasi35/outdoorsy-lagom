package com.marimon.routes;

import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 *
 */
public class RoutesModule extends AbstractModule implements ServiceGuiceSupport {
  @Override
  protected void configure() {
    bindServices(serviceBinding(RoutesService.class, RoutesServiceImpl.class));
  }
}
