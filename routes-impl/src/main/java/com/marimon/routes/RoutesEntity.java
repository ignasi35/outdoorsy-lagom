package com.marimon.routes;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.marimon.routes.RouteCommand.*;
import com.marimon.routes.RouteEvent.*;
import com.marimon.routes.impl.PRoute;

import java.util.Optional;
import java.util.function.Function;


public class RoutesEntity extends PersistentEntity<RouteCommand, RouteEvent, Optional<PRoute>> {
  @Override
  public Behavior initialBehavior(Optional<Optional<PRoute>> snapshotState) {

    Optional<PRoute> route = snapshotState.flatMap(Function.identity());
    if (route.isPresent())
      return persisted(route.get());
    else
      return missing();
  }

  private Behavior missing() {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.empty());

    builder.setCommandHandler(
        ReportRoute.class,
        (cmd, ctx) ->
            ctx.thenPersist(
                new RouteReported(cmd.getRoute()),
                evt -> ctx.reply(Done.getInstance())
            )
    );

    builder.setEventHandlerChangingBehavior(
        RouteReported.class,
        evt -> persisted(evt.getRoute()));

    builder.setReadOnlyCommandHandler(LoadRoute.class,
        (cmd, ctx) -> ctx.reply(state()));

    return builder.build();
  }

  private Behavior persisted(PRoute route) {
    BehaviorBuilder builder = newBehaviorBuilder(Optional.of(route));

    builder.setReadOnlyCommandHandler(LoadRoute.class,
        (cmd, ctx) -> ctx.reply(state()));

    return builder.build();
  }

  // --------------------------------------------------------------------------------


}
