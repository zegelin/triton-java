package com.zegelin.joyent.serializer;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import com.zegelin.joyent.IdOrName;

import java.io.IOException;

public class IdOrNameSerializer extends StdSerializer<IdOrName<?>> {

    public IdOrNameSerializer() {
        super(IdOrName.class, false);
    }

    @Override
    public void serialize(final IdOrName<?> idOrName, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
        jsonGenerator.writeObject(idOrName.idOrName());
    }
}
