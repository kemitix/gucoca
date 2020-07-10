package net.kemitix.gucoca.common.spi;

import java.io.InputStream;

public interface JsonObjectParser {
    <T> T fromJson(InputStream inputStream, Class<T> aClass);
}
