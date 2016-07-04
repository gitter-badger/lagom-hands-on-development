/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.activity.impl;

import akka.NotUsed;
import com.lightbend.lagom.javadsl.api.ServiceCall;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.CompletionStage;
import javax.inject.Inject;

import org.pcollections.POrderedSet;
import org.pcollections.PSequence;
import sample.chirper.activity.api.ActivityStreamService;
import sample.chirper.chirp.api.Chirp;
import sample.chirper.chirp.api.ChirpService;
import sample.chirper.chirp.api.HistoricalChirpsRequest;
import sample.chirper.chirp.api.LiveChirpsRequest;
import sample.chirper.favorite.api.FavoriteService;
import sample.chirper.friend.api.FriendService;

import akka.stream.javadsl.Source;

public class ActivityStreamServiceImpl implements ActivityStreamService {

  private final FriendService friendService;
  private final ChirpService chirpService;
  private final FavoriteService favoriteService;

  @Inject
  public ActivityStreamServiceImpl(FriendService friendService,
                                   ChirpService chirpService,
                                   FavoriteService favoriteService) {
    this.friendService = friendService;
    this.chirpService = chirpService;
    this.favoriteService = favoriteService;
  }

  @Override
  public ServiceCall<NotUsed, Source<Chirp, ?>> getLiveActivityStream(String userId) {
    return req -> {
      return friendService.getUser(userId).invoke().thenCompose(user -> {
        PSequence<String> userIds = user.getFriends().plus(userId);
        LiveChirpsRequest chirpsReq =  LiveChirpsRequest.of(userIds);
        // Note that this stream will not include changes to friend associates,
        // e.g. adding a new friend.
        CompletionStage<Source<Chirp, ?>> result = chirpService.getLiveChirps().invoke(chirpsReq);
        CompletionStage<POrderedSet<String>> favorites = favoriteService.getFavorites(userId).invoke();
        return result.thenCombine(favorites, (chirps, favs) -> {
          System.out.println(favs);
          return chirps.map(c -> {
            if (favs.contains(c.getUuid())) {
              return c.withIsFavorite(true);
            } else {
              return c;
            }
          });
        });
      });
    };
  }

  @Override
  public ServiceCall<NotUsed, Source<Chirp, ?>> getHistoricalActivityStream(String userId) {
    return req ->
      friendService.getUser(userId).invoke().thenCompose(user -> {
        PSequence<String> userIds = user.getFriends().plus(userId);
        // FIXME we should use HistoricalActivityStreamReq request parameter
        Instant fromTime = Instant.now().minus(Duration.ofDays(7));
        HistoricalChirpsRequest chirpsReq = HistoricalChirpsRequest.of(fromTime, userIds);
        CompletionStage<Source<Chirp, ?>> result = chirpService.getHistoricalChirps().invoke(chirpsReq);
        return result;
      });
  }

}
