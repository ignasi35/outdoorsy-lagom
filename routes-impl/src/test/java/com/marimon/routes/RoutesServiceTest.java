package com.marimon.routes;

import org.junit.Test;
import org.pcollections.ConsPStack;
import org.pcollections.PStack;

import java.util.Arrays;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class RoutesServiceTest {

  @Test
  public void shouldStoreAndRetrieveARoute() {
    withServer(defaultSetup().withCassandra(true), server -> {
          RoutesService routesService = server.client(RoutesService.class);

          PStack<WayPoint> waypoints = ConsPStack.from(
              Arrays.asList(new WayPoint(-13.214208, -72.3810852), new WayPoint(-13.1639352, -72.5453277)));
          Track track = new Track(waypoints);
          Route r = new Route("Inca Trail", track);
          RouteId routeId = routesService.reportRoute().invoke(r).toCompletableFuture().get(5, SECONDS);
          Route route = routesService.loadRoute(routeId.getId().toString()).invoke().toCompletableFuture().get(5, SECONDS);

          assertEquals(r, route);
        }

    );
  }

}
