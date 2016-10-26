package com.marimon.excursions.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.PSequence;

import javax.inject.Inject;

public class ExcursionEventProcessor extends ReadSideProcessor<ExcursionEvent> {

  private final CassandraReadSide cassandraReadSide;
  private final CassandraSession cassandraSession;

  @Inject
  public ExcursionEventProcessor(CassandraReadSide cassandraReadSide, CassandraSession cassandraSession) {
    this.cassandraReadSide = cassandraReadSide;
    this.cassandraSession = cassandraSession;
  }

  @Override
  public ReadSideHandler<ExcursionEvent> buildHandler() {
    return null;
  }

  @Override
  public PSequence<AggregateEventTag<ExcursionEvent>> aggregateTags() {
    return null;
  }
}
