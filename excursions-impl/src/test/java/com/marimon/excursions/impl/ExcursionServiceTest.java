package com.marimon.excursions.impl;

import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import com.marimon.excursions.Excursion;
import com.marimon.excursions.ExcursionId;
import com.marimon.excursions.ExcursionsService;
import com.marimon.excursions.ScheduleExcursionRequest;
import com.marimon.excursions.impl.readside.ExcursionReadWriteRepository;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.startServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.*;

public class ExcursionServiceTest {

  private static ServiceTest.TestServer server;

  @BeforeClass
  public static void setUp() {
    server = startServer(
        defaultSetup().withCassandra(true).withCluster(true)
    );
  }

  @AfterClass
  public static void tearDown() {
    if (server != null) {
      server.stop();
      server = null;
    }
  }

  ExcursionReadWriteRepository writeRepository = server.injector().instanceOf(ExcursionReadWriteRepository.class);

  @Before
  public void before() throws InterruptedException, ExecutionException, TimeoutException {
    writeRepository.deleteAll().toCompletableFuture().get(5, SECONDS);
    eventually(() -> {
      List<Excursion> excursions = server.client(ExcursionsService.class)
          .loadExcursions()
          .invoke().toCompletableFuture().get(5, SECONDS)
          .toMat(Sink.seq(), Keep.right())
          .run(server.materializer()).toCompletableFuture().get(5, SECONDS);
      assertEquals(0, excursions.size());
    });
  }

  @Test
  public void shouldStoreScheduledExcursions() throws Exception {
    ExcursionsService service = server.client(ExcursionsService.class);

    String location = "Ha Long Bay";
    String isoDate = "2016-09-01";
    ScheduleExcursionRequest excRequest = new ScheduleExcursionRequest(location, isoDate);

    ExcursionId excursionId = service.scheduleExcursion().invoke(excRequest).toCompletableFuture().get(5, SECONDS);

    Optional<Excursion> excursion = service.loadExcursion(excursionId.getId()).invoke().toCompletableFuture().get(5, SECONDS);
    assertTrue(excursion.isPresent());
    assertEquals(isoDate, excursion.get().getIsoDate());
    assertEquals(location, excursion.get().getLocation());

  }

  @Test
  public void shouldReturnTheListOfAllExcursionsSortedInInverseChronology() throws Exception {
    ExcursionsService service = server.client(ExcursionsService.class);

    String lc = "Cala Montgo";
    insertSync(service, new ScheduleExcursionRequest("Ha Long Bay", "2015-10-01"));
    insertSync(service, new ScheduleExcursionRequest("12 Apostles", "2015-10-15"));
    insertSync(service, new ScheduleExcursionRequest(lc, "2015-10-28"));

    eventually(() -> {
      List<Excursion> excursions = service
          .loadExcursions()
          .invoke().toCompletableFuture().get(5, SECONDS)
          .toMat(Sink.seq(), Keep.right())
          .run(server.materializer()).toCompletableFuture().get(5, SECONDS);
      excursions.stream().map(exc -> " " + exc.getLocation() + " " + exc.getIsoDate()).forEach(System.out::println);
      assertEquals(3, excursions.size());
      assertEquals(lc, excursions.get(0).getLocation());
    });

  }

  private void insertSync(ExcursionsService service, ScheduleExcursionRequest reqA)
      throws InterruptedException, ExecutionException, TimeoutException {
    service.scheduleExcursion().invoke(reqA).toCompletableFuture().get(5, SECONDS);
  }

  // TODO: read the status code from response.
  @Test
  public void shouldReturn404WhenRequestingAnExcursionIdThatIsInvalidOrNotFound() throws Exception {

    ExcursionsService service = server.client(ExcursionsService.class);

    //TODO: how to assert the status code ?
    //TODO: why is the returned payload 'null' when the response was Optional.empty() ?
    String invalidId = "asdf";
    Optional<Excursion> whenInvalid = getExcursionExpectingFailure(service, invalidId);
    assertFalse(whenInvalid.isPresent());

    String notFoundId = UUID.randomUUID().toString();
    Optional<Excursion> whenNotFound = getExcursionExpectingFailure(service, notFoundId);
    assertFalse(whenNotFound.isPresent());

  }

  private Optional<Excursion> getExcursionExpectingFailure(ExcursionsService service, String notFoundId) throws InterruptedException, ExecutionException, TimeoutException {
    return service.loadExcursion(notFoundId).invoke().toCompletableFuture()
        .exceptionally((Throwable t) -> {
          // some gymnastics because the payload is "null" (literally) and JSON parser fails.
          if (t instanceof CompletionException && t.getCause() instanceof NullPointerException)
            return Optional.empty();
          else
            throw new RuntimeException(t);
        }).get(5, SECONDS);
  }

  // -----------------------------------------------------------------------------------------------------------

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
        return ;
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
