package com.marimon.excursions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Data;

@Data
public class ExcursionId implements Jsonable {
  public final String id;

  @JsonCreator
  public ExcursionId(String id) {
    this.id = id;
  }
}
