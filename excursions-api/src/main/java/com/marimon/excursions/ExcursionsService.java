package com.marimon.excursions;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;

import static com.lightbend.lagom.javadsl.api.Service.*;


public interface ExcursionsService extends Service {
  ServiceCall<ScheduleExcursion, Done> scheduleExcursion();

  ServiceCall<NotUsed, Excursion> loadExcursion();

  @Override
  default Descriptor descriptor() {
    return named("excursions").withCalls(
        restCall(Method.POST, "/api/excursions", this::scheduleExcursion),
        restCall(Method.GET, "/api/excursions/:id", this::loadExcursion)
    ).withAutoAcl(true);

  }
}
