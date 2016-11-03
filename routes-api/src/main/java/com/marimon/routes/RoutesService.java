package com.marimon.routes;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.api.deser.MessageSerializer;
import com.lightbend.lagom.javadsl.api.transport.MessageProtocol;
import com.lightbend.lagom.javadsl.api.transport.Method;
import com.lightbend.lagom.javadsl.api.transport.NotAcceptable;
import com.lightbend.lagom.javadsl.api.transport.UnsupportedMediaType;

import java.util.List;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface RoutesService extends Service {

  ServiceCall<Route, RouteId> reportRoute();

  ServiceCall<NotUsed, Route> loadRoute(String id);

  @Override
  default Descriptor descriptor() {
    return named("routes")
        .withCalls(
            restCall(Method.POST, "/api/routes", this::reportRoute),
            restCall(Method.GET, "/api/routes/:xyz", this::loadRoute)
        )
        .withAutoAcl(true)
        ;
  }
}

class RouteIdSerializer implements MessageSerializer<NotUsed, RouteId> {

  @Override
  public NegotiatedSerializer<NotUsed, RouteId> serializerForRequest() {
    return null;
  }

  @Override
  public NegotiatedDeserializer<NotUsed, RouteId> deserializer(MessageProtocol protocol) throws UnsupportedMediaType {
    return null;
  }

  @Override
  public NegotiatedSerializer<NotUsed, RouteId> serializerForResponse(List<MessageProtocol> acceptedMessageProtocols) throws NotAcceptable {
    return null;
  }
}