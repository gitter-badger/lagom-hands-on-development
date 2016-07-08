package sample.chirper.favorite.impl;

import akka.Done;
import akka.NotUsed;
import akka.stream.javadsl.Source;
import com.datastax.driver.core.Row;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRef;
import com.lightbend.lagom.javadsl.persistence.PersistentEntityRegistry;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraReadSide;
import com.lightbend.lagom.javadsl.persistence.cassandra.CassandraSession;
import org.pcollections.POrderedSet;
import sample.chirper.favorite.api.Favor;
import sample.chirper.favorite.api.Favores;
import sample.chirper.favorite.api.FavoriteId;
import sample.chirper.favorite.api.FavoriteService;

import javax.inject.Inject;
import java.time.Instant;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

public class FavoriteServiceImpl implements FavoriteService {

    private final PersistentEntityRegistry persistentEntities;

    private final CassandraSession db;

    @Inject
    public FavoriteServiceImpl(PersistentEntityRegistry persistentEntities,
                               CassandraReadSide readSide,
                               CassandraSession db) {
        this.persistentEntities = persistentEntities;
        this.persistentEntities.register(FavoriteEntity.class);
        this.db = db;

        readSide.register(FavoriteEventProcessor.class);
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
    public ServiceCall<NotUsed, POrderedSet<String>> getFavorites(String userId) {
        return request -> {
            CompletionStage<GetFavoritesReply> favorites =
                favoriteEntityRef(userId).ask(GetFavorites.of());
            return favorites.thenApply(rep -> rep.getFavoriteIds());
        };
    }

    @Override
    public ServiceCall<NotUsed, Source<Favor, ?>> getFavors(String favoriteId) {
        return request -> {
            Source<Favor, NotUsed> select =
                    db.select("SELECT * FROM favor WHERE favoriteId = ? ORDER BY timestamp ASC", favoriteId)
                      .map(this::mapFavor);
            return CompletableFuture.completedFuture(select);
        };
    }

    private Favor mapFavor(Row row) {
        return Favor.of(
                row.getString("favoriteId"),
                row.getString("favoredBy"),
                Instant.ofEpochMilli(row.getLong("timestamp")));
    }
}
