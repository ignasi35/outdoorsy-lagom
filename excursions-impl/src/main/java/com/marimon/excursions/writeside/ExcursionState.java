package com.marimon.excursions.writeside;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import com.marimon.excursions.Excursion;
import com.marimon.excursions.ExcursionStatus;
import lombok.Data;

import java.util.Optional;

@Data
public class ExcursionState implements Jsonable {

  private final Optional<Excursion> excursion;

  @JsonCreator
  public ExcursionState(Optional<Excursion> excursion) {
    this.excursion = excursion;
  }

  public static ExcursionState empty() {
    return new ExcursionState(Optional.empty());
  }

  public static ExcursionState create(ExcursionEvent.ExcursionScheduled evt) {
    return new ExcursionState(Optional.of(evt.getExcursion()));
  }

  public ExcursionStatus getStatus() {
    return excursion.map(Excursion::getStatus).orElse(ExcursionStatus.NOT_CREATED);
  }

  public Optional<Excursion> getExcursion() {
    return excursion;
  }

}
