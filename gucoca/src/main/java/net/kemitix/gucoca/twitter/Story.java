package net.kemitix.gucoca.twitter;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.List;

@EqualsAndHashCode
@Setter
@Getter
public class Story {
    private int issue;
    private String title;
    private String author;
    private List<String> blurb;
    private String url;
    private String key;
    private String published;

    public String slug() {
        String[] split = url.split("/");
        return split[split.length - 1];
    }

    public String cardKey() {
        return String.format("content/issue/%d/story-card-%s.webp",
                getIssue(), slug());
    }

    public boolean isPublished(Instant when) {
        return published != null &&
                !published.isEmpty() &&
                    LocalDate.parse(published)
                            .atStartOfDay()
                            .toInstant(ZoneOffset.UTC)
                            .isBefore(when);
    }
}
