package net.kemitix.gucoca.twitter.stories;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;

import javax.inject.Inject;
import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class HistorySlugs {

    public static final String EXPIRY_DATE = "Gucoca.TwitterStories.ExpiryDate";
    private static final String FIELD_SLUG = "Slug";
    private static final String FIELD_BROADCAST_DATE = "BroadcastDate";

    @Inject
    Supplier<Instant> now;

    static StoryContext addToStoryContext(
            StoryContext storyContext,
            List<Map<String, AttributeValue>> items
    ) {
        List<String> slugs =
                items.stream()
                        .map(map -> map.get(FIELD_SLUG))
                        .map(AttributeValue::getS)
                        .collect(Collectors.toList());
        return storyContext.withHistory(slugs);
    }

    Map<String, AttributeValue> createHistoryItem(Story story) {
        String slug = story.slug();
        Map<String, AttributeValue> item = new HashMap<>();
        AttributeValue broadcastDate = new AttributeValue();
        broadcastDate.setN(Long.toString(now.get().getEpochSecond()));
        item.put("BroadcastDate", broadcastDate);
        item.put(FIELD_SLUG,  new AttributeValue(slug));
        return item;
    }

    Map<String, Condition> scanCriteria(long expiryDate) {
        Condition condition = new Condition()
                .withComparisonOperator(ComparisonOperator.GT)
                .withAttributeValueList(
                        new AttributeValue().withN("" + expiryDate));
        return Map.of(FIELD_BROADCAST_DATE, condition);
    }

}
