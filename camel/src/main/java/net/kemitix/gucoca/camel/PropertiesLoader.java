package net.kemitix.gucoca.camel;

import net.kemitix.gucoca.common.spi.PropertyFilenames;
import org.apache.camel.component.properties.PropertiesComponent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Instance;
import javax.enterprise.inject.Produces;
import javax.inject.Inject;
import javax.inject.Named;
import java.io.File;
import java.util.Optional;

@ApplicationScoped
public class PropertiesLoader {

    @Inject
    Instance<PropertyFilenames> propertyFilenames;

    @Produces
    @ApplicationScoped
    @Named("properties")
    PropertiesComponent propertiesComponent() {
        PropertiesComponent component = new PropertiesComponent();

        propertyFilenames
                .stream()
                .map(PropertyFilenames::getName)
                .forEach(name -> {
                    addPropertyLocations(component, name);
                });

        return component;
    }

    private void addPropertyLocations(
            PropertiesComponent component,
            String name
    ) {
        // optional environment: gucoca-twitterstories-${GUCOCA-ENV}.properties
        Optional.ofNullable(System.getenv("GUCOCA_ENV")).ifPresent(env -> {

            String envFileName = String.format(
                    "gucoca-%s-%s.properties", name, env);

            // in current directory
            String localFile = String.format("%s/.config/%s",
                    System.getProperty("user.dir"), envFileName);
            addFileLocation(component, localFile);

            // in $HOME/.config/
            String userFile = String.format("%s/.config/%s",
                    System.getProperty("user.home"), envFileName);
            addFileLocation(component, userFile);
        });

        String fileName = String.format("gucoca-%s.properties", name);

        // in current directory
        String localFile = String.format("%s/.config/%s",
                System.getProperty("user.dir"), fileName);
        addFileLocation(component, localFile);

        // in $HOME/.config/
        String userFile = String.format("%s/.config/%s",
                System.getProperty("user.home"), fileName);
        addFileLocation(component, userFile);

        // default bundled in jar
        component.addLocation("classpath:" + fileName);
    }

    private void addFileLocation(
            PropertiesComponent component,
            String filename
    ) {
        if (new File(filename).exists())
            component.addLocation("file:" + filename);
    }

}
