import com.google.inject.AbstractModule;
import com.lightbend.lagom.javadsl.server.ServiceGuiceSupport;

/**
 * Created by kazuki on 2016/07/03.
 */
public class FavoriteModule extends AbstractModule implements ServiceGuiceSupport {

    @Override
    protected void configure() {
        bindServices(serviceBinding(FavoriteService.class, FavoriteServiceImpl.class));
    }
}
