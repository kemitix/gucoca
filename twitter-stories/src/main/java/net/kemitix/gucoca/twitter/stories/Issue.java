package net.kemitix.gucoca.twitter.stories;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;

@ToString
@Setter
@Getter
public class Issue {
    int issue;
    Instant published;
    List<Story> stories;
}
