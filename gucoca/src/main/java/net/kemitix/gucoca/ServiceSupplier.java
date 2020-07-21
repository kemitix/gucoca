package net.kemitix.gucoca;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ServiceLoader;

/**
 * Wrapper around {@link ServiceLoader}.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ServiceSupplier {

    public <T> T findOne(Class<T> serviceClass) {
        return ServiceLoader.load(serviceClass)
                .findFirst()
                .orElseThrow(() ->
                        new MissingService(serviceClass));
    }

    public static ServiceSupplier create() {
        return new ServiceSupplier();
    }

    public static class MissingService extends RuntimeException {

        public <T> MissingService(Class<T> serviceClass) {
            super("ServiceLoader could not find an implementation for: " +
                    serviceClass.getCanonicalName());
        }
    }
}
