package com.marimon.routes;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;
import org.pcollections.PStack;

/**
 *
 */
@Data
public class Track {

  private final PStack<WayPoint> wayPoints;

  @JsonCreator
  public Track(PStack<WayPoint>  wayPoints){
    this.wayPoints = wayPoints;
  }

}
