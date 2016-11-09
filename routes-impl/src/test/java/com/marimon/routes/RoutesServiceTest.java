package com.marimon.routes;

import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.pcollections.ConsPStack;
import org.pcollections.PStack;

import java.util.Arrays;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.eventually;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.startServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class RoutesServiceTest {

  private static ServiceTest.TestServer server;

  @BeforeClass
  public static void setUp() {
    server = startServer(
        defaultSetup().withCassandra(true)
    );
  }

  @AfterClass
  public static void tearDown() {
    if (server != null) {
      server.stop();
      server = null;
    }
  }

  private PStack<WayPoint> waypoints = ConsPStack.from(Arrays.asList(new WayPoint(-13.214208, -72.3810852), new WayPoint(-13.1639352, -72.5453277)));
  private Track track = new Track(waypoints);
  private Route incaTrail = new Route("Inca Trail", track);

  @Test
  public void shouldStoreAndRetrieveARoute() throws InterruptedException, ExecutionException, TimeoutException {
    RoutesService service = server.client(RoutesService.class);
    RouteId routeId = reportRoute(service, incaTrail);
    Route route = loadRoute(service, routeId);
    assertEquals(incaTrail, route);
  }


  @Test
  public void shouldStoreAndRetrieveManyRoutes() throws InterruptedException, ExecutionException, TimeoutException {
    RoutesService routesService = server.client(RoutesService.class);
    reportRoute(routesService, incaTrail);
    reportRoute(routesService, incaTrail);
    reportRoute(routesService, incaTrail);
    eventually(() -> {
      PStack<RouteId> routes = routesService.loadRoutes().invoke().toCompletableFuture().get(5, SECONDS);
      assertEquals(3, routes.size());
    });
  }

  // ------------------------------------------------------------------------------------------------------
  private Route loadRoute(RoutesService routesService, RouteId routeId) throws InterruptedException, ExecutionException, TimeoutException {
    return routesService.loadRoute(routeId.getId().toString()).invoke().toCompletableFuture().get(5, SECONDS);
  }

  private RouteId reportRoute(RoutesService routesService, Route route) throws InterruptedException, ExecutionException, TimeoutException {
    return routesService.reportRoute().invoke(incaTrail).toCompletableFuture().get(5, SECONDS);
  }

  // ------------------------------------------------------------------------------------------------------

  @FunctionalInterface
  protected interface ThrowingRunnable extends Runnable {
    @Override
    default void run() {
      try {
        runThrows();
      } catch (final Exception e) {
        throw new RuntimeException(e);
      }
    }

    void runThrows() throws Exception;
  }

  private void eventually(ThrowingRunnable block) {
    int retries = 10;
    while (retries > 0) {
      try {
        TimeUnit.MILLISECONDS.sleep(100);
        block.runThrows();
        return;
      } catch (InterruptedException e) {
      } catch (Exception e) {
        throw new RuntimeException(e);
      } catch (AssertionError ae) {
        if (retries > 0) retries--;
        else throw ae;
      }
    }
  }


}
