package net.kemitix.gucoca.camel.email;

import net.kemitix.gucoca.common.ServiceSupplier;
import net.kemitix.gucoca.common.spi.AwsSesConfig;
import net.kemitix.gucoca.common.spi.JsonObjectParser;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

@ApplicationScoped
class LoadConfigFile {

    //  $HOME/.config/gucoca-ses.json
    String configFile = String.join(File.separator,
            System.getProperty("user.home"),
            ".config",
            "gucoca-ses.json");

    ServiceSupplier serviceSupplier = ServiceSupplier.create();

    @Produces
    AwsSesConfig config() throws FileNotFoundException {
        return serviceSupplier.findOne(JsonObjectParser.class)
                .fromJson(new FileInputStream(configFile),
                        AwsSesConfigImpl.class);
    }

}
