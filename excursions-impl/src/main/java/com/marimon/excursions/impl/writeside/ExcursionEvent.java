package com.marimon.excursions.impl.writeside;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventShards;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import com.marimon.excursions.Excursion;
import lombok.Data;

import java.util.UUID;


public interface ExcursionEvent extends Jsonable, AggregateEvent<ExcursionEvent> {

  int NUM_SHARDS = 4;

  AggregateEventShards<ExcursionEvent> TAG =
      AggregateEventTag.sharded(ExcursionEvent.class, "ExcursionEventTagName", NUM_SHARDS);

  default public AggregateEventTagger<ExcursionEvent> aggregateTag() {
    return TAG;
  }

  @SuppressWarnings("serial")
  @Data
  final class ExcursionScheduled implements ExcursionEvent {
    private final UUID entityUuid;
    private final Excursion excursion;

    @JsonCreator
    public ExcursionScheduled(UUID entityUuid, Excursion excursion) {
      this.entityUuid = entityUuid;
      this.excursion = excursion;
    }

  }

}
