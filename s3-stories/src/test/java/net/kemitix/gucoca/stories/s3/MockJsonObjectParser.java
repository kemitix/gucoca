package net.kemitix.gucoca.stories.s3;

import net.kemitix.gucoca.spi.JsonObjectParser;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class MockJsonObjectParser
        implements JsonObjectParser {

    static Map<InputStream, Object> fromJson = new HashMap<>();

    @Override
    public <T> T fromJson(InputStream inputStream, Class<T> aClass) {
        return aClass.cast(fromJson.get(inputStream));
    }
}
