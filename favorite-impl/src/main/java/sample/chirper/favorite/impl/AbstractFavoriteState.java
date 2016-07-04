package sample.chirper.favorite.impl;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;
import org.pcollections.*;

import javax.validation.constraints.NotNull;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = FavoriteState.class)
public interface AbstractFavoriteState extends Jsonable {

    @Value.Default
    default POrderedSet<String> getFavoriteIds() {
        return OrderedPSet.empty();
    }

    default FavoriteState addFavoriteId(String favoriteId) {
        POrderedSet<String> newFavoriteIds = getFavoriteIds().plus(favoriteId);
        return FavoriteState.builder().from(this)
                .favoriteIds(newFavoriteIds)
                .build();
    }

    default FavoriteState deleteFavoriteId(String favoriteId) {
        POrderedSet<String> newFavoriteIds = getFavoriteIds().minus(favoriteId);
        return FavoriteState.builder().from(this)
                .favoriteIds(newFavoriteIds)
                .build();
    }
}
