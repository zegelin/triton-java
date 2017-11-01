package com.zegelin.joyent;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.zegelin.joyent.deserializer.IdOrNameDeserializer;
import com.zegelin.joyent.filter.APIVersionFilter;
import com.zegelin.joyent.filter.AuthenticationFilter;
import com.zegelin.joyent.filter.RequestDateFilter;
import com.zegelin.joyent.serializer.IdOrNameSerializer;
import org.glassfish.jersey.client.ClientConfig;
import org.glassfish.jersey.grizzly.connector.GrizzlyConnectorProvider;
import org.glassfish.jersey.internal.inject.AbstractBinder;
import org.glassfish.jersey.logging.LoggingFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import java.security.KeyPair;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class TritonClient implements Triton {
    private final WebTarget accountTarget;
    private final WebTarget packagesTarget;
    private final WebTarget imagesTarget;
    private final WebTarget machinesTarget;
    private final WebTarget keysTarget;
    private final WebTarget usersTarget;
    private final WebTarget rolesTarget;
    private final WebTarget policiesTarget;
    private final WebTarget configTarget;
    private final WebTarget datacentersTarget;
    private final WebTarget servicesTarget;
    private final WebTarget analyticsTarget;
    private final WebTarget fwRulesTarget;
    private final WebTarget networksTarget;

    static class ObjectMapperProvider implements ContextResolver<ObjectMapper> {
        final ObjectMapper objectMapper;

        public ObjectMapperProvider() {
            objectMapper = new ObjectMapper();

            objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
            objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.SNAKE_CASE);

            objectMapper.registerModule(new SimpleModule() {{
                addSerializer(new IdOrNameSerializer());
                addDeserializer(IdOrName.class, new IdOrNameDeserializer());
            }});
        }

        @Override
        public ObjectMapper getContext(final Class<?> type) {
            return objectMapper;
        }
    }

    public TritonClient(final Endpoint endpoint, final Login<Account> accountLogin, final KeyPair keyPair) {
        final ClientConfig clientConfig = new ClientConfig(); // TODO: expose this to clients, so they can select the HTTP connector, etc
        clientConfig.connectorProvider(new GrizzlyConnectorProvider());
        clientConfig.register(new LoggingFeature(null, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, 1024 * 1024));

        final Client client = ClientBuilder.newClient(clientConfig);

        client.register(new AbstractBinder() {
            @Override
            protected void configure() {
                bind(keyPair).to(KeyPair.class);
                bind(accountLogin).to(new GenericType<Login<Account>>() {});
            }
        });

        client.register(ObjectMapperProvider.class);
        client.register(APIVersionFilter.class);
        client.register(RequestDateFilter.class);
        client.register(AuthenticationFilter.class);

        final WebTarget apiRootTarget = client.target(endpoint.uri).path(accountLogin.username);

        accountTarget = apiRootTarget;
        packagesTarget = apiRootTarget.path("/packages");
        imagesTarget = apiRootTarget.path("/images");
        machinesTarget = apiRootTarget.path("/machines");
        keysTarget = apiRootTarget.path("/keys");
        usersTarget = apiRootTarget.path("/users");
        rolesTarget = apiRootTarget.path("/roles");
        policiesTarget = apiRootTarget.path("/policies");
        configTarget = apiRootTarget.path("/config");
        datacentersTarget = apiRootTarget.path("/datacenters");
        servicesTarget = apiRootTarget.path("/services");
        analyticsTarget = apiRootTarget.path("/analytics");
        fwRulesTarget = apiRootTarget.path("/fwrules");
//        fabricsTarget = apiRootTarget.path("/fabrics/default/");
        networksTarget = apiRootTarget.path("/networks");
    }

    private WebTarget packageTarget(final IdOrName<Package> packageIdOrName) {
        return packagesTarget.path("/{id}")
                .resolveTemplate("id", packageIdOrName.idOrName());
    }

    private WebTarget imageTarget(final Id<Image> imageId) {
        return imagesTarget.path("/{id}")
                .resolveTemplate("id", imageId.id);
    }

    private WebTarget machineTarget(final Id<Machine> machineId) {
        return machinesTarget.path("/{id}")
                .resolveTemplate("id", machineId.id);
    }


    @Override
    public Future<Account> account() {
        return accountTarget
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(Account.class);
    }

    @Override
    public Future<List<Package>> packages() {
        return packagesTarget
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(new GenericType<List<Package>>() {});
    }

    @Override
    public Future<Package> packageWithIdOrName(final IdOrName<Package> packageIdOrName) {
        return packageTarget(packageIdOrName)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(Package.class);
    }


    @Override
    public Future<List<Image>> images() {
        return imagesTarget
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(new GenericType<List<Image>>() {});
    }

    @Override
    public Future<Image> imageForId(final Id<Image> imageId) {
        return imageTarget(imageId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(Image.class);
    }

    @Override
    public CreateImageFromMachineRequest.Builder createImageFromMachine(final Id<Machine> machine, final String name, final String version) {
        return new CreateImageFromMachineRequest.Builder(this, machine, name, version);
    }

    Future<Image> createImageFromMachine(final CreateImageFromMachineRequest request) {
        return imagesTarget
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .post(Entity.json(request), Image.class);
    }



    @Override
    public Future<List<Machine>> machines() {
        return machinesTarget
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(new GenericType<List<Machine>>() {});
    }


    @Override
    public Future<Machine> machineWithId(final Id<Machine> machineId) {
        return machineTarget(machineId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(Machine.class);
    }

    @Override
    public CreateMachineRequest.Builder createMachine(final IdOrName<Package> pkg, final IdOrName<Image> image) {
        return new CreateMachineRequest.Builder(this, pkg, image);
    }

    Future<Machine> createMachine(final CreateMachineRequest request) {
        return machinesTarget
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .post(Entity.json(request), Machine.class);
    }

    @Override
    public Future<Void> deleteMachine(final Id<Machine> machineId) {
        return machineTarget(machineId)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .delete(Void.class);
    }

    @Override
    public Future<Void> stopMachine(final Id<Machine> machineId) {
        return machineTarget(machineId)
                .request()
                .async()
                .post(Machine.Action.stop(), Void.class);
    }

    @Override
    public Future<Void> startMachine(final Id<Machine> machineId) {
        return machineTarget(machineId)
                .request()
                .async()
                .post(Machine.Action.start(), Void.class);
    }

    @Override
    public Future<Void> rebootMachine(final Id<Machine> machineId) {
        return machineTarget(machineId)
                .request()
                .async()
                .post(Machine.Action.reboot(), Void.class);
    }

    @Override
    public Future<Void> resizeMachine(final Id<Machine> machineId, final IdOrName<Package> newPackage) {
        return machineTarget(machineId)
                .request()
                .async()
                .post(Machine.Action.resize(newPackage), Void.class);
    }

    @Override
    public Future<Void> renameMachine(final Id<Machine> machineId, final String newName) {
        return machineTarget(machineId)
                .request()
                .async()
                .post(Machine.Action.rename(newName), Void.class);
    }

    @Override
    public Future<Void> replaceMachineTags(final Id<Machine> machineId, final Map<String, String> tags) {
        return machineTarget(machineId)
                .path("/tags")
                .request()
                .async()
                .put(Entity.json(tags), Void.class);
    }

    @Override
    public Future<List<Network>> networks() {
        return networksTarget
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(new GenericType<List<Network>>() {});
    }
}
