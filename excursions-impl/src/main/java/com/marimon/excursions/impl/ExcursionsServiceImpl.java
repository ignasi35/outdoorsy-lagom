package com.marimon.excursions.impl;


import akka.NotUsed;
import akka.japi.Pair;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol;
import com.lightbend.lagom.javadsl.api.transport.ResponseHeader;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.server.HeaderServiceCall;
import com.marimon.excursions.*;
import com.marimon.excursions.impl.readside.ExcursionEventProcessor;
import com.marimon.excursions.impl.readside.ExcursionReadOnlyRepository;
import com.marimon.excursions.impl.writeside.ExcursionEntity;
import org.pcollections.HashTreePMap;

import javax.inject.Inject;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

public class ExcursionsServiceImpl implements ExcursionsService {
  private final PersistentEntityRegistry persistentEntities;
  private final ExcursionReadOnlyRepository repository;

  @Inject
  public ExcursionsServiceImpl(
      PersistentEntityRegistry registry,
      ReadSide readSide,
      ExcursionReadOnlyRepository repository) {
    this.persistentEntities = registry;
    this.repository = repository;


    registry.register(ExcursionEntity.class);
    readSide.register(ExcursionEventProcessor.class);
  }


  @Override
  public HeaderServiceCall<ScheduleExcursionRequest, ExcursionId> scheduleExcursion() {
    return (reqHeader, excursion) -> {
      UUID itemId = UUID.randomUUID();
      ExcursionCommand.ScheduleExcursion cmd =
          new ExcursionCommand.ScheduleExcursion(
              excursion.getLocation(), excursion.getIsoDate());
      return entityRef(itemId)
          .ask(cmd)
          .thenApply(id -> {
            ResponseHeader created = new ResponseHeader(201, new MessageProtocol(), HashTreePMap.empty());
            return Pair.create(created.withHeader("Location", " /api/excursions/" + id.getId()), id);
          });
    };
  }

  @Override
  public ServiceCall<NotUsed, Source<Excursion, ?>> loadExcursions() {
    return request -> repository.loadAll();
  }

  @Override
  public HeaderServiceCall<NotUsed, Excursion> loadExcursion(String id) {
    // TODO: replace this error management with ExceptionHandler at service.
    return (request, notused) ->
        CompletableFuture
            .supplyAsync(() -> UUID.fromString(id))
            .thenCompose(itemId -> entityRef(itemId).ask(new ExcursionCommand.LoadExcursion()).thenApply(exc -> Pair.create(ResponseHeader.OK, (Excursion) exc)))
            .handle((p, t) -> {
                  if (t instanceof CompletionException) {
                    if (t.getCause() instanceof PersistentEntity.UnhandledCommandException) {
                      throw FailedReturns.ExcursionNotFound.apply(id);
                    } else if (t.getCause() instanceof IllegalArgumentException) {
                      throw FailedReturns.BadRequest_InvalidIdFormat.apply(id);
                    }
                  }
                  return p;
                }
            );
  }


  private PersistentEntityRef<ExcursionCommand> entityRef(UUID itemId) {
    return persistentEntities.refFor(ExcursionEntity.class, itemId.toString());
  }

}
