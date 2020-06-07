package net.kemitix.gucoca.app;

import lombok.extern.java.Log;
import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.JobStateData;
import net.kemitix.gucoca.spi.JsonObjectParser;
import net.kemitix.gucoca.utils.ServiceSupplier;

import javax.batch.api.AbstractBatchlet;
import javax.batch.runtime.BatchStatus;
import javax.batch.runtime.context.JobContext;
import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;

@Log
public class LoadConfigFile
        extends AbstractBatchlet {

    String configFile = String.join(File.separator,
            System.getProperty("user.dir"), "gucoca-config.json");

    @Inject JobContext jobContext;
    ServiceSupplier serviceSupplier = ServiceSupplier.create();

    @Override
    public String process() throws Exception {
        log.info("Reading: " + configFile);

        GucocaConfig gucocaConfig =
                serviceSupplier.findOne(JsonObjectParser.class)
                        .fromJson(new FileInputStream(configFile),
                                GucocaConfig.class);

        JobStateData jobStateData = new JobStateData();
        jobStateData.setConfig(gucocaConfig);
        jobContext.setTransientUserData(jobStateData);

        return BatchStatus.COMPLETED.toString();
    }
}
