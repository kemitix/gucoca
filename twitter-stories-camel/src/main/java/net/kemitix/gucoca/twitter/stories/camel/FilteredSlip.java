package net.kemitix.gucoca.twitter.stories.camel;

import org.apache.camel.PropertyInject;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FilteredSlip {

    @PropertyInject("{{gucoca.twitterstories.slip.blacklist}}")
    String blacklist;

    String filter(String inSlip) {
        List<String> exclude = List.of(blacklist.split(","));
        return Stream.of(inSlip.split(","))
                .filter(slip -> !exclude.contains(slip))
                .collect(Collectors.joining(","));
    }

}
