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

    public static IdOrName<Network> withId(final UUID id) {
        return () -> id;
    }

    public static IdOrName<Network> withId(final String id) {
        return () -> UUID.fromString(id);
    }
}
