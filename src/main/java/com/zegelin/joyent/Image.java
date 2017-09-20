package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Image extends Id<Image> {
    public final String name;
    public final String os;
    public final String version;

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
                 @JsonProperty("version") final String version) {
        super(id);
        this.name = name;
        this.os = os;
        this.version = version;
    }
}
