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

    @Inject
    Random random;

    public String startTimer(int startFrequencySeconds) {
        long startPeriodMilliseconds = startFrequencySeconds * 1000;
        return "timer:GuCoca.TwitterStories.Start?period=" + startPeriodMilliseconds;
    }

    public boolean shouldIRun(int percentChangeToPost) {
        int roll = random.nextInt(100);
        log.info("Rolled " + roll + " @ " + Instant.now().toString());
        return roll <= percentChangeToPost;
    }
}
