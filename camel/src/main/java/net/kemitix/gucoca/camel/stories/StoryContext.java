package net.kemitix.gucoca.camel.stories;

import lombok.Builder;
import lombok.Getter;
import lombok.With;
import net.kemitix.gucoca.spi.Issue;
import net.kemitix.gucoca.spi.Story;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@With
@Getter
@Builder
public class StoryContext {

    @Builder.Default
    private final List<Issue> issues = Collections.emptyList();
    @Builder.Default
    private final List<String> history = Collections.emptyList();

    List<Story> stories() {
        return issues.stream()
                .map(Issue::getStories)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
