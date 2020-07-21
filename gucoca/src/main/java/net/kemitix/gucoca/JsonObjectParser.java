package net.kemitix.gucoca;

import java.io.InputStream;

public interface JsonObjectParser {
    <T> T fromJson(InputStream inputStream, Class<T> aClass);
}
