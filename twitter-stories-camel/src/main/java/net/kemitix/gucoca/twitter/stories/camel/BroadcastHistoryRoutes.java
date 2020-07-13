package net.kemitix.gucoca.twitter.stories.camel;

import net.kemitix.gucoca.common.spi.AwsDynamoDB;
import net.kemitix.gucoca.twitter.stories.BroadcastHistory;
import net.kemitix.gucoca.twitter.stories.HistorySlugs;
import org.apache.camel.PropertyInject;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.aws.ddb.DdbConstants;

import javax.inject.Inject;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static net.kemitix.gucoca.twitter.stories.HistorySlugs.EXPIRY_DATE;
import static org.apache.camel.builder.Builder.bean;

class BroadcastHistoryRoutes
    extends RouteBuilder
        implements BroadcastHistory {

    @Inject
    AwsDynamoDB awsDynamoDB;
    @Inject
    HistorySlugs historySlugs;

    @PropertyInject("gucoca.twitterstories.norepeatdays")
    long noRepeatDays;
    @PropertyInject("gucoca.twitterstories.historytable")
    String tableName;

    @Override
    public void configure() {
        long expiryDate = Instant.now()
                .minus(noRepeatDays, ChronoUnit.DAYS)
                .getEpochSecond();

        from(BroadcastHistory.LOAD_ENDPOINT)
                .routeId("load-history")
                // load history
                .setHeader(EXPIRY_DATE, () -> expiryDate)
                .setHeader(DdbConstants.SCAN_FILTER,
                        bean(historySlugs, String.format(
                                "scanCriteria(${header.[%s]})", EXPIRY_DATE)))
                .to(awsDynamoDB.scan(tableName))
                .bean(historySlugs, String.format(
                        "addToStoryContext(${body}, ${header.[%s]})", DdbConstants.ITEMS))
                .log("Loaded History: ${body.history.size}")
        ;

        from(UPDATE_ENDPOINT)
                .routeId("add-to-history")
                .bean(method(HistorySlugs.class, "createHistoryItem"))
                .to(awsDynamoDB.put(tableName))
                .log("Finished")
        ;
    }

}
