package com.marimon.excursions;

import com.lightbend.lagom.javadsl.api.deser.ExceptionMessage;
import com.lightbend.lagom.javadsl.api.transport.NotFound;
import com.lightbend.lagom.javadsl.api.transport.TransportErrorCode;
import com.lightbend.lagom.javadsl.api.transport.TransportException;

import java.util.function.Function;


public interface FailedReturns {

  Function<String, TransportException> ExcursionNotFound = NotFound::new;

  Function<String, TransportException> BadRequest_InvalidIdFormat =
      badId -> {
        ExceptionMessage exceptionMessage =
            new ExceptionMessage("Invalid Id: " + badId, "ExcursionId must be a properly formatted UUID.");
        return TransportException.fromCodeAndMessage(TransportErrorCode.BadRequest, exceptionMessage);
      };

}
