package com.marimon.excursions.impl;


import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import com.marimon.excursions.Excursion;
import lombok.Data;

import java.util.Optional;
import java.util.UUID;

public interface ExcursionCommand extends Jsonable {


  @SuppressWarnings("serial")
  @Data
  @JsonDeserialize
  public final class ScheduleExcursion
      implements ExcursionCommand,
      CompressedJsonable,
      PersistentEntity.ReplyType<Done> {
    public final String location;
    public final String isoDate;

    public ScheduleExcursion(String location, String isoDate) {
      this.location = location;
      this.isoDate = isoDate;
    }
  }

  @SuppressWarnings("serial")
  @Data
  @JsonDeserialize
  public final class LoadExcursion
      implements ExcursionCommand,
      CompressedJsonable,
      PersistentEntity.ReplyType<Optional<Excursion>> {

    private final UUID id;

    public LoadExcursion(UUID id) {
      this.id = id;
    }
  }


}