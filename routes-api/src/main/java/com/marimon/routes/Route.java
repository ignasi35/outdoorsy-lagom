package com.marimon.routes;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Data;

import java.util.Optional;

/**
 *
 */
@Data
public class Route implements Jsonable {

  private final String name;
  private final Track track;

  @JsonCreator
  public Route(String name , Track track) {
    this.name = name;
    this.track = track;
  }

}
