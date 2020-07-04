package net.kemitix.gucoca.camel;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import net.kemitix.gucoca.spi.Story;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.camel.component.aws.ddb.DdbConstants;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

class BroadcastHistory {

    Processor setStorySlug() {
        return exchange -> {
            Message in = exchange.getIn();
            Map<String, AttributeValue> item = new HashMap<>();

            AttributeValue broadcastDate = new AttributeValue();
            broadcastDate.setN(Long.toString(Instant.now().getEpochSecond()));
            item.put("broadcast-date", broadcastDate);

            Story story = in.getHeader(Headers.STORY_SELECTED, Story.class);
            String slug = story.slug();
            item.put("slug",  new AttributeValue(slug));

            in.setHeader(DdbConstants.ITEM, item);
        };
    }

    Processor setCriteria(long expiryDate) {
        return exchange -> {
            Message in = exchange.getIn();
            Condition condition = new Condition()
                    .withComparisonOperator(ComparisonOperator.GT)
                    .withAttributeValueList(
                            new AttributeValue().withN("" + expiryDate));
            Map<String, Condition> conditions = new HashMap<>();
            conditions.put("broadcast-date", condition);
            in.setHeader(DdbConstants.SCAN_FILTER, conditions);
        };
    }

    @SuppressWarnings("unchecked")
    Processor putSlugsInHeader() {
        return exchange -> {
            Message in = exchange.getIn();
            List<String> slugs =
                    ((List<Map<String, AttributeValue>>)
                            in.getHeader(DdbConstants.ITEMS, List.class))
                            .stream()
                            .map(map ->
                                    map.get("slug")
                                            .getS())
                            .collect(Collectors.toList());
            in.setHeader(Headers.BROADCAST_LIST, slugs);
        };
    }

}
