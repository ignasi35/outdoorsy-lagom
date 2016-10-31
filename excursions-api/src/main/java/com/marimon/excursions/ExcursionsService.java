package com.marimon.excursions;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.Method;
import org.pcollections.PStack;

import java.util.Optional;

import static com.lightbend.lagom.javadsl.api.Service.*;


public interface ExcursionsService extends Service {
  ServiceCall<ScheduleExcursionRequest, ExcursionId> scheduleExcursion();

  ServiceCall<NotUsed, Optional<Excursion>> loadExcursion(String id);

  ServiceCall<NotUsed, Source<Excursion, ?>> loadExcursions();

  @Override
  default Descriptor descriptor() {
    return named("excursions").withCalls(
        restCall(Method.GET, "/api/excursions", this::loadExcursions),
        restCall(Method.POST, "/api/excursions", this::scheduleExcursion),
        restCall(Method.GET, "/api/excursions/:id", this::loadExcursion)
    )
//        .withExceptionSerializer(new MyExceptionSerializer())
        .withAutoAcl(true);

  }

//  class MyExceptionSerializer implements ExceptionSerializer {
//
//    @Override
//    public RawExceptionMessage serialize(Throwable exception, Collection<MessageProtocol> accept) {
//      return null;
//    }
//
//    @Override
//    public Throwable deserialize(RawExceptionMessage message) {
//      return null;
//    }
//  }
}
