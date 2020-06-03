package net.kemitix.gucoca.app;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.batch.operations.JobOperator;
import java.util.Collections;
import java.util.Properties;
import java.util.function.Supplier;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class AppTest implements WithAssertions {

    private final App app = new App();
    private final JobOperator jobOperator;
    private final Supplier<JobOperator> jobOperatorSupplier;

    public AppTest(@Mock JobOperator jobOperator) {
        this.jobOperator = jobOperator;
        this.jobOperatorSupplier = () -> jobOperator;
    }

    @Test
    @DisplayName("Launches Job")
    public void launchesJob() {
        //given
        app.jobOperator = jobOperatorSupplier;
        //when
        app.run(Collections.emptyList());
        //then
        then(jobOperator)
                .should()
                .start(eq("gucoca"), any(Properties.class));
    }

}