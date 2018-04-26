import com.google.inject.AbstractModule;
import com.lightbend.lagom.EchoService;
import com.lightbend.lagom.chatty.EchoServiceImpl;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

public class ExcursionsModule extends AbstractModule implements ServiceGuiceSupport {

  @Override
  protected void configure() {

    bindServices(serviceBinding(EchoService.class, EchoServiceImpl.class));
//    bindServices(serviceBinding(ExcursionsService.class, ExcursionsServiceImpl.class));

  }
}
