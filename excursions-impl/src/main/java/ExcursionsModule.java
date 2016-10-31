import com.google.inject.AbstractModule;
import com.lightbend.lagom.internal.persistence.cassandra.CassandraPersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;
import com.marimon.excursions.ExcursionsService;
import com.marimon.excursions.impl.ExcursionsServiceImpl;

public class ExcursionsModule extends AbstractModule implements ServiceGuiceSupport {

  @Override
  protected void configure() {

    bindServices(serviceBinding(ExcursionsService.class, ExcursionsServiceImpl.class));

  }
}
