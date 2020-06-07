package net.kemitix.gucoca.spi;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class JobStateData {

    private GucocaConfig config;
    private List<Story> stories;

}
