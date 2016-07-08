package sample.chirper.favorite.api;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import org.immutables.value.Value;

import java.time.Instant;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = Favor.class)
public interface AbstractFavor {

    @Value.Parameter
    String getFavoriteId();

    @Value.Parameter
    String favoredBy();

    @Value.Parameter
    Instant timestamp();
}
