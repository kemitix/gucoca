package net.kemitix.gucoca.app;

import lombok.extern.java.Log;
import net.kemitix.gucoca.spi.GucocaService;

import javax.enterprise.context.ApplicationScoped;
import java.util.List;

@Log
@ApplicationScoped
public class App
        implements GucocaService {
    public void run(List<String> args) {
        log.info("running...");
    }
}
