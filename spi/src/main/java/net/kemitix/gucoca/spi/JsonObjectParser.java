package net.kemitix.gucoca.spi;

import java.io.InputStream;

public interface JsonObjectParser {
    <T> T fromJson(InputStream inputStream, Class<T> aClass);
}
