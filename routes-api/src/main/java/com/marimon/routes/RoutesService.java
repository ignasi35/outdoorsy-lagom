package com.marimon.routes;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;
import org.pcollections.PStack;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface RoutesService extends Service {

  ServiceCall<Route, RouteId> reportRoute();

  ServiceCall<NotUsed, Route> loadRoute(String id);
  ServiceCall<NotUsed, PStack<RouteId>> loadRoutes();

  @Override
  default Descriptor descriptor() {
    return named("routes")
        .withCalls(
            restCall(Method.POST, "/api/routes", this::reportRoute),
            restCall(Method.GET, "/api/routes/:xyz", this::loadRoute),
            restCall(Method.GET, "/api/routes", this::loadRoutes)
        )
        .withAutoAcl(true)
        ;
  }
}
