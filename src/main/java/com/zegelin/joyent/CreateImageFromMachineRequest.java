package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.concurrent.Future;

public class CreateImageFromMachineRequest {
    @JsonProperty
    public final Id<Machine> machine;

    @JsonProperty
    public final String name;

    @JsonProperty
    public final String version;

    CreateImageFromMachineRequest(final Builder builder) {
        this.machine = builder.machine;
        this.name = builder.name;
        this.version = builder.version;
    }

    public static class Builder {
        private final TritonClient tritonClient;

        public final Id<Machine> machine;
        public final String name;
        public final String version;

        Builder(final TritonClient tritonClient, final Id<Machine> machine, final String name, final String version) {
            this.tritonClient = tritonClient;
            this.machine = machine;
            this.name = name;
            this.version = version;
        }

        public Future<Image> create() {
            return tritonClient.createImageFromMachine(new CreateImageFromMachineRequest(this));
        }
    }
}
