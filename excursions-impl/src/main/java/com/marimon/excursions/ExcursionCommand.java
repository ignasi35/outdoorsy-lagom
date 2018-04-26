package com.marimon.excursions;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.CompressedJsonable;
import com.lightbend.lagom.serialization.Jsonable;
import lombok.Data;

public interface ExcursionCommand extends Jsonable {


  @SuppressWarnings("serial")
  @Data
  public final class ScheduleExcursion
      implements ExcursionCommand,
      PersistentEntity.ReplyType<ExcursionId> {
    public final String location;
    public final String isoDate;

    @JsonCreator
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
      PersistentEntity.ReplyType<Excursion> {

    public LoadExcursion() {
    }
  }


}
