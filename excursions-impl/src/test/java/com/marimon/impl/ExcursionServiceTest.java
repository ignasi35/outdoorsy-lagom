package com.marimon.impl;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.*;
import static java.util.concurrent.TimeUnit.SECONDS;

import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import com.marimon.excursions.Excursion;
import com.marimon.excursions.ExcursionsService;
import com.marimon.excursions.ScheduleExcursion;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertEquals;

public class ExcursionServiceTest {


  private static ServiceTest.TestServer server;

  @BeforeClass
  public static void setUp() {
    server = startServer(defaultSetup().withCluster(false));
  }

  @AfterClass
  public static void tearDown() {
    if (server != null) {
      server.stop();
      server = null;
    }
  }

  @Test
  public void shouldStoreScheduledExcursionsUsingConcatLocationDateAsName() throws InterruptedException, ExecutionException, TimeoutException {
    ExcursionsService service = server.client(ExcursionsService.class);
    // TODO: scheduling is only allowed in the future. To Log an
    // excursion in the past use LogExcursion Ops
    String location = "Aiguablava";
    String isoDate = "2016-10-29";
    ScheduleExcursion excRequest = new ScheduleExcursion(location, isoDate);

    service.scheduleExcursion().invoke(excRequest).toCompletableFuture().get(5, SECONDS);

    Excursion excursion = service.loadExcursion().invoke().toCompletableFuture().get(5, SECONDS);
    assertEquals(isoDate, excursion.getIsoDate());
    assertEquals(location, excursion.getLocation());

  }

}
