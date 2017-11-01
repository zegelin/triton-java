package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Enums;

import java.util.UUID;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Image extends Id<Image> {
    public final String name;
    public final String os;
    public final String version;
    public final State state;
    public final Error error;

    public static class Error {
        public final String code;
        public final String message;

        @JsonCreator
        public Error(@JsonProperty("code") final String code,
                     @JsonProperty("message") final String message) {
            this.code = code;
            this.message = message;
        }
    }

    public enum State {
        ACTIVE,
        UNACTIVATED,
        DISABLED,
        CREATING,
        FAILED,
        UNKNOWN;

        @JsonCreator
        static State forJsonString(final String state) {
            return Enums.getIfPresent(State.class, state.toUpperCase()).or(UNKNOWN);
        }
    }

    public static IdOrName<Image> named(final String name) {
        return () -> name;
    }

    public static IdOrName<Image> withId(final UUID id) {
        return () -> id;
    }

    public static IdOrName<Image> withId(final String id) {
        return () -> UUID.fromString(id);
    }

    @JsonCreator()
    public Image(@JsonProperty("id") final UUID id,
                 @JsonProperty("name") final String name,
                 @JsonProperty("os") final String os,
                 @JsonProperty("version") final String version,
                 @JsonProperty("state") final State state,
                 @JsonProperty("error") final Error error) {
        super(id);
        this.name = name;
        this.os = os;
        this.version = version;
        this.state = state;
        this.error = error;
    }
}
