package net.kemitix.gucoca.camel;

import org.apache.camel.Processor;
import org.slf4j.Logger;

interface LoggingUtils {

    static Processor logAllHeaders(Logger log) {
        return exchange ->
                exchange.getIn()
                        .getHeaders()
                        .forEach((header, value) ->
                                log.info("HEADER: {} = {}", header, value));
    }
}
