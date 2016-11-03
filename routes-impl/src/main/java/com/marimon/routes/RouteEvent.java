package com.marimon.routes;


import com.fasterxml.jackson.annotation.JsonCreator;
import com.lightbend.lagom.serialization.Jsonable;
import com.marimon.routes.impl.PRoute;
import lombok.Data;
import java.util.UUID;

public interface RouteEvent extends Jsonable {

  @Data
  public final class RouteReported
      implements RouteEvent {
    private final PRoute route;


    @JsonCreator
    public RouteReported(PRoute route) {
      this.route = route;
    }
  }

}
