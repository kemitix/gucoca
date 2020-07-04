package net.kemitix.gucoca.camel;

import lombok.extern.log4j.Log4j2;
import net.kemitix.gucoca.spi.GucocaConfig;
import org.apache.camel.Processor;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Random;

/**
 * Controls how often the main route triggers and whether it performs any work
 * when it does so.
 */
@Log4j2
class PostingFrequency {

    private static final Random random = new Random();

    @Inject GucocaConfig config;

    String startTimer() {
        long startPeriodMilliseconds = config.getStartFrequencySeconds() * 1000;
        return "timer:start-load-history?period=" + startPeriodMilliseconds;
    }

    Processor shouldIRun() {
        return exchange -> {
            int roll = random.nextInt(100);
            log.info("Rolled " + roll + " @ " + Instant.now().toString());
            if (roll <= config.getPercentChanceToPost()) {
                log.info("Posting a Story!");
            } else {
                exchange.setRouteStop(true);
            }
        };
    }
}
