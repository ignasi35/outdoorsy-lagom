package com.marimon.excursions;

import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.Data;

@Data
public class ExcursionId {
  public final String id;

  @JsonCreator
  public ExcursionId(String id) {
    this.id = id;
  }
}
