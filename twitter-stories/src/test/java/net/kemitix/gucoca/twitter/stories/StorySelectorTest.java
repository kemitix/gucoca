package net.kemitix.gucoca.twitter.stories;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class StorySelectorTest
        implements WithAssertions {

    @Mock
    Random random;

    @InjectMocks
    StorySelector storySelector;

    @Test
    @DisplayName("select()")
    public void select() {
        //given
        Issue issue = new Issue();
        List<Story> stories = IntStream.range(1, 100)
                .mapToObj(i -> {
                    var story = new Story();
                    story.setTitle(Integer.toString(i));
                    story.setUrl("stories/" + i);
                    story.setPublished("2020-01-01");
                    story.setBlurb(List.of("blurb"));
                    return story;
                }).collect(Collectors.toList());
        issue.setStories(stories);
        var storyContext = StoryContext.empty()
                .withIssues(List.of(issue));

        // drop three stories
        stories.get(10).setBlurb(List.of()); // filtered
        stories.get(20).setPublished(""); // filtered
        StoryContext contextWithHistory = storyContext
                .withHistory(List.of(stories.get(30).slug()));
        int dropped = 3;

        int selectedIndex = 42;
        given(random.nextInt(anyInt()))
                .willReturn(selectedIndex);
        //when
        Story result = storySelector.select(contextWithHistory);
        //then
        assertThat(result).isSameAs(stories.get(selectedIndex + dropped));
    }
}