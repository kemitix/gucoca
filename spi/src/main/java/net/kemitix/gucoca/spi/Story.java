package net.kemitix.gucoca.spi;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

@Setter
@Getter
public class Story {
    private int issue;
    private String title;
    private String author;
    private List<String> blurb;
    private String url;
}
