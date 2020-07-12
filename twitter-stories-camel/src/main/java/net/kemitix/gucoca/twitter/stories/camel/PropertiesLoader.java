package net.kemitix.gucoca.twitter.stories.camel;

import org.apache.camel.component.properties.PropertiesComponent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Produces;
import javax.inject.Named;
import java.io.File;
import java.util.Optional;

@ApplicationScoped
public class PropertiesLoader {

    @Produces
    @ApplicationScoped
    @Named("properties")
    PropertiesComponent propertiesComponent() {
        PropertiesComponent component = new PropertiesComponent();

        // optional environment: gucoca-twitterstories-${GUCOCA-ENV}.properties
        Optional.ofNullable(System.getenv("GUCOCA_ENV")).ifPresent(env -> {
            // in current directory
            String envFileName = String.format("gucoca-twitterstories-%s.properties", env);
            if (new File(envFileName).exists())
                component.addLocation("file:" + envFileName);

            // in $HOME/.config/
            String userFile = String.format("%s/.config/%s",
                    System.getProperty("user.home"), envFileName);
            if (new File(userFile).exists())
                component.addLocation("file:" + userFile);
        });

        // in current directory
        String fileName = "gucoca-twitterstories.properties";
        if (new File(fileName).exists())
            component.addLocation("file:" + fileName);

        // in $HOME/.config/
        String userFile = String.format("%s/.config/%s",
                System.getProperty("user.home"), fileName);
        if (new File(userFile).exists())
            component.addLocation("file:" + userFile);

        // default bundled in jar
        component.addLocation("classpath:" + fileName);

        return component;
    }

}
