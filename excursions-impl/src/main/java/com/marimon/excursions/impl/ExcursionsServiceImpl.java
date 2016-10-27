package com.marimon.excursions.impl;


import akka.Done;
import akka.NotUsed;
import akka.japi.Pair;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.ResponseHeader;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.server.HeaderServiceCall;
import com.marimon.excursions.*;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class ExcursionsServiceImpl implements ExcursionsService {
  private final PersistentEntityRegistry persistentEntities;

  @Inject
  public ExcursionsServiceImpl(PersistentEntityRegistry registry) {
    this.persistentEntities = registry;
    registry.register(ExcursionEntity.class);
  }


  @Override
  public HeaderServiceCall<ScheduleExcursion, ExcursionId> scheduleExcursion() {
    return (reqHeader, excursion) -> {
      UUID itemId = UUID.randomUUID();
      ExcursionCommand.ScheduleExcursion cmd =
          new ExcursionCommand.ScheduleExcursion(
              excursion.getLocation(), excursion.getIsoDate());
      return entityRef(itemId)
          .ask(cmd)
          .thenApply(id -> Pair.create(ResponseHeader.OK.withHeader("Location", " /api/excursions/" + id.getId()), id));
    };
  }

  @Override
  public ServiceCall<NotUsed, Optional<Excursion>> loadExcursion(String id) {
    return request -> {
      return entityRef(UUID.fromString(id)).ask(new ExcursionCommand.LoadExcursion());
    };
  }

  private PersistentEntityRef<ExcursionCommand> entityRef(UUID itemId) {
    return persistentEntities.refFor(ExcursionEntity.class, itemId.toString());
  }

}
