package com.marimon.routes;

import akka.Done;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import com.marimon.routes.impl.PRoute;
import lombok.Data;

import java.util.Optional;

public interface RouteCommand extends Jsonable {

  enum LoadRoute implements RouteCommand, PersistentEntity.ReplyType<Optional<PRoute>> {
    INSTANCE
  }

  @Data
  public final class ReportRoute
      implements RouteCommand,
      PersistentEntity.ReplyType<Done> {
    private final PRoute route;

    @JsonCreator
    public ReportRoute(PRoute route) {
      this.route = route;
    }
  }

}
