package sample.chirper.favorite.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

public class FavoriteEntity extends PersistentEntity<FavoriteCommand, FavoriteEvent, FavoriteState> {

    @Override
    public Behavior initialBehavior(Optional<FavoriteState> snapshotState) {
        BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(FavoriteState.builder().build()));

        b.setCommandHandler(AddFavorite.class,
            (request, ctx) -> {
                FavoriteAdded event = FavoriteAdded.of(request.getUserId(), request.getFavoriteChirpId());
                return ctx.thenPersist(event, (evt) -> ctx.reply(Done.getInstance()));
            }
        );

        b.setEventHandler(FavoriteAdded.class,
            (evt) -> state().addFavoriteId(evt.getFavoriteId())
        );

        b.setCommandHandler(DeleteFavorite.class,
            (request, ctx) -> {
                FavoriteDeleted event = FavoriteDeleted.of(request.getUserId(), request.getFavoriteChirpId());
                return ctx.thenPersist(event, (evt) -> ctx.reply(Done.getInstance()));
            }
        );

        b.setEventHandler(FavoriteDeleted.class,
            (evt) -> state().deleteFavoriteId(evt.getFavoriteId())
        );

        b.setReadOnlyCommandHandler(GetFavorites.class,
            (request, ctx) -> {
                GetFavoritesReply favorites = GetFavoritesReply.builder()
                        .favoriteIds(state().getFavoriteIds())
                        .build();
                ctx.reply(favorites);
            }
        );

        return b.build();
    }
}
