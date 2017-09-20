package com.zegelin.joyent.serializer;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;
import com.google.common.base.Preconditions;
import com.zegelin.joyent.CreateMachineRequest;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

public class UnwrappingMapSerializer extends JsonSerializer<Map<String, ?>> implements ContextualSerializer {
    private final String prefix;
    private final String suffix;

    public UnwrappingMapSerializer() {
        this.prefix = "";
        this.suffix = "";
    }

    public UnwrappingMapSerializer(final String prefix, final String suffix) {
        this.prefix = Optional.ofNullable(prefix).orElse("");
        this.suffix = Optional.ofNullable(suffix).orElse("");
    }

    @Override
    public void serialize(final Map<String, ?> value, final JsonGenerator gen, final SerializerProvider serializers) throws IOException, JsonProcessingException {
        for (final Map.Entry<String, ?> entry : value.entrySet()) {
            gen.writeFieldName(String.format("%s%s%s", prefix, entry.getKey(), suffix));

            final Object entryValue = entry.getValue();

            final JsonSerializer<Object> valueSerializer = serializers.findValueSerializer(entryValue.getClass());
            valueSerializer.serialize(entryValue, gen, serializers);
        }
    }

    @Override
    public boolean isUnwrappingSerializer() {
        return true;
    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializers, final BeanProperty property) throws JsonMappingException {
        final JsonUnwrapped annotation = property.getAnnotation(JsonUnwrapped.class);
        Preconditions.checkNotNull(annotation,
                "%s for %s requires a %s annotation",
                UnwrappingMapSerializer.class.getCanonicalName(),
                property.getMember(),
                JsonUnwrapped.class.getCanonicalName());

        final String prefix = annotation.prefix();
        final String suffix = annotation.suffix();

        return new UnwrappingMapSerializer(prefix, suffix);
    }
}
