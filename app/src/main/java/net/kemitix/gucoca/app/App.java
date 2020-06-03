package net.kemitix.gucoca.app;

import net.kemitix.gucoca.spi.GucocaService;

import javax.batch.operations.JobOperator;
import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;
import java.util.Properties;
import java.util.function.Supplier;

@Named
public class App implements GucocaService {

    @Inject Supplier<JobOperator> jobOperator;

    public void run(List<String> args) {
        jobOperator.get()
                .start("gucoca", new Properties());
    }

}
