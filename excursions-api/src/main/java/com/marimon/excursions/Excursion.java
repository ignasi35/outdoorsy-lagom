package com.marimon.excursions;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;

@JsonDeserialize
public final class Excursion {
  private final String location;
  private final String isoDate;
  private final ExcursionStatus status;

  public Excursion(String location, String isoDate, ExcursionStatus status) {
    this.location = location;
    this.isoDate = isoDate;
    this.status = status;
  }

  public String getLocation() {
    return location;
  }

  public String getIsoDate() {
    return isoDate;
  }

  public ExcursionStatus getStatus() {
    return status;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Excursion excursion = (Excursion) o;

    if (getLocation() != null ? !getLocation().equals(excursion.getLocation()) : excursion.getLocation() != null)
      return false;
    if (getIsoDate() != null ? !getIsoDate().equals(excursion.getIsoDate()) : excursion.getIsoDate() != null)
      return false;
    if (getStatus() != excursion.getStatus()) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = getLocation() != null ? getLocation().hashCode() : 0;
    result = 31 * result + (getIsoDate() != null ? getIsoDate().hashCode() : 0);
    result = 31 * result + (getStatus() != null ? getStatus().hashCode() : 0);
    return result;
  }
}
