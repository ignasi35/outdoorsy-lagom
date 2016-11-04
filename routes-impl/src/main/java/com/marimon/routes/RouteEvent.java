package com.marimon.routes;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import com.marimon.routes.impl.PRoute;
import lombok.Data;
import java.util.UUID;

public interface RouteEvent extends Jsonable, AggregateEvent<RouteEvent> {
  int NUM_SHARDS = 4;

  AggregateEventShards<RouteEvent> TAG =
      AggregateEventTag.sharded(RouteEvent.class, "RouteEventTagName", NUM_SHARDS);

  default public AggregateEventTagger<RouteEvent> aggregateTag() {
    return TAG;
  }

  @Data
  public final class RouteReported
      implements RouteEvent {
    private final PRoute route;

    @JsonCreator
    public RouteReported(PRoute route) {
      this.route = route;
    }
  }

}
