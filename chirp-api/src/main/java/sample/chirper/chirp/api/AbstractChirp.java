/*
 * Copyright (C) 2016 Lightbend Inc. <http://www.lightbend.com>
 */
package sample.chirper.chirp.api;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.lightbend.lagom.javadsl.immutable.ImmutableStyle;
import com.lightbend.lagom.serialization.Jsonable;
import org.immutables.value.Value;

import javax.annotation.Nullable;

@Value.Immutable
@ImmutableStyle
@JsonDeserialize(as = Chirp.class)
public interface AbstractChirp extends Jsonable {

  @Value.Parameter
  String getUserId();

  @Value.Parameter
  String getMessage();

  @Value.Default
  default Instant getTimestamp() {
    return Instant.now();
  }

  @Value.Default
  default String getUuid() {
    return UUID.randomUUID().toString();
  }

  static Chirp of(String userId, String message, Optional<Instant> timestamp, Optional<String> uuid) {

    Chirp.Builder builder = Chirp.builder()
            .userId(userId)
            .message(message);

    timestamp.ifPresent(t -> builder.timestamp(t));
    uuid.ifPresent(u -> builder.uuid(u));

    return builder.build();
  }


}