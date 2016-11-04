package com.marimon.routes.readside;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;
import com.lightbend.lagom.javadsl.persistence.ReadSideProcessor;
import com.lightbend.lagom.javadsl.persistence.jdbc.JdbcReadSide;
import com.marimon.routes.RouteEvent;
import org.pcollections.PSequence;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;


public class RouteEventProcessor extends ReadSideProcessor<RouteEvent> {

  private final JdbcReadSide readSide;

  @Inject
  public RouteEventProcessor(JdbcReadSide readSide) {
    this.readSide = readSide;
  }

  @Override
  public PSequence<AggregateEventTag<RouteEvent>> aggregateTags() {
    return RouteEvent.TAG.allTags();
  }

  @Override
  public ReadSideHandler buildHandler() {
    return readSide.<RouteEvent>builder("routeOffset")
        .setGlobalPrepare(this::createTables)
        .setPrepare(this::prepareStatements)
        .setEventHandler(RouteEvent.RouteReported.class, this::handleNewRoute)
        .build()
        ;
  }

  private void createTables(Connection connection) throws SQLException {
    connection.prepareStatement("CREATE TABLE IF NOT EXISTS route_ids (" +
        "id VARCHAR(64), PRIMARY KEY (id));").execute();
  }

  private void prepareStatements(Connection connection, AggregateEventTag<RouteEvent> routeEventAggregateEventTag) throws SQLException {
  }

  private void handleNewRoute(Connection connection, RouteEvent.RouteReported newRoute) throws SQLException {
    String id = newRoute.getRoute().getRouteId().getId().toString();
    PreparedStatement statement = connection.prepareStatement("INSERT INTO route_ids (id) VALUES (?);");
    statement.setString(1, id);
    statement.execute();
  }


}
