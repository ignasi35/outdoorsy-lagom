package com.marimon.excursions.impl.readside;

import akka.Done;
import akka.stream.javadsl.Source;
import com.datastax.driver.core.Statement;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.marimon.excursions.Excursion;
import com.marimon.excursions.ExcursionStatus;

import javax.inject.Inject;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public final class ExcursionReadOnlyRepository {

  private final CassandraSession cassandraSession;

  @Inject
  public ExcursionReadOnlyRepository(CassandraSession cassandraSession) {
    this.cassandraSession = cassandraSession;
  }

  public CompletionStage<Source<Excursion, ?>> loadAll() {
    Source<Excursion, ?> selects =
        cassandraSession
            .select("SELECT * FROM excursions;")
            .map(r ->
                new Excursion(r.getString("location"), r.getDate("day_of_excursion").toString(), ExcursionStatus.SCHEDULED)
            );
    return CompletableFuture.completedFuture(selects);
  }

}

