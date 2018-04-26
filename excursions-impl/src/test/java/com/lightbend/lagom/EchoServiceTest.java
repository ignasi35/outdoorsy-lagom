package com.lightbend.lagom;

import com.lightbend.lagom.javadsl.testkit.ServiceTest;
import org.junit.Test;

import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

/**
 *
 */
public class EchoServiceTest {
  @Test
  public void shouldEcho() {
    ServiceTest.withServer(ServiceTest.defaultSetup(), server -> {
          EchoService client = server.client(EchoService.class);
          String response = client.echo().invoke("input").toCompletableFuture().get(5, TimeUnit.SECONDS);
          assertEquals("input", response);
        }
    );
  }
}
