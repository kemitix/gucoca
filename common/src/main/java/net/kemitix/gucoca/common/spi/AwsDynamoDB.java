package net.kemitix.gucoca.common.spi;

import net.kemitix.gucoca.common.AwsDdbConfig;

public interface AwsDynamoDB {

    String scan(AwsDdbConfig config);
    String put(AwsDdbConfig config);

}
