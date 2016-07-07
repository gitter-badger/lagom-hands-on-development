package sample.chirper.favorite.impl;

import com.lightbend.lagom.javadsl.persistence.AggregateEventTag;

/**
 * Created by kazuki on 2016/07/05.
 */
public class FavoriteEventTag {

    public static final AggregateEventTag<FavoriteEvent> INSTANCE =
            AggregateEventTag.of(FavoriteEvent.class);

}
