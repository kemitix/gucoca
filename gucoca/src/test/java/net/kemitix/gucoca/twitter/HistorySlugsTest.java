package net.kemitix.gucoca.twitter;

import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.Condition;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;
import java.util.Map;

class HistorySlugsTest
        implements WithAssertions {

    @Test
    @DisplayName("add items to context")
    public void addsItemsToContext() {
        //given
        StoryContext context = StoryContext.empty();
        String slug1 = "a-slug";
        AttributeValue attributeValue1 = new AttributeValue(slug1);
        String slug2 = "a-slug";
        AttributeValue attributeValue2 = new AttributeValue(slug2);
        List<Map<String, AttributeValue>> items =
                List.of(
                        Map.of("Slug", attributeValue1),
                        Map.of("Slug", attributeValue2)
                        );
        //when
        StoryContext result =
                HistorySlugs.addToStoryContext(context, items);
        //then
        assertThat(result.getHistory())
                .containsExactly(slug1, slug2);
    }

    @Test
    @DisplayName("create a history item")
    public void createsHistoryItem() {
        //given
        var story = new Story();
        story.setUrl("stories/slug-part");
        HistorySlugs historySlugs = new HistorySlugs();
            int epochSecond = 123456789;
        historySlugs.now = () -> Instant.ofEpochSecond(epochSecond);
        //when
        Map<String, AttributeValue> historyItem = historySlugs.createHistoryItem(story);
        //then
        assertThat(historyItem).containsOnlyKeys("Slug", "BroadcastDate");
        assertThat(historyItem.get("Slug").getS())
                .isEqualTo("slug-part");
        assertThat(historyItem.get("BroadcastDate").getN())
                .isEqualTo(Integer.toString(epochSecond));
    }

    @Test
    @DisplayName("create expiry conditions")
    public void createsExpiryConditions() {
        //given
        var expiryDate = 123456789L;
        var historySlugs = new HistorySlugs();
        //when
        Map<String, Condition> result = historySlugs.scanCriteria(expiryDate);
        //then
        assertThat(result).containsOnlyKeys("BroadcastDate");
        Condition condition = result.get("BroadcastDate");
        assertThat(condition.getComparisonOperator()).isEqualTo("GT");
        List<AttributeValue> attributeValues = condition.getAttributeValueList();
        assertThat(attributeValues).hasSize(1);
        assertThat(attributeValues.get(0).getN())
                .isEqualTo(Long.toString(expiryDate));
    }
}