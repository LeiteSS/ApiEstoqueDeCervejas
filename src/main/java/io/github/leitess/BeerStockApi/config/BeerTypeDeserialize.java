package io.github.leitess.BeerStockApi.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import io.github.leitess.BeerStockApi.entity.enums.BeerType;

import java.io.IOException;

public class BeerTypeDeserialize extends StdDeserializer<BeerType> {

    public BeerTypeDeserialize() {
        super(BeerType.class);
    }

    @Override
    public BeerType deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return BeerType.of(p.getText());
    }


}
