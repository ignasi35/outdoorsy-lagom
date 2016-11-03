package com.marimon.routes;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

/**
 * A naïve type to represent a waypoint in a GPS track.
 */
@Data
public class WayPoint {

  private final double latitude;
  private final double longitude;

  @JsonCreator
  public WayPoint(double latitude, double longitude){
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
