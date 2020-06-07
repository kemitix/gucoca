package net.kemitix.gucoca.spi;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class GucocaConfig {

    private String s3BucketName;
    private String s3BucketPrefix;
    private String storyFilename;

}
