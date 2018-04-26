package com.lightbend.lagom.chatty;

import com.lightbend.lagom.EchoService;
import com.lightbend.lagom.javadsl.api.ServiceCall;

import java.util.concurrent.CompletableFuture;

/**
 *
 */
public class EchoServiceImpl implements EchoService {

  public EchoServiceImpl(){

  }

  @Override
  public ServiceCall<String, String> echo() {
    return input -> CompletableFuture.completedFuture(input);
  }
}
