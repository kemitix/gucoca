package net.kemitix.gucoca.twitter.stories;

import net.kemitix.gucoca.common.spi.JsonObjectParser;
import net.kemitix.gucoca.common.ServiceSupplier;

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
