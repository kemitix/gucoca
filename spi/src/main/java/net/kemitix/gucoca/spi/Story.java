package net.kemitix.gucoca.spi;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@ToString
@Setter
@Getter
public class Story {
    private int issue;
    private String title;
    private String author;
    private List<String> blurb;
    private String url;
    private String key;

    public String slug() {
        String[] split = url.split("/");
        return split[split.length - 1];
    }
}
