package com.marimon.excursions;

import akka.actor.ActorSystem;
import akka.testkit.JavaTestKit;
import com.lightbend.lagom.javadsl.testkit.PersistentEntityTestDriver;
import com.marimon.excursions.writeside.ExcursionEvent;
import com.marimon.excursions.writeside.ExcursionEntity;
import com.marimon.excursions.writeside.ExcursionState;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.Collections;
import java.util.UUID;

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
  public void shouldProcessAScheduleExcursionCommand() {
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

    assertEquals(Collections.emptyList(), outcome.issues());
  }

}
