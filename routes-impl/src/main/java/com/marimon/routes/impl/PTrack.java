package com.marimon.routes.impl;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Data;
import org.pcollections.PStack;

@Data
public class PTrack implements Jsonable {

  private final PStack<PWayPoint> wayPoints;

  @JsonCreator
  public PTrack(PStack<PWayPoint> wayPoints) {
    this.wayPoints = wayPoints;
  }
}
