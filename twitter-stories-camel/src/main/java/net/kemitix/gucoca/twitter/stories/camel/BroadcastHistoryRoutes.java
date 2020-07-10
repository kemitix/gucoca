package net.kemitix.gucoca.twitter.stories.camel;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import net.kemitix.gucoca.common.spi.AwsDynamoDB;
import net.kemitix.gucoca.twitter.stories.BroadcastHistory;
import net.kemitix.gucoca.twitter.stories.GucocaConfig;
import net.kemitix.gucoca.twitter.stories.StoryContext;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.ddb.DdbConstants;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class BroadcastHistoryRoutes
    extends RouteBuilder
        implements BroadcastHistory {

    private static final String FIELD_SLUG = "Slug";
    private static final String FIELD_BROADCAST_DATE = "BroadcastDate";

    @Inject GucocaConfig config;
    @Inject AwsDynamoDB awsDynamoDB;

    @Override
    public void configure() {
        long expiryDate = Instant.now()
                .minus(config.getNoRepeatDays(), ChronoUnit.DAYS)
                .getEpochSecond();

        from(BroadcastHistory.LOAD_ENDPOINT)
                .routeId("load-history")
                // load history
                .process(setCriteria(expiryDate))
                .to(awsDynamoDB.scan())
                .process(putSlugsInContext())
                .log("Loaded History: ${body.history.size}")
        ;

        from(UPDATE_ENDPOINT)
                .routeId("add-to-history")
                .process(setStorySlug())
                .to(awsDynamoDB.put())
                .log("Finished")
        ;
    }

    private Processor setStorySlug() {
        return exchange -> {
            Message in = exchange.getIn();
            Map<String, AttributeValue> item = new HashMap<>();

            AttributeValue broadcastDate = new AttributeValue();
            broadcastDate.setN(Long.toString(Instant.now().getEpochSecond()));
            item.put(FIELD_BROADCAST_DATE, broadcastDate);

            String slug = in.getBody(String.class);
            item.put(FIELD_SLUG,  new AttributeValue(slug));

            in.setHeader(DdbConstants.ITEM, item);
        };
    }

    private Processor setCriteria(long expiryDate) {
        return exchange -> {
            Message in = exchange.getIn();
            Condition condition = new Condition()
                    .withComparisonOperator(ComparisonOperator.GT)
                    .withAttributeValueList(
                            new AttributeValue().withN("" + expiryDate));
            Map<String, Condition> conditions = new HashMap<>();
            conditions.put(FIELD_BROADCAST_DATE, condition);
            in.setHeader(DdbConstants.SCAN_FILTER, conditions);
        };
    }

    @SuppressWarnings("unchecked")
    private Processor putSlugsInContext() {
        return exchange -> {
            Message in = exchange.getIn();
            List<String> slugs =
                    ((List<Map<String, AttributeValue>>)
                            in.getHeader(DdbConstants.ITEMS, List.class))
                            .stream()
                            .map(map ->
                                    map.get(FIELD_SLUG)
                                            .getS())
                            .collect(Collectors.toList());
            in.setBody(
                    in.getBody(StoryContext.class)
                            .withHistory(slugs));
        };
    }
}
