package com.marimon.excursions.impl.writeside;

import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.marimon.excursions.Excursion;
import com.marimon.excursions.ExcursionId;
import com.marimon.excursions.ExcursionStatus;
import com.marimon.excursions.FailedReturns;
import com.marimon.excursions.impl.ExcursionCommand;
import com.marimon.excursions.impl.ExcursionCommand.LoadExcursion;
import com.marimon.excursions.impl.ExcursionCommand.ScheduleExcursion;
import com.marimon.excursions.impl.writeside.ExcursionEvent.ExcursionScheduled;

import java.util.Optional;
import java.util.UUID;


public class ExcursionEntity extends PersistentEntity<ExcursionCommand, ExcursionEvent, ExcursionState> {

  @Override
  public Behavior initialBehavior(Optional<ExcursionState> snapshotState) {
    ExcursionStatus excursionStatus = snapshotState.map(ExcursionState::getStatus).orElse(ExcursionStatus.NOT_CREATED);
    switch (excursionStatus) {
      case NOT_CREATED:
        return empty();
      case SCHEDULED:
        return scheduled(snapshotState.get());
      default:
        throw new UnsupportedOperationException();
    }
  }


  public Behavior empty() {
    BehaviorBuilder b = newBehaviorBuilder(ExcursionState.empty());

    b.setCommandHandler(
        ScheduleExcursion.class,
        (cmd, ctx) -> {
          Excursion excursion = new Excursion(cmd.location, cmd.isoDate, ExcursionStatus.SCHEDULED);
          UUID id = entityUuid();
          return ctx.thenPersist(
              new ExcursionScheduled(id, excursion),
              evt -> ctx.reply(new ExcursionId(id.toString())));
        });

    b.setEventHandlerChangingBehavior(ExcursionScheduled.class, evt ->
        scheduled(ExcursionState.create(evt)));

    return b.build();
  }

  public Behavior scheduled(ExcursionState excursionState) {
    BehaviorBuilder b = newBehaviorBuilder(excursionState);

    // retrieve excursion
    b.setReadOnlyCommandHandler(LoadExcursion.class, this::getItem);

    return b.build();
  }

  // ----------------------------------------------------------------------------------------------------

  private void getItem(LoadExcursion cmd, ReadOnlyCommandContext<Excursion> ctx) {
    ctx.reply(state().getExcursion().get());
  }

  private UUID entityUuid() {
    return UUID.fromString(entityId());
  }
}
