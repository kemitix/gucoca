package net.kemitix.gucoca.stories.s3;

import javax.batch.api.AbstractBatchlet;
import javax.batch.runtime.BatchStatus;
import javax.inject.Named;

@Named
public class LoadS3Stories extends AbstractBatchlet {

    @Override
    public String process() throws Exception {
        return BatchStatus.COMPLETED.toString();
    }

}
