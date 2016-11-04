package com.marimon.routes;

import akka.NotUsed;
import akka.japi.Pair;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.api.transport.ResponseHeader;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.ReadSide;
import com.lightbend.lagom.javadsl.server.HeaderServiceCall;

import com.marimon.routes.RouteCommand.*;
import com.marimon.routes.impl.PRoute;
import com.marimon.routes.impl.PTrack;
import com.marimon.routes.impl.PWayPoint;
import com.marimon.routes.readside.RouteEventProcessor;
import com.marimon.routes.readside.RouteReadOnlyRepository;
import org.pcollections.ConsPStack;
import org.pcollections.HashTreePMap;
import org.pcollections.PStack;

import javax.inject.Inject;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

public class RoutesServiceImpl implements RoutesService {

  private final PersistentEntityRegistry entityRegistry;
  private final RouteReadOnlyRepository repository;

  @Inject
  public RoutesServiceImpl(PersistentEntityRegistry entityRegistry, ReadSide readSide, RouteReadOnlyRepository repository) {
    this.entityRegistry = entityRegistry;
    this.repository = repository;

    entityRegistry.register(RoutesEntity.class);
    readSide.register(RouteEventProcessor.class);
  }

  @Override
  public HeaderServiceCall<Route, RouteId> reportRoute() {
    return (reqHeaders, route) -> {
      RouteId routeId = new RouteId(UUID.randomUUID());
      return entityRef(routeId)
          .ask(new ReportRoute(toPersistent(route, routeId)))
          .thenApply(d -> {
            ResponseHeader created = new ResponseHeader(201, new MessageProtocol(), HashTreePMap.empty());
            return Pair.create(created.withHeader("Location", " /api/excursions/" + routeId.getId()), routeId);
          });
    };
  }

  @Override
  public ServiceCall<NotUsed, Route> loadRoute(String id) {
    return request ->
        entityRef(new RouteId(UUID.fromString(id)))
            .<Optional<PRoute>, LoadRoute>ask(LoadRoute.INSTANCE)
            .thenApply(maybePRoute -> {
                  if (maybePRoute.isPresent())
                    return toRoute(maybePRoute.get());
                  else
                    throw new NotFound(id + " not found");
                }
            );
  }


  @Override
  public ServiceCall<NotUsed, PStack<RouteId>> loadRoutes() {
    return request -> repository.loadAll();
  }

  // --------------------------------------------------------------------------------
  private PersistentEntityRef<RouteCommand> entityRef(RouteId routeId) {
    return entityRegistry.refFor(RoutesEntity.class, routeId.getId().toString());
  }

  private Route toRoute(PRoute pr) {
    PStack<WayPoint> wps = ConsPStack.from(
        pr.getTrack().getWayPoints()
            .stream()
            .map(wp -> new WayPoint(wp.getLatitude(), wp.getLongitude()))
            .collect(Collectors.toList()));
    Track t = new Track(wps);
    Route r = new Route(pr.getName(), t);
    return r;
  }

  private PRoute toPersistent(Route route, RouteId routeId) {
    PStack<PWayPoint> pwp = ConsPStack.from(
        route.getTrack().getWayPoints()
            .stream()
            .map(wp -> new PWayPoint(wp.getLatitude(), wp.getLongitude()))
            .collect(Collectors.toList()));
    PTrack pt = new PTrack(pwp);
    return new PRoute(routeId, route.getName(), pt);
  }

}
