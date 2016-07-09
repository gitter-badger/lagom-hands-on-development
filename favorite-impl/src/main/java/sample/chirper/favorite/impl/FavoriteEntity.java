package sample.chirper.favorite.impl;

import akka.Done;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;

import java.util.Optional;

/**
 * お気に入りに関する属性と振る舞いを持つエンティティ (write-side)
 */
public class FavoriteEntity extends PersistentEntity<FavoriteCommand, FavoriteEvent, FavoriteState> {

    @Override
    public Behavior initialBehavior(Optional<FavoriteState> snapshotState) {
        BehaviorBuilder b = newBehaviorBuilder(snapshotState.orElse(FavoriteState.builder().build()));

        /*
         * AddFavorite が送られてくる
         *  → FavoriteAdded イベントを作成
         *  → イベントを永続化
         *  → Done を送り返す
         */
        b.setCommandHandler(AddFavorite.class,
            (request, ctx) -> {
                FavoriteAdded event = FavoriteAdded.of(request.getUserId(), request.getFavoriteChirpId());
                return ctx.thenPersist(event, (evt) -> ctx.reply(Done.getInstance()));
            }
        );
        // FavoriteAdded イベントが起きたときは状態に favoriteId を追加
        b.setEventHandler(FavoriteAdded.class,
            (evt) -> state().addFavoriteId(evt.getFavoriteId())
        );

        /*
         * DeleteFavorite が送られてくる
         *  → FavoriteDeleted イベントを作成
         *  → イベントを永続化
         *  → Done を送り返す
         */
        b.setCommandHandler(DeleteFavorite.class,
            (request, ctx) -> {
                FavoriteDeleted event = FavoriteDeleted.of(request.getUserId(), request.getFavoriteChirpId());
                return ctx.thenPersist(event, (evt) -> ctx.reply(Done.getInstance()));
            }
        );
        // FavoriteDeleted イベントが起きたときは状態からfavoriteId を削除
        b.setEventHandler(FavoriteDeleted.class,
            (evt) -> state().deleteFavoriteId(evt.getFavoriteId())
        );

        /*
         * DeleteFavorite が送られてくる
         *  → GetFavoritesReply を作成して送り返す
         */
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
