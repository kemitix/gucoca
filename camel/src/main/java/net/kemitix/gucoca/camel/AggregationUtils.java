package net.kemitix.gucoca.camel;

import org.apache.camel.Exchange;
import org.apache.camel.builder.ValueBuilder;
import org.apache.camel.processor.aggregate.AbstractListAggregationStrategy;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.apache.camel.builder.Builder.constant;

public interface AggregationUtils {

    static <T> AbstractListAggregationStrategy<T> listFromBodies(
            Class<T> aClass
    ) {
        return new AbstractListAggregationStrategy<T>() {
            @Override
            public T getValue(Exchange exchange) {
                return exchange.getIn().getBody(aClass);
            }

            @Override
            public void timeout(Exchange exchange, int index, int total, long timeout) {
                // suppress warning
            }
        };
    }

    static ValueBuilder timestampedAggregatorId() {
        return constant(
                Instant.now()
                        .truncatedTo(ChronoUnit.HOURS));
    }

}
