import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.marimon.excursions.ExcursionsService;
import com.marimon.excursions.ExcursionsServiceImpl;

public class ExcursionsModule extends AbstractModule implements ServiceGuiceSupport {

  @Override
  protected void configure() {

    bindServices(serviceBinding(ExcursionsService.class, ExcursionsServiceImpl.class));

  }
}
