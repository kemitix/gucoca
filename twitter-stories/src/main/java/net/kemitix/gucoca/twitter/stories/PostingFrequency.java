package net.kemitix.gucoca.twitter.stories;

import lombok.extern.log4j.Log4j2;

import javax.inject.Inject;
import java.time.Instant;
import java.util.Random;

/**
 * Controls how often the main route triggers and whether it performs any work
 * when it does so.
 */
@Log4j2
public class PostingFrequency {

    private static final Random random = new Random();

//    @Inject TwitterStoriesConfig config;

    public String startTimer(TwitterStoriesConfig config) {
        long startPeriodMilliseconds = config.getStartFrequencySeconds() * 1000;
        return "timer:start-load-history?period=" + startPeriodMilliseconds;
    }

    public boolean shouldIRun(TwitterStoriesConfig config) {
        int roll = random.nextInt(100);
        log.info("Rolled {} @ {}", roll, Instant.now().toString());
        log.info("Config: {}", config);
        int percentChanceToPost = config.getPercentChanceToPost();
        log.info("Target: {}", percentChanceToPost);
        return roll <= percentChanceToPost;
    }
}
