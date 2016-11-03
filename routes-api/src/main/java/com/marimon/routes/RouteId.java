package com.marimon.routes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Data;

import java.util.UUID;

@Data
public class RouteId implements Jsonable {
  private final UUID id;

  @JsonCreator
  public RouteId(UUID id) {
    this.id = id;
  }

}
