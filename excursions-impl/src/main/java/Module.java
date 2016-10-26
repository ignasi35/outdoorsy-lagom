import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.marimon.excursions.ExcursionsService;
import com.marimon.excursions.impl.ExcursionsServiceImpl;

public class Module extends AbstractModule implements ServiceGuiceSupport {

  @Override
  protected void configure() {
    bindServices(serviceBinding(ExcursionsService.class, ExcursionsServiceImpl.class));
  }
}
