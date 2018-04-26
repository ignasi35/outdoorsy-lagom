package com.marimon.excursions;

import akka.stream.javadsl.Keep;
import akka.stream.javadsl.Sink;
import com.lightbend.lagom.javadsl.api.transport.TransportException;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import com.marimon.excursions.readside.ExcursionReadWriteRepository;
import org.junit.*;
import scala.concurrent.duration.FiniteDuration;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.*;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ExcursionServiceTest {

  private static ServiceTest.TestServer server;

  private FiniteDuration eventualDuration = new FiniteDuration(10, SECONDS);

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

  ExcursionReadWriteRepository writeRepository = server.injector().instanceOf(ExcursionReadWriteRepository.class);

  @Before
  public void before() throws InterruptedException, ExecutionException, TimeoutException {
    writeRepository.deleteAll().toCompletableFuture().get(5, SECONDS);
    assertExcursionCountEquals(0);
  }

  @Test
  public void shouldStoreScheduledExcursions() throws Exception {
    ExcursionsService service = server.client(ExcursionsService.class);

    String location = "Ha Long Bay";
    String isoDate = "2016-09-01";
    ScheduleExcursionRequest excRequest = new ScheduleExcursionRequest(location, isoDate);

    ExcursionId excursionId = insertSync(service, excRequest);

    Excursion excursion = service.loadExcursion(excursionId.getId()).invoke().toCompletableFuture().get(5, SECONDS);
    assertEquals(isoDate, excursion.getIsoDate());
    assertEquals(location, excursion.getLocation());

    // wait until the readside acknowledges the event, otherwise there's a race condition with @Before and storage is not properly cleaned before next test.
    eventually(eventualDuration, () -> {
      List<Excursion> excursions = getExcursions(service);
      assertEquals(1, excursions.size());
    });

  }

  @Test
  public void shouldReturnTheListOfAllExcursionsSortedInInverseChronology() throws Exception {
    ExcursionsService service = server.client(ExcursionsService.class);

    String lc = "Cala Montgo";
    insertSync(service, new ScheduleExcursionRequest("Ha Long Bay", "2015-10-01"));
    insertSync(service, new ScheduleExcursionRequest("12 Apostles", "2015-10-15"));
    insertSync(service, new ScheduleExcursionRequest(lc, "2015-10-28"));

    assertExcursionCountEquals(3);
    assertEquals(lc, getExcursions(service).get(0).getLocation());
  }

  private ExcursionId insertSync(ExcursionsService service, ScheduleExcursionRequest reqA)
      throws InterruptedException, ExecutionException, TimeoutException {
    return service.scheduleExcursion().invoke(reqA).toCompletableFuture().get(5, SECONDS);
  }

  @Test
  @Ignore
  public void shouldReturnNotFoundWhenRequestingAnExcursionIdThatDoesnExist() throws Throwable {
    //TODO: how to assert the status code ?
    String notFoundId = UUID.randomUUID().toString();
    getExcursionExpectingFailure(notFoundId, "NotFound");
  }

  @Test
  @Ignore
  public void shouldReturnBadParamsWhenRequestingAnExcursionIdThatIsNotProperlyFormatted() throws Throwable {
    //TODO: how to assert the status code ?
    String invalidId = "asdf";
    getExcursionExpectingFailure(invalidId, "Invalid Id: asdf");

  }

  // -----------------------------------------------------------------------------------------------------------

  private Excursion getExcursionExpectingFailure(String notFoundId, String expectedErrorName) throws Throwable {
    try {
      ExcursionsService service = server.client(ExcursionsService.class);
      return service.loadExcursion(notFoundId).invoke().toCompletableFuture().get(5, SECONDS);
    } catch (ExecutionException ee) {
      Throwable cause = ee.getCause();
      assertTrue(cause instanceof TransportException);
      assertEquals(expectedErrorName, ((TransportException) cause).exceptionMessage().name());
      throw cause;
    }
  }

  private void assertExcursionCountEquals(int expectedExcursionCount) {
    eventually(eventualDuration, () -> {
      List<Excursion> excursions = getExcursions(server.client(ExcursionsService.class));
      assertEquals(expectedExcursionCount, excursions.size());
    });
  }

  private List<Excursion> getExcursions(ExcursionsService service) throws InterruptedException, ExecutionException, TimeoutException {
    return service
        .loadExcursions()
        .invoke().toCompletableFuture().get(5, SECONDS)
        .toMat(Sink.seq(), Keep.right())
        .run(server.materializer()).toCompletableFuture().get(5, SECONDS);
  }

}
