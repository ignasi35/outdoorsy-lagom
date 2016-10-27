package com.marimon.excursions;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@JsonDeserialize
public final class ScheduleExcursion {

  private final String location;
  private final String isoDate;

  @JsonCreator
  public ScheduleExcursion(String location, String isoDate){
    this.location = location;
    this.isoDate = isoDate;
  }

  public String getLocation() {
    return location;
  }

  public String getIsoDate() {
    return isoDate;
  }

  @Override
  public String toString() {
    return "ScheduleExcursion{" +
        "location='" + location + '\'' +
        ", isoDate='" + isoDate + '\'' +
        '}';
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    ScheduleExcursion that = (ScheduleExcursion) o;

    if (getLocation() != null ? !getLocation().equals(that.getLocation()) : that.getLocation() != null) return false;
    return getIsoDate() != null ? getIsoDate().equals(that.getIsoDate()) : that.getIsoDate() == null;

  }

  @Override
  public int hashCode() {
    int result = getLocation() != null ? getLocation().hashCode() : 0;
    result = 31 * result + (getIsoDate() != null ? getIsoDate().hashCode() : 0);
    return result;
  }
}
