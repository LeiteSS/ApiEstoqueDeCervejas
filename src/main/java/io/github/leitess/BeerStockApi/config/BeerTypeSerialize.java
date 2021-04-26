package io.github.leitess.BeerStockApi.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import io.github.leitess.BeerStockApi.entity.enums.BeerType;

import java.io.IOException;

public class BeerTypeSerialize extends StdSerializer<BeerType> {

    public BeerTypeSerialize() {
        super(BeerType.class);
    }


    @Override
    public void serialize(BeerType type, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeString(type.getDescription());
    }
}
