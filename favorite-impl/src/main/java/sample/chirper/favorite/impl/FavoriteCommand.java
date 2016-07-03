package sample.chirper.favorite.impl;

import akka.Done;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.javadsl.persistence.PersistentEntity;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;
import org.pcollections.PSequence;
import org.pcollections.TreePVector;

public interface FavoriteCommand extends Jsonable {

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize(as = AddFavorite.class)
    interface AbstractAddFavorite extends FavoriteCommand, PersistentEntity.ReplyType<Done> {

        @Value.Parameter
        String getUserId();

        @Value.Parameter
        String getFavoriteChirpId();
    }

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize(as = DeleteFavorite.class)
    interface AbstractDeleteFavorite extends FavoriteCommand, PersistentEntity.ReplyType<Done> {

        @Value.Parameter
        String getUserId();

        @Value.Parameter
        String getFavoriteChirpId();
    }

    @Value.Immutable(singleton = true)
    @ImmutableStyle
    @JsonDeserialize(as = GetFavorites.class)
    interface AbstractGetFavorites extends FavoriteCommand, PersistentEntity.ReplyType<GetFavoritesReply> {
    }

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize(as = GetFavoritesReply.class)
    interface AbstractGetFavoritesReply extends Jsonable {

        @Value.Default
        default PSequence<String> getFavoriteIds() {
            return TreePVector.empty();
        }
    }

}
