package com.marimon.excursions.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.AggregateEvent;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTagger;
import com.lightbend.lagom.serialization.Jsonable;
import com.marimon.excursions.Excursion;
import lombok.Data;

import java.util.UUID;


public interface ExcursionEvent extends Jsonable, AggregateEvent<ExcursionEvent> {

  @Override
  default AggregateEventTagger<ExcursionEvent> aggregateTag() {
    return null;
  }

  @SuppressWarnings("serial")
  @Data
  @JsonDeserialize
  public class ExcursionScheduled implements ExcursionEvent {
    private final UUID entityUuid;
    private final Excursion excursion;

    public ExcursionScheduled(UUID entityUuid, Excursion excursion) {
      this.entityUuid = entityUuid;
      this.excursion = excursion;
    }
  }

}
