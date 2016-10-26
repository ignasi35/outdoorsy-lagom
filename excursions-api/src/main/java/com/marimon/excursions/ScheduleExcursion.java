package com.marimon.excursions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@JsonDeserialize
@Data
public final class ScheduleExcursion {

  private final String location;
  private final String isoDate;

  @JsonCreator
  public ScheduleExcursion(String location, String isoDate){

    this.location = location;
    this.isoDate = isoDate;
  }

}
