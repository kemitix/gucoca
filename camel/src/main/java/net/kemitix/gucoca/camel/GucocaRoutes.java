package net.kemitix.gucoca.camel;

import net.kemitix.gucoca.camel.twitter.TwitterStoryPublisher;
import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.DefaultAggregateController;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class GucocaRoutes extends RouteBuilder {

    public static final String DIRECT_GUCOCA_HISTORY_ADD = "direct:Gucoca.History.Add";
    @Inject GucocaConfig config;
    @Inject AwsOperations awsOperations;
    @Inject PostingFrequency postingFrequency;
    @Inject StoryCardProcessors storyCards;
    @Inject BroadcastHistory broadcastHistory;
    @Inject AwsS3Stories awsS3Stories;
    @Inject HistoryFilter historyFilter;
    @Inject StoryAggregator storyAggregator;
    @Inject StorySelector storySelector;

    @Override
    public void configure() {
        long expiryDate = Instant.now()
                .minus(config.getNoRepeatDays(), ChronoUnit.DAYS)
                .getEpochSecond();

        from(postingFrequency.startTimer())
                .routeId("main")

                .process(postingFrequency.shouldIRun())

                // load history
                .process(broadcastHistory.setCriteria(expiryDate))
                .to(awsOperations.awsDDBScan())
                .log("Loaded ${header.CamelAwsDdbCount} history items")
                .process(broadcastHistory.putSlugsInHeader())

                // load list of stories
                .to(awsOperations.awsS3ListObjects())
                .process(awsS3Stories.extractObjectSummaries())
                .log("Found ${body.size} objects")
                .split(body())
                .filter(awsS3Stories.toPrefix())
                .filter(awsS3Stories.toFilename())
                .process(awsS3Stories.setHeaderForKeyToDownload())
                .to(awsOperations.awsS3GetObjects())
                .process(awsS3Stories.setBodyToStory())

                // discard stories in recent history
                .filter(historyFilter.dropStoriesInHistory())

                // collect publishable stories
                .aggregate(AggregationUtils.timestampedAggregatorId(),
                        AggregationUtils.listFromBodies(Story.class))
                .aggregateController(new DefaultAggregateController())
                .completionTimeout(storyAggregator.getCompletionTimeoutMillis())
                .process(storyAggregator.putStoriesInHeader())
                .log("Downloaded ${header.[Gucoca.Stories.Count]} stories")

                // select story
                .process(storySelector.selectAStory())

                // get story card
                .process(storyCards.setS3GetObjectKeyHeader())
                .to(awsOperations.awsS3GetObjects())
                .process(storyCards.setS3ObjectInputStreamHeader())
                .log("Story selected ${header[Gucoca.Story.Selected].slug}")

                .to(TwitterStoryPublisher.ENDPOINT);

        from(DIRECT_GUCOCA_HISTORY_ADD)
                .process(broadcastHistory.setStorySlug())
                .to(awsOperations.awsDDBPut())
                .log("Finished");

    }

}
