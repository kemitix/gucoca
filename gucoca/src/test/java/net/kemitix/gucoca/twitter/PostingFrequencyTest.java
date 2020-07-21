package net.kemitix.gucoca.twitter;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class PostingFrequencyTest
        implements WithAssertions {

    @Mock
    Random random;

    @InjectMocks
    PostingFrequency postingFrequency;

    @Test
    @DisplayName("create timer endpoint")
    public void createTimerEndpoint() {
        //when
        String timer = postingFrequency.startTimer(100);
        //then
        assertThat(timer)
                .isEqualTo("timer:GuCoca.TwitterStories.Start?period=100000");
    }

    @Test
    @DisplayName("shouldIRun() is true")
    public void shouldIRunIsTrue() {
        //given
        int bound = 100;
        given(random.nextInt(bound)).willReturn(42);
        //when
        boolean result = postingFrequency.shouldIRun(42);
        //then
        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("shouldIRun() is false")
    public void shouldIRunIsFalse() {
        //given
        int bound = 100;
        given(random.nextInt(bound)).willReturn(42);
        //when
        boolean result = postingFrequency.shouldIRun(41);
        //then
        assertThat(result).isFalse();
    }
}