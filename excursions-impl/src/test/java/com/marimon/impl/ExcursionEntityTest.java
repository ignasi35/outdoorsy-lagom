package com.marimon.impl;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.marimon.excursions.Excursion;
import com.marimon.excursions.ExcursionStatus;
import com.marimon.excursions.ExcursionsService;
import com.marimon.excursions.ScheduleExcursion;
import com.marimon.excursions.impl.ExcursionCommand;
import com.marimon.excursions.impl.ExcursionEntity;
import com.marimon.excursions.impl.ExcursionEvent;
import com.marimon.excursions.impl.ExcursionState;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.UUID;

import static com.lightbend.lagom.javadsl.testkit.ServiceTest.defaultSetup;
import static com.lightbend.lagom.javadsl.testkit.ServiceTest.withServer;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.Assert.assertEquals;

public class ExcursionEntityTest {


  static ActorSystem system;

  @BeforeClass
  public static void setup() {
    system = ActorSystem.create();
  }

  @AfterClass
  public static void teardown() {
    JavaTestKit.shutdownActorSystem(system);
    system = null;
  }

  @Test
  public void shouldProcessASchedulingCommand() {
    UUID uuid = UUID.randomUUID();
    PersistentEntityTestDriver<ExcursionCommand, ExcursionEvent, ExcursionState> driver =
        new PersistentEntityTestDriver<>(system, new ExcursionEntity(), uuid.toString());

    String location = "Aiguablava";
    String isoDate = "2016-10-29";
    ExcursionCommand.ScheduleExcursion cmd = new ExcursionCommand.ScheduleExcursion(location, isoDate);
    PersistentEntityTestDriver.Outcome<ExcursionEvent, ExcursionState> outcome = driver.run(cmd);

    assertEquals(ExcursionStatus.SCHEDULED, outcome.state().getStatus());
    assertEquals(location, outcome.state().getExcursion().get().getLocation());
    assertEquals(1, outcome.events().size());

  }

}
