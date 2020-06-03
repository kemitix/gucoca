package net.kemitix.gucoca;

import net.kemitix.gucoca.spi.GucocaService;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.inject.Inject;
import java.util.Arrays;

@SpringBootApplication
public class GucocaMain implements ApplicationRunner {

    private final GucocaService service;

    @Inject
    public GucocaMain(GucocaService service) {
        this.service = service;
    }

    @Override
    public void run(ApplicationArguments args) {
        service.run(Arrays.asList(args.getSourceArgs()));
    }

    public static void main(String[] args) {
        SpringApplication.run(GucocaMain.class, args);
    }

}
