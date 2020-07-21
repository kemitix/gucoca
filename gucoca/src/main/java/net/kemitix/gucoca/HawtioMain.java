package net.kemitix.gucoca;

import io.hawt.embedded.Main;
import lombok.extern.log4j.Log4j2;
import org.apache.camel.spi.CamelEvent;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

@Log4j2
@ApplicationScoped
public class HawtioMain {

    void onContextStarted(@Observes CamelEvent.CamelContextStartedEvent event) throws Exception {
        // Called before the default Camel context is about to start
        log.info("init()");
        Main main = new Main();
        main.setWar("hawtio.war");
        System.setProperty("hawtio.authenticationEnabled", "false");
        main.run();
        log.info("running");
    }

}
