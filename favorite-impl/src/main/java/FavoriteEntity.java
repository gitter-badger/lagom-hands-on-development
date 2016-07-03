import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

/**
 * Created by kazuki on 2016/07/03.
 */
public class FavoriteEntity extends PersistentEntity<FavoriteCommand, FavoriteEvent, FavoriteState> {

    @Override
    public Behavior initialBehavior(Optional<FavoriteState> snapshotState) {
        BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(FavoriteState.builder().build()));

        b.setCommandHandler(AddFavorite.class,
            (request, ctx) ->
                ctx.thenPersist(FavoriteAdded.of(request.getUserId(), request.getFavoriteChirpId()))
        );

        b.setEventHandler(FavoriteAdded.class,
            (evt) -> state().addFavoriteId(evt.getFavoriteId())
        );

        b.setCommandHandler(DeleteFavorite.class,
            (request, ctx) ->
                ctx.thenPersist(FavoriteDeleted.of(request.getUserId(), request.getFavoriteChirpId()))
        );

        b.setEventHandler(FavoriteDeleted.class,
            (evt) -> state().deleteFavoriteId(evt.getFavoriteId())
        );

        b.setReadOnlyCommandHandler(GetFavorites.class,
            (request, ctx) -> GetFavoritesReply.of(state().getFavoriteIds())
        );

        return b.build();
    }
}
