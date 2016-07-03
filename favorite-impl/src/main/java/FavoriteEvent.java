import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

import java.time.Instant;


public interface FavoriteEvent extends Jsonable {

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize(as = FavoriteAdded.class)
    interface AbstractFavoriteAdded extends FavoriteEvent {

        @Value.Parameter
        String getUserId();

        @Value.Parameter
        String getFavoriteId();

        @Value.Default
        default Instant getTimestamp() {
            return Instant.now();
        }
    }

    @Value.Immutable
    @ImmutableStyle
    @JsonDeserialize(as = FavoriteDeleted.class)
    interface AbstractFavoriteDeleted extends FavoriteEvent {

        @Value.Parameter
        String getUserId();

        @Value.Parameter
        String getFavoriteId();

        @Value.Default
        default Instant getTimestamp() {
            return Instant.now();
        }
    }
}
