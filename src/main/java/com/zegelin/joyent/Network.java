package com.zegelin.joyent;

import java.util.UUID;

@SuppressWarnings({"WeakerAccess", "unused"})
public class Network extends Id<Network> {
    Network(final UUID id) {
        super(id);
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
