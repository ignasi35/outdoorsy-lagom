package com.marimon.excursions.impl;


import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.marimon.excursions.Excursion;
import com.marimon.excursions.ExcursionsService;
import com.marimon.excursions.ScheduleExcursion;

import javax.inject.Inject;
import java.util.UUID;

public class ExcursionsServiceImpl implements ExcursionsService {
  private final PersistentEntityRegistry persistentEntities;

  @Inject
  public ExcursionsServiceImpl(PersistentEntityRegistry registry) {
    this.persistentEntities = registry;
    registry.register(ExcursionEntity.class);
  }


  @Override
  public ServiceCall<ScheduleExcursion, Done> scheduleExcursion() {
    return excursion -> {
      UUID itemId = UUID.randomUUID();
      ExcursionCommand.ScheduleExcursion cmd = new ExcursionCommand.ScheduleExcursion(excursion.getLocation(), excursion.getIsoDate());
      return entityRef(itemId).ask(cmd);
    };

  }


  @Override
  public ServiceCall<NotUsed, Excursion> loadExcursion() {
    return null;
  }

  private PersistentEntityRef<ExcursionCommand> entityRef(UUID itemId) {
    return persistentEntities.refFor(ExcursionEntity.class, itemId.toString());
  }

}
