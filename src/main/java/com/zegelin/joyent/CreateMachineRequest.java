package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.zegelin.joyent.serializer.UnwrappingMapSerializer;

import java.util.*;
import java.util.concurrent.Future;

@SuppressWarnings({"WeakerAccess", "unused"})
public class CreateMachineRequest {
    public static class Locality {
        public final boolean strict;

        public final Set<Id<Machine>> near;
        public final Set<Id<Machine>> far;

        Locality(final boolean strict, final Iterable<? extends Id<Machine>> near, final Iterable<? extends Id<Machine>> far) {
            this.strict = strict;
            this.near = ImmutableSet.copyOf(near);
            this.far = ImmutableSet.copyOf(far);
        }

        Locality(final Builder builder) {
            this(builder.strict, builder.near.build(), builder.far.build());
        }


        public static Locality nearTo(final Id<Machine> machine) {
            return nearTo(ImmutableList.of(machine));
        }

        public static Locality nearTo(final Id<Machine>... machines) {
            return nearTo(ImmutableList.copyOf(machines));
        }

        public static Locality nearTo(final List<Id<Machine>> machines) {
            return new Locality(false, machines, ImmutableList.of());
        }

        public static Locality strictlyNearTo(final Id<Machine> machine) {
            return strictlyNearTo(ImmutableList.of(machine));
        }

        public static Locality strictlyNearTo(final Id<Machine>... machines) {
            return strictlyNearTo(ImmutableList.copyOf(machines));
        }

        public static Locality strictlyNearTo(final Iterable<? extends Id<Machine>> machines) {
            return new Locality(true, machines, ImmutableList.of());
        }


        public static Locality farFrom(final Id<Machine> machine) {
            return farFrom(ImmutableList.of(machine));
        }

        public static Locality farFrom(final Id<Machine>... machines) {
            return farFrom(ImmutableList.copyOf(machines));
        }

        public static Locality farFrom(final List<? extends Id<Machine>> machines) {
            return new Locality(false, ImmutableList.of(), machines);
        }

        public static Locality strictlyFarFrom(final Id<Machine> machine) {
            return strictlyFarFrom(ImmutableList.of(machine));
        }

        public static Locality strictlyFarFrom(final Id<Machine>... machines) {
            return strictlyFarFrom(ImmutableList.copyOf(machines));
        }

        public static Locality strictlyFarFrom(final Iterable<? extends Id<Machine>> machines) {
            return new Locality(true, ImmutableList.of(), machines);
        }


        public static Builder builder() {
            return new Builder();
        }

        public static class Builder {
            public boolean strict;

            public final ImmutableSet.Builder<Id<Machine>> near = ImmutableSet.builder();
            public final ImmutableSet.Builder<Id<Machine>> far = ImmutableSet.builder();

            Builder() {
            }

            public Builder strict() {
                this.strict = true;
                return this;
            }

            public Builder nearTo(final Id<Machine> machine) {
                near.add(machine);
                return this;
            }

            public Builder nearTo(final Id<Machine>... machines) {
                near.add(machines);
                return this;
            }

            public Builder nearTo(final Iterable<? extends Id<Machine>> machines) {
                near.addAll(machines);
                return this;
            }


            public Builder farFrom(final Id<Machine> machine) {
                far.add(machine);
                return this;
            }

            public Builder farFrom(final Id<Machine>... machines) {
                far.add(machines);
                return this;
            }

            public Builder farFrom(final Iterable<? extends Id<Machine>> machines) {
                far.addAll(machines);
                return this;
            }


            public Locality build() {
                return new Locality(this);
            }
        }
    }

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final String name;

    @JsonProperty("package")
    public final IdOrName<Package> pkg;

    @JsonProperty
    public final IdOrName<Image> image;

    @JsonProperty
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    public final List<Id<Network>> networks;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    public final Locality locality;

    @JsonSerialize(using = UnwrappingMapSerializer.class)
    @JsonUnwrapped(prefix = "metadata.")
    public final Map<String, String> metadata;

    @JsonSerialize(using = UnwrappingMapSerializer.class)
    @JsonUnwrapped(prefix = "tag.")
    public final Map<String, String> tags;

    @JsonProperty
    public final boolean firewallEnabled;

    CreateMachineRequest(final Builder builder) {
        this.name = builder.name;
        this.pkg = builder.pkg;
        this.image = builder.image;
        this.networks = builder.networks.build();
        this.locality = builder.locality;
        this.metadata = builder.metadata.build();
        this.tags = builder.tags.build();
        this.firewallEnabled = builder.firewallEnabled;
    }



    public static class Builder {
        private final TritonClient tritonClient;

        public final IdOrName<Package> pkg;
        public final IdOrName<Image> image;

        public final ImmutableList.Builder<Id<Network>> networks = ImmutableList.builder();

        public final ImmutableMap.Builder<String, String> metadata = ImmutableMap.builder();
        public final ImmutableMap.Builder<String, String> tags = ImmutableMap.builder();

        public String name;
        public Locality locality;

        public boolean firewallEnabled = false;

        Builder(final TritonClient tritonClient, final IdOrName<Package> pkg, final IdOrName<Image> image) {
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


        public Builder withNetwork(final Id<Network> networkId) {
            this.networks.add(networkId);
            return this;
        }

        public Builder withNetworks(final Id<Network>... networkId) {
            this.networks.add(networkId);
            return this;
        }

        public Builder withNetworks(final Iterable<? extends Id<Network>> networkIds) {
            this.networks.addAll(networkIds);
            return this;
        }

        public Builder withNetworks(final Iterator<? extends Id<Network>> networkIds) {
            this.networks.addAll(networkIds);
            return this;
        }


        public Builder withMetadata(final Map<String, String> metadata) {
            this.metadata.putAll(metadata);
            return this;
        }

        public Builder withMetadatum(final Map.Entry<String, String> datum) {
            this.metadata.put(datum);
            return this;
        }

        public Builder withMetadatum(final String key, final String value) {
            this.metadata.put(key, value);
            return this;
        }

        public Builder withTags(final Map<String, String> tags) {
            this.tags.putAll(tags);
            return this;
        }

        public Builder withTag(final Map.Entry<String, String> tag) {
            this.tags.put(tag);
            return this;
        }

        public Builder withTag(final String key, final String value) {
            this.tags.put(key, value);
            return this;
        }

        public Builder withFirewallEnabled() {
            this.firewallEnabled = true;
            return this;
        }

        public Builder withFirewallDisabled() {
            this.firewallEnabled = false;
            return this;
        }
    }
}
