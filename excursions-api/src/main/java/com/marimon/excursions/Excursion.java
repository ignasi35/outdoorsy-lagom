package com.marimon.excursions;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@JsonDeserialize
@Data
public final class Excursion {
  private final String location;
  private final String isoDate;
  private final ExcursionStatus status ;

  public Excursion(String location, String isoDate, ExcursionStatus status) {
    this.location = location;
    this.isoDate = isoDate;
    this.status = status;
  }

}
