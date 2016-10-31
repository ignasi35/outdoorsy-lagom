package com.marimon.excursions.impl.readside;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;


public final class ExcursionReadWriteRepository {

  private final CassandraSession cassandraSession;

  @Inject
  public ExcursionReadWriteRepository(CassandraSession cassandraSession) {
    this.cassandraSession = cassandraSession;
  }

  public CompletionStage<Done> deleteAll() {
    return cassandraSession.prepare("TRUNCATE excursions;")
        .thenCompose(stmt -> cassandraSession.executeWrite(stmt.bind()));
  }

}
