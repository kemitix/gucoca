package net.kemitix.gucoca.twitter;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

class StoryContextTest
        implements WithAssertions {

    @Test
    @DisplayName("get stories from issues")
    public void getStoriesFromIssues() {
        //given
        var story1 = new Story();
        var story2 = new Story();
        var story3 = new Story();
        var story4 = new Story();
        var issue1 = new Issue();
        var issue2 = new Issue();
        issue1.setStories(List.of(story1, story2));
        issue2.setStories(List.of(story3, story4));
        var storyContext =
                new StoryContext(List.of(issue1, issue2), Collections.emptyList());
        //when
        var stories = storyContext.stories();
        //then
        assertThat(stories)
                .containsExactlyInAnyOrder(
                        story1, story2, story3, story4
                );
    }

    @Test
    @DisplayName("verify with syntax")
    public void withSyntax() {
        var issue1 = new Issue();
        var issue2 = new Issue();
        var context = StoryContext.empty()
                .withIssues(List.of(issue1, issue2))
                .withHistory(List.of("last-story-slug"));
        //then
        assertThat(context.getIssues()).containsExactly(issue1, issue2);
        assertThat(context.getHistory()).containsExactly("last-story-slug");
    }
}