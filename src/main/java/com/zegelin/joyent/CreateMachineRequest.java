package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class CreateMachineRequest {
    public static class Locality {
        public final boolean strict;

        public final List<Id<Machine>> near;
        public final List<Id<Machine>> far;

        public Locality(final boolean strict, final Collection<? extends Id<Machine>> near, final Collection<? extends Id<Machine>> far) {
            this.strict = strict;
            this.near = ImmutableList.copyOf(near);
            this.far = ImmutableList.copyOf(far);
        }

//        public Locality(final Builder builder) {
//            this(builder.strict, builder.near.build(), builder.far.build());
//        }
//
//        public static Locality near(final Id<Machine>... machine) {
//            return near(ImmutableList.copyOf(machine));
//        }
//
//        public static Locality near(final List<Id<Machine>> machines) {
//            return new Locality(false, machines, ImmutableList.of());
//        }
//
//        public static Locality far(final Id<Machine>... machine) {
//            return far(ImmutableList.copyOf(machine));
//        }
//
//        public static Locality far(final List<Id<Machine>> machines) {
//            return new Locality(false, ImmutableList.of(), machines);
//        }
//
//        public Builder builder() {
//            return new Builder();
//        }
//
//        static class Builder {
//            public boolean strict;
//
//            public final ImmutableList.Builder<Id<Machine>> near = ImmutableList.builder();
//            public final ImmutableList.Builder<Id<Machine>> far = ImmutableList.builder();
//
//            public Builder strict() {
//                this.strict = true;
//                return this;
//            }
//
//            public Builder near(final Id<Machine> machine) {
//                near.add(machine);
//                return this;
//            }
//
//            public Builder near(final Id<Machine>... machines) {
//                near.add(machines);
//                return this;
//            }
//
//            public Builder near(final Iterable<Id<Machine>> machines) {
//                near.addAll(machines);
//                return this;
//            }
//
//            public Locality build() {
//                return new Locality(this);
//            }
//        }
    }

    @JsonProperty
    public final String name;

    @JsonProperty("package")
    public final IdOrName<Package> pkg;

    @JsonProperty
    public final IdOrName<Image> image;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public final List<Id<Network>> networks;

    public final Locality locality;

//    @JsonUnwrapped(prefix = "metadata")
    public final Map<String, String> metadata;

    @JsonUnwrapped
    @JsonProperty
    public final Map<String, String> tags;

    @JsonProperty
    public final boolean firewallEnabled;

    public CreateMachineRequest(final Builder builder) {
        this.name = builder.name;
        this.pkg = builder.pkg;
        this.image = builder.image;
        this.networks = builder.networks.build();
        this.locality = builder.locality;
        this.metadata = ImmutableMap.of("test", "foo-bar");
        this.tags = builder.tags.build();
        this.firewallEnabled = false;
    }

    public static class Builder {
        private final TritonClient tritonClient;

        public final IdOrName<Package> pkg;
        public final IdOrName<Image> image;
        public ImmutableList.Builder<Id<Network>> networks = ImmutableList.builder();

        public final ImmutableMap.Builder<String, String> metadata = ImmutableMap.builder();
        public final ImmutableMap.Builder<String, String> tags = ImmutableMap.builder();

        public String name;
        public Locality locality;

        public Builder(final TritonClient tritonClient, final IdOrName<Package> pkg, final IdOrName<Image> image) {
            this.tritonClient = tritonClient;
            this.pkg = pkg;
            this.image = image;
        }

        public Future<Machine> create() {
            return tritonClient.createMachine(new CreateMachineRequest(this));
        }

        public Builder withName(final String name) {
            this.name = name;
            return this;
        }

        public Builder withLocality(final Locality locality) {
            this.locality = locality;
            return this;
        }

        public Builder withMetadata(final Map<String, String> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        public Builder withNetwork(final Network network) {
            this.networks.add(network);
            return this;
        }

        public Builder withTags(final Map<String, String> tags) {
            this.tags.putAll(tags.entrySet().stream().collect(Collectors.toMap(e -> "tag." + e.getKey(), Map.Entry::getValue)));
            return this;
        }
    }
}
