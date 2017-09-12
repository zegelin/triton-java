package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

public class Package extends Id<Package> {
    public final String name;
    public final Number memory;
    public final Number disk;
    public final Number swap;
    public final Number lwps;
    public final Number vcpus;
    public final String version;
    public final String group;
    public final String description;

    public static IdOrName<Package> named(final String name) {
        return () -> name;
    }

    public static IdOrName<Package> withId(final UUID id) {
        return () -> id;
    }

    public static IdOrName<Package> withId(final String id) {
        return () -> UUID.fromString(id);
    }

    @JsonCreator
    Package(@JsonProperty("id") final UUID id,
            @JsonProperty("name") final String name,
            @JsonProperty("memory") final Number memory,
            @JsonProperty("disk") final Number disk,
            @JsonProperty("swap") final Number swap,
            @JsonProperty("lwps") final Number lwps,
            @JsonProperty("vcpus") final Number vcpus,
            @JsonProperty("version") final String version,
            @JsonProperty("group") final String group,
            @JsonProperty("description") final String description) {
        super(id);
        this.name = name;
        this.memory = memory;
        this.disk = disk;
        this.swap = swap;
        this.lwps = lwps;
        this.vcpus = vcpus;
        this.version = version;
        this.group = group;
        this.description = description;
    }
}
