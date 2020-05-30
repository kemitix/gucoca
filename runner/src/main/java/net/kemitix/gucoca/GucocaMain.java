package net.kemitix.gucoca;

import io.quarkus.runtime.QuarkusApplication;
import io.quarkus.runtime.annotations.QuarkusMain;
import net.kemitix.gucoca.spi.GucocaService;

import javax.enterprise.inject.Instance;
import javax.inject.Inject;
import java.util.Arrays;

@QuarkusMain
public class GucocaMain
        implements QuarkusApplication {

    @Inject Instance<GucocaService> services;

    @Override
    public int run(String... args) {
        services.forEach(service ->
                service.run(Arrays.asList(args)));
        return 0;
    }

}
