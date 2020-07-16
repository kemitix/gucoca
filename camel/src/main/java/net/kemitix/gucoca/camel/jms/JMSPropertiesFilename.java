package net.kemitix.gucoca.camel.jms;

import net.kemitix.gucoca.common.spi.PropertyFilenames;

public class JMSPropertiesFilename
        implements PropertyFilenames {

    @Override
    public String getName() {
        return "jms";
    }

}
