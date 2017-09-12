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
import org.glassfish.jersey.logging.LoggingFeature;

import javax.crypto.NoSuchPaddingException;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.concurrent.Future;
import java.util.logging.Level;

public class TritonClient implements Triton {
    private final WebTarget apiEndpoint;

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

    public TritonClient(final KeyPair keyPair) {
        final ClientConfig clientConfig = new ClientConfig();
        clientConfig.connectorProvider(new GrizzlyConnectorProvider());
        clientConfig.register(new LoggingFeature(null, Level.INFO, LoggingFeature.Verbosity.PAYLOAD_ANY, 1024 * 1024));

        final Client client = ClientBuilder.newClient(clientConfig);

        apiEndpoint = client.target("https://us-east-1.api.joyent.com/my");

        apiEndpoint.register(ObjectMapperProvider.class);
        apiEndpoint.register(APIVersionFilter.class);
        apiEndpoint.register(RequestDateFilter.class);
        apiEndpoint.register(new AuthenticationFilter(keyPair));
    }

    @Override
    public Future<List<Package>> packages() {
        return apiEndpoint.path("/packages")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(new GenericType<List<Package>>() {});
    }

    @Override
    public Future<Package> getPackage(final IdOrName<Package> packageIdOrName) {
        return apiEndpoint.path("/packages/{id}")
                .resolveTemplate("id", packageIdOrName.idOrName())
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(Package.class);
    }

    @Override
    public Future<List<Image>> images() {
        return apiEndpoint.path("/images")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(new GenericType<List<Image>>() {});
    }

    @Override
    public Future<Image> getImage(final Id<Image> imageId) {
        return apiEndpoint.path("/images/{id}")
                .resolveTemplate("id", imageId.id)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(Image.class);
    }

    @Override
    public Future<List<Machine>> machines() {
        return apiEndpoint.path("/machines")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(new GenericType<List<Machine>>() {});
    }

    @Override
    public CreateMachineRequest.Builder createMachine(final IdOrName<Package> pkg, final IdOrName<Image> image) {
        return new CreateMachineRequest.Builder(this, pkg, image);
    }

    Future<Machine> createMachine(final CreateMachineRequest request) {
        return apiEndpoint.path("/machines")
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .post(Entity.entity(request, MediaType.APPLICATION_JSON_TYPE), Machine.class);
    }

    @Override
    public Future<Machine> machineWithId(final Id<Machine> machineId) {
        return apiEndpoint.path("/machines/{id}")
                .resolveTemplate("id", machineId.id)
                .request(MediaType.APPLICATION_JSON_TYPE)
                .async()
                .get(Machine.class);
    }
}
