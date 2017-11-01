package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Network extends Id<Network> {
    public final String name;
    public final boolean isPublic;
    public final boolean isFabric;
    public final String description;

    Network(@JsonProperty("id") final UUID id,
            @JsonProperty("name") final String name,
            @JsonProperty("public") final boolean isPublic,
            @JsonProperty("fabric") final boolean isFabric,
            @JsonProperty("description") final String description
            ) {
        super(id);
        this.name = name;
        this.isPublic = isPublic;
        this.isFabric = isFabric;
        this.description = description;
    }

    public static IdOrName<Network> named(final String name) {
        return () -> name;
    }

    public static Id<Network> withId(final UUID id) {
        return new Id<>(id);
    }

    public static Id<Network> withId(final String id) {
        return withId(UUID.fromString(id));
    }
}
