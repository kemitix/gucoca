package net.kemitix.gucoca.camel.stories;

import lombok.Builder;
import lombok.Getter;
import lombok.With;
import net.kemitix.gucoca.spi.Story;

import java.util.Collections;
import java.util.List;

@With
@Getter
@Builder
public class StoryContext {

    @Builder.Default
    private final List<Story> stories = Collections.emptyList();
    @Builder.Default
    private final List<String> history = Collections.emptyList();

}
