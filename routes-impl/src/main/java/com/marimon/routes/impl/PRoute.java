package com.marimon.routes.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import com.marimon.routes.RouteId;
import lombok.Data;

@Data
public class PRoute implements Jsonable {

  private final RouteId routeId;
  private final String name;
  private final PTrack track;

  @JsonCreator
  public PRoute(RouteId routeId, String name, PTrack track) {
    this.routeId = routeId;
    this.name = name;
    this.track = track;
  }

}
