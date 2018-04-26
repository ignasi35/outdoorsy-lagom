package com.marimon.excursions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Data;

@Data
public final class Excursion implements Jsonable{
  private final String location;
  private final String isoDate;
  private final ExcursionStatus status;

  @JsonCreator
  public Excursion(String location, String isoDate, ExcursionStatus status) {
    this.location = location;
    this.isoDate = isoDate;
    this.status = status;
  }
}
