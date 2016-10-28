package com.marimon.excursions.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import com.marimon.excursions.Excursion;
import com.marimon.excursions.ExcursionId;
import com.marimon.excursions.ExcursionsService;
import com.marimon.excursions.ScheduleExcursion;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
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
        defaultSetup()
            .withCassandra(true)
    );
  }

  @AfterClass
  public static void tearDown() {
    if (server != null) {
      server.stop();
      server = null;
    }
  }

  @Test
  public void shouldStoreScheduledExcursionsUsingConcatLocationDateAsName() throws Exception {
    ExcursionsService service = server.client(ExcursionsService.class);

    String location = "Ha Long Bay";
    String isoDate = "2016-10-29";
    ScheduleExcursion excRequest = new ScheduleExcursion(location, isoDate);

    ExcursionId excursionId = service.scheduleExcursion().invoke(excRequest).toCompletableFuture().get(5, SECONDS);

    Optional<Excursion> excursion = service.loadExcursion(excursionId.getId()).invoke().toCompletableFuture().get(5, SECONDS);
    assertTrue(excursion.isPresent());
    assertEquals(isoDate, excursion.get().getIsoDate());
    assertEquals(location, excursion.get().getLocation());

  }

}
