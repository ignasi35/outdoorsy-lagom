package com.marimon.routes.readside;


import com.lightbend.lagom.javadsl.persistence.jdbc.JdbcSession;
import com.marimon.routes.RouteId;
import org.pcollections.ConsPStack;
import org.pcollections.PStack;

import java.util.UUID;

import javax.inject.Inject;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.CompletionStage;

public class RouteReadOnlyRepository {

  private final JdbcSession session;

  @Inject
  public RouteReadOnlyRepository(JdbcSession session) {
    this.session = session;
  }

  public CompletionStage<PStack<RouteId>> loadAll() {
    return session.withConnection(this::loadIds);
  }

  private PStack<RouteId> loadIds(Connection conn) throws SQLException {
    PStack<RouteId> rids = ConsPStack.empty();
    ResultSet rs = conn.createStatement().executeQuery("SELECT id from route_ids;");
    while (rs.next()) {
      rids = rids.plus(new RouteId(UUID.fromString(rs.getString("id"))));
    }
    return rids;
  }

}
