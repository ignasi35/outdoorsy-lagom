package com.marimon.excursions.impl;

import lombok.Data;


@Data
public class ExcursionId {
  public final String id;

  public ExcursionId(String id) {
    this.id = id;
  }
}
