package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.google.common.base.MoreObjects;

import java.util.Objects;
import java.util.UUID;

public class Id<T> implements IdOrName<T> {
    public final UUID id;

    @JsonCreator
    Id(final UUID id) {
        this.id = id;
    }

    @Override
    public Object idOrName() {
        return id;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (!(o instanceof Id)) return false;
        final Id<?> id1 = (Id<?>) o;
        return Objects.equals(id, id1.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("id", id)
                .toString();
    }
}
