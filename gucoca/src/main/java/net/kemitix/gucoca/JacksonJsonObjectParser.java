package net.kemitix.gucoca;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class JacksonJsonObjectParser
        implements JsonObjectParser {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public <T> T fromJson(InputStream inputStream, Class<T> aClass) {
        try {
            String json = new String(inputStream.readAllBytes());
            return objectMapper.readValue(json, aClass);
        } catch (IOException e) {
            throw new RuntimeException("Error parsing Json", e);
        }
    }
}
