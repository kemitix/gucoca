package net.kemitix.gucoca;

import com.fasterxml.jackson.core.JsonParseException;
import lombok.*;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

class JacksonJsonObjectParserTest
        implements WithAssertions {

    @Test
    @DisplayName("Deserialise JSON")
    public void DeserialiseJson() {
        //given
        String json = "{\"name\":\"Arthur Dent\",\"age\":42}";
        var inputStream = new ByteArrayInputStream(json.getBytes(UTF_8));
        Subject expected = new Subject("Arthur Dent", 42);
        //when
        Subject subject = new JacksonJsonObjectParser().fromJson(inputStream, Subject.class);
        //then
        assertThat(subject).isEqualTo(expected);
    }

    @Test
    @DisplayName("Deserialise Error wrapped in RuntimeException")
    public void WrapErrorsInRuntimeException() {
        //given
        String json = "{\"name\":\"Arthur Dent\"\"age\":42}";
        var inputStream = new ByteArrayInputStream(json.getBytes(UTF_8));
        //then
        assertThatCode(() ->
                new JacksonJsonObjectParser()
                        .fromJson(inputStream, Subject.class))
                .isExactlyInstanceOf(RuntimeException.class)
                .hasCauseExactlyInstanceOf(JsonParseException.class);
    }

    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @EqualsAndHashCode
    static class Subject {
        private String name;
        private int age;
    }

}