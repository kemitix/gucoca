package net.kemitix.gucoca.stories.s3;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;

@ExtendWith(MockitoExtension.class)
public class WriteS3StoriesTest
        implements WithAssertions {

    private final WriteS3Stories itemWriter = new WriteS3Stories();

    private final JobContext jobContext;
    private final StepContext stepContext;
    private final AtomicReference<Object> jobData = new AtomicReference<>();
    private final AtomicReference<Object> stepData = new AtomicReference<>();

    public WriteS3StoriesTest(
            @Mock JobContext jobContext,
            @Mock StepContext stepContext
    ) {
        this.jobContext = jobContext;
        this.stepContext = stepContext;
    }

    @BeforeEach
    public void setUp() {
        itemWriter.jobContext = jobContext;
        itemWriter.stepContext = stepContext;

        // jobContext transientUserData
        doAnswer(onMock -> jobData.get())
                .when(jobContext).getTransientUserData();
        doAnswer(onMock -> {
            jobData.set(onMock.getArgument(0));
            return null;
        }).when(jobContext).setTransientUserData(any());

        // stepContext transientUserData
        doAnswer(onMock -> stepData.get())
                .when(stepContext).getTransientUserData();
        doAnswer(onMock -> {
            stepData.set(onMock.getArgument(0));
            return null;
        }).when(stepContext).setTransientUserData(any());
    }

    @Test
    @DisplayName("Items are added to StepContext")
    public void itemsAddedToStepContext() {
        //given
        List<Object> items = Arrays.asList(new Object(), new Object());
        //when
        itemWriter.writeItems(items);
        //then
        Object data = stepData.get();
        assertThat(data).isInstanceOf(List.class);
        List<Object> list = (List<Object>) data;
        assertThat(list).containsExactlyElementsOf(items);
    }

    @Test
    @DisplayName("Items are added to any existing item in StepContext")
    public void itemsAddedToExistingItemsInStepContext() {
        //given
        List<Object> items1 = Arrays.asList(new Object(), new Object());
        itemWriter.writeItems(items1);
        List<Object> items2 = Arrays.asList(new Object(), new Object());
        //when
        itemWriter.writeItems(items2);
        //then
        Object data = stepData.get();
        assertThat(data).isInstanceOf(List.class);
        List<Object> list = (List<Object>) data;
        assertThat(list).as("add items").containsAll(items2);
        assertThat(list).as("keep items").containsAll(items1);
        assertThat(list).hasSize(items1.size() + items2.size());
    }

    @Test
    @DisplayName("On close promote data from step to job context")
    public void promoteDataToJobContextOnClose() {
        //given
        Object value = new Object();
        stepData.set(value);
        //when
        itemWriter.close();
        //then
        assertThat(jobData).hasValue(value);
    }
}