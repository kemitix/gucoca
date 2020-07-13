package net.kemitix.gucoca.common;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.time.Instant;
import java.util.Random;
import java.util.function.Supplier;

@ApplicationScoped
public class CommonProviders {

    @Produces
    Random random() {
        return new Random();
    }

    @Produces
    ObjectMapper objectMapper() {
        return new ObjectMapper();
    }

    @Produces
    Supplier<Instant> now() {
        return Instant::now;
    }
}
