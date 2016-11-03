package com.marimon.routes.impl;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Data;

@Data
public class PWayPoint implements Jsonable{

  private final double latitude;
  private final double longitude;

  @JsonCreator
  public PWayPoint(double latitude, double longitude){
    this.latitude = latitude;
    this.longitude = longitude;
  }
}
