package net.kemitix.gucoca.camel;

import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.JsonObjectParser;
import net.kemitix.gucoca.utils.ServiceSupplier;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@ApplicationScoped
class LoadConfigFile {

    //  $HOME/.config/gucoca-config.json
    String configFile = String.join(File.separator,
            System.getProperty("user.home"),
            ".config",
            "gucoca-config.json");

    ServiceSupplier serviceSupplier = ServiceSupplier.create();

    @Produces
    GucocaConfig gucocaConfig() throws FileNotFoundException {
        return serviceSupplier.findOne(JsonObjectParser.class)
                .fromJson(new FileInputStream(configFile),
                        GucocaConfig.class);
    }

}
