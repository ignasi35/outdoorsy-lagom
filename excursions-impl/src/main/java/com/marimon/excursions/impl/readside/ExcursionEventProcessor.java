package com.marimon.excursions.impl.readside;

import akka.Done;
import com.datastax.driver.core.BoundStatement;
import com.datastax.driver.core.PreparedStatement;
import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import com.marimon.excursions.Excursion;
import com.marimon.excursions.impl.writeside.ExcursionEvent;
import com.marimon.excursions.impl.writeside.ExcursionEvent.*;
import org.joda.time.Days;
import org.joda.time.LocalDate;
import org.joda.time.MutableDateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.pcollections.PSequence;

import javax.inject.Inject;
import java.util.List;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide.completedStatement;

public class ExcursionEventProcessor extends ReadSideProcessor<ExcursionEvent> {

  private final CassandraSession cassandraSession;
  private final CassandraReadSide readSide;
  private PreparedStatement insertExcursion;

  @Inject
  public ExcursionEventProcessor(CassandraSession cassandraSession, CassandraReadSide readSide) {
    this.cassandraSession = cassandraSession;
    this.readSide = readSide;
  }

  @Override
  public PSequence<AggregateEventTag<ExcursionEvent>> aggregateTags() {
    return ExcursionEvent.TAG.allTags();
  }

  @Override
  public ReadSideHandler<ExcursionEvent> buildHandler() {
    return readSide.<ExcursionEvent>builder("excursionOffset")
        .setGlobalPrepare(this::createTables)
        .setPrepare(tag -> this.prepareStatements())
        .setEventHandler(ExcursionScheduled.class, e -> this.handleExcursionScheduled(e.getExcursion()))
        .build();
  }

  /**
   * Create the tables needed for this read side if not already created.
   */
  private CompletionStage<Done> createTables() {
    // TODO: introduce 'group' concept into excursions. A 'group' is a team of outdoorsy people with a name, one or more guides, etc...
    return cassandraSession.executeCreateTable(
        "CREATE TABLE IF NOT EXISTS excursions ( " +
            " group_id text, " +
            " day_of_excursion date, " +
            " location text, " +
            " PRIMARY KEY ((group_id), day_of_excursion) )" +
            " with clustering order by ( day_of_excursion desc);");
  }

  private CompletionStage<Done> prepareStatements() {
    return
        cassandraSession
            .prepare("INSERT INTO excursions (group_id, day_of_excursion, location) VALUES ('nogroup', ?, ?)")
            .thenApply(stmt -> {
              setInsertExcursionStatement(stmt);
              return Done.getInstance();
            });
  }

  private CompletionStage<List<BoundStatement>> handleExcursionScheduled(Excursion excursion) {
    BoundStatement insertStatement = insertExcursion.bind()
        .setString("location", excursion.getLocation())
        .setDate("day_of_excursion", buildCassandraDate(excursion));

    return completedStatement(insertStatement);
  }

  private com.datastax.driver.core.LocalDate buildCassandraDate(Excursion excursion) {
    LocalDate jodaDate = ISODateTimeFormat.yearMonthDay().parseLocalDate(excursion.getIsoDate());
    return com.datastax.driver.core.LocalDate.fromYearMonthDay(jodaDate.getYear(), jodaDate.getMonthOfYear(), jodaDate.getDayOfMonth());
  }

  private void setInsertExcursionStatement(PreparedStatement stmt) {
    insertExcursion = stmt;
  }

}
