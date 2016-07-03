package sample.chirper.favorite.impl;

import akka.Done;
import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import org.pcollections.PSequence;
import sample.chirper.favorite.api.FavoriteId;
import sample.chirper.favorite.api.FavoriteService;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

public class FavoriteServiceImpl implements FavoriteService {

    private final PersistentEntityRegistry persistentEntities;

    @Inject
    public FavoriteServiceImpl(PersistentEntityRegistry persistentEntities) {
        this.persistentEntities = persistentEntities;
        this.persistentEntities.register(FavoriteEntity.class);
    }

    private PersistentEntityRef<FavoriteCommand> favoriteEntityRef(String userId) {
        return persistentEntities.refFor(FavoriteEntity.class, userId);
    }

    @Override
    public ServiceCall<FavoriteId, NotUsed> addFavorite(String userId) {
        return request -> {
            CompletionStage<Done> adding =
                favoriteEntityRef(userId).ask(AddFavorite.of(userId, request.getFavoriteId()));
            return adding.thenApply(ack -> NotUsed.getInstance());
        };
    }

    @Override
    public ServiceCall<FavoriteId, NotUsed> deleteFavorite(String userId) {
        return request -> {
            CompletionStage<Done> deleting =
                favoriteEntityRef(userId).ask(DeleteFavorite.of(userId, request.getFavoriteId()));
            return deleting.thenApply(ack -> NotUsed.getInstance());
        };
    }

    @Override
    public ServiceCall<NotUsed, PSequence<String>> getFavorites(String userId) {
        return request -> {
            CompletionStage<GetFavoritesReply> favorites =
                favoriteEntityRef(userId).ask(GetFavorites.of());
            return favorites.thenApply(rep -> rep.getFavoriteIds());
        };
    }
}
