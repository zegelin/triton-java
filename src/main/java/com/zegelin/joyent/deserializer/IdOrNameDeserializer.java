package com.zegelin.joyent.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.zegelin.joyent.IdOrName;

import java.io.IOException;
import java.util.UUID;

public class IdOrNameDeserializer extends StdDeserializer<IdOrName<?>> {
    public IdOrNameDeserializer() {
        super(IdOrName.class);
    }

    @Override
    public IdOrName<?> deserialize(final JsonParser p, final DeserializationContext ctxt) throws IOException, JsonProcessingException {
        final String raw = p.getText();

        try {
            return () -> UUID.fromString(raw);

        } catch (final IllegalArgumentException e) {
            return () -> raw;
        }
    }
}
