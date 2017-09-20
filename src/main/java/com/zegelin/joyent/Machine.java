package com.zegelin.joyent;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.base.Enums;

import javax.ws.rs.client.Entity;
import javax.ws.rs.core.Form;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class Machine extends Id<Machine> {
    public final String name;
    public final String type;
    public final String brand;
    public final State state;
    public final Id<Image> image;

    public final int memory;
    public final int disk;

    public final Map<String, String> metadata;
    public final Map<String, String> tags;

    public final List<InetAddress> ips;
    public final InetAddress primaryIp;
    public final boolean firewallEnabled;
    public final Id<ComputeNode> computeNode;
    public final IdOrName<Package> pkg;

    static final class Action {
        static Entity<Form> start() {
            return entity("start");
        }

        static Entity<Form> stop() {
            return entity("stop");
        }

        static Entity<Form> reboot() {
            return entity("reboot");
        }

        static Entity<Form> resize(final IdOrName<Package> pkg) {
            return Entity.form(form("resize").param("package", pkg.idOrName().toString()));
        }

        static Entity<Form> rename(final String name) {
            return Entity.form(form("rename").param("name", name));
        }

        static Entity<Form> enableFirewall() {
            return entity("enable_firewall");
        }

        static Entity<Form> disableFirewall() {
            return entity("disable_firewall");
        }


        private static Entity<Form> entity(final String action) {
            return Entity.form(form(action));
        }

        private static Form form(final String action) {
            return new Form("action", action);
        }

        private Action() {}
    }

    enum State {
        PROVISIONING,
        RUNNING,
        STOPPING,
        STOPPED,
        DELETED,
        FAILED,
        UNKNOWN;

        @JsonCreator
        static State forJsonString(final String state) {
            return Enums.getIfPresent(State.class, state.toUpperCase()).or(UNKNOWN);
        }
    }

    public static Id<Machine> withId(final UUID id) {
        return new Id<>(id);
    }

    public static Id<Machine> withId(final String id) {
        return new Id<>(UUID.fromString(id));
    }

    Machine(@JsonProperty("id") final UUID id,
            @JsonProperty("name") final String name,
            @JsonProperty("type") final String type,
            @JsonProperty("brand") final String brand,
            @JsonProperty("state") final State state,
            @JsonProperty("image") final Id<Image> image,
            @JsonProperty("memory") final int memory,
            @JsonProperty("disk") final int disk,
            @JsonProperty("metadata") final Map<String, String> metadata,
            @JsonProperty("tags") final Map<String, String> tags,
//            @JsonProperty("created") final Instant created,
//            @JsonProperty("updated") final Instant updated,
            @JsonProperty("docker") final boolean docker,
            @JsonProperty("ips") final List<InetAddress> ips,
//            @JsonProperty("networks") final Id<Network> networks,
            @JsonProperty("primaryIp") final InetAddress primaryIp,
            @JsonProperty("firewall_enabled") final boolean firewallEnabled,
            @JsonProperty("compute_node") final Id<ComputeNode> computeNode,
            @JsonProperty("package") final IdOrName<Package> pkg,
            @JsonProperty("dns_named") final List<String> dnsNames) {
        super(id);
        this.name = name;
        this.type = type;
        this.brand = brand;
        this.state = state;
        this.image = image;
        this.memory = memory;
        this.disk = disk;
        this.metadata = metadata;
        this.tags = tags;
        this.ips = ips;
        this.primaryIp = primaryIp;
        this.firewallEnabled = firewallEnabled;
        this.computeNode = computeNode;
        this.pkg = pkg;
    }
}
