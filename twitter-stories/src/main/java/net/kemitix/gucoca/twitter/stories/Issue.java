package net.kemitix.gucoca.twitter.stories;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class Issue {
    int issue;
    List<Story> stories;
}
