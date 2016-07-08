package sample.chirper.favorite.api;

import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.lightbend.lagom.javadsl.api.Descriptor;
import com.lightbend.lagom.javadsl.api.Service;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import org.pcollections.POrderedSet;

import static com.lightbend.lagom.javadsl.api.Service.*;

public interface FavoriteService extends Service {

    ServiceCall<FavoriteId, NotUsed> addFavorite(String userId);

    ServiceCall<FavoriteId, NotUsed> deleteFavorite(String userId);

    ServiceCall<NotUsed, POrderedSet<String>> getFavorites(String userId);

    ServiceCall<NotUsed, Source<Favor, ?>> getFavors(String favoriteId);

    @Override
    default Descriptor descriptor() {
        // @formatter:off
        return named("favoriteservice").with(
                pathCall("/api/favorites/:userId/add", this::addFavorite),
                pathCall("/api/favorites/:userId/delete", this::deleteFavorite),
                pathCall("/api/favorites/:userId/list", this::getFavorites),
                pathCall("/api/favors/:favoriteId/list", this::getFavors)
        ).withAutoAcl(true);
        // @formatter:on
    }
}