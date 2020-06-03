package net.kemitix.gucoca;

import javax.batch.operations.JobOperator;
import javax.batch.runtime.BatchRuntime;
import javax.inject.Named;
import java.util.function.Supplier;

@Named
public class JobOperatorProducer implements Supplier<JobOperator> {

    @Override
    public JobOperator get() {
        return BatchRuntime.getJobOperator();
    }
}
