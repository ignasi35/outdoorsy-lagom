package com.marimon.excursions.impl;


import com.marimon.excursions.Excursion;
import com.marimon.excursions.ExcursionStatus;

import java.util.Optional;

public class ExcursionState {

  private final Optional<Excursion> excursion;

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
