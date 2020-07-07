package net.kemitix.gucoca.camel.stories;

import net.kemitix.gucoca.camel.AggregationUtils;
import net.kemitix.gucoca.camel.aws.AwsS3;
import net.kemitix.gucoca.camel.email.SendEmail;
import net.kemitix.gucoca.camel.twitter.TwitterStoryPublisher;
import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.processor.aggregate.DefaultAggregateController;

import javax.inject.Inject;

class StoriesRoutes
        extends RouteBuilder
        implements Stories {

    @Inject GucocaConfig config;
    @Inject AwsS3 awsS3;
    @Inject AwsS3Stories awsS3Stories;
    @Inject HistoryFilter historyFilter;
    @Inject StoryAggregator storyAggregator;
    @Inject StorySelector storySelector;
    @Inject StoryCardProcessors storyCards;
    @Inject SendEmail sendEmail;

    @Override
    public void configure() throws Exception {
        long completionTimeoutMillis = 1000L;

        from(LOAD_STORIES)
                .routeId("load-stories")
                // load list of stories
                .to(awsS3.listObjects(config.getS3BucketPrefix()))
                .process(awsS3Stories.extractObjectSummaries())
                .log("Found ${body.size} objects within prefix: /" +
                        config.getS3BucketPrefix())
                .split(body())
                .filter(awsS3Stories.toFilename())

                .process(awsS3Stories.setHeaderForKeyToDownload())
                .to(awsS3.getObjects())
                .process(awsS3Stories.setBodyToStory())

                // discard stories in recent history
                .filter(historyFilter.dropStoriesInHistory())
                // collect publishable stories
                .aggregate(AggregationUtils.timestampedAggregatorId(),
                        AggregationUtils.listFromBodies(Story.class))
                .aggregateController(new DefaultAggregateController())
                .completionTimeout(completionTimeoutMillis)
                .process(storyAggregator.putStoriesInHeader())
                .log("Downloaded ${header.[" + Stories.PUBLISHABLE_COUNT + "]} stories")
                .to(SELECT_STORY);

        from(SELECT_STORY)
                .routeId("select-story")
                // select story
                .process(storySelector.selectAStory())
                // get story card
                .process(storyCards.setS3GetObjectKeyHeader())
                .to(awsS3.getObjects())
                .process(storyCards.addToStory())
                .log("Story selected ${header.[" + TwitterStoryPublisher.STORY + "].slug}")
                .process(prepareNotificationEmail())
                .to(sendEmail.send("story-selector"))
                .delay(config.getTwitterDelayMillis())
                .to(TwitterStoryPublisher.PUBLISH);
    }

    private Processor prepareNotificationEmail() {
        return exchange -> {
            Message in = exchange.getIn();
            Story story = in.getBody(Story.class);
            sendEmail
                    .message(in)
                    .from(config.getEmailSender())
                    .to(config.getNotificationRecipient())
                    .subject(String.format("Story Selected: %s by %s",
                            story.getTitle(),
                            story.getAuthor()
                    ))
                    .html()
                    .body(String.format("<h1>Selected: %s by %s</h1>" +
                                    "This story has been selected to be promoted " +
                                    "on Twitter.",
                            story.getTitle(), story.getAuthor()));
        };
    }
}
