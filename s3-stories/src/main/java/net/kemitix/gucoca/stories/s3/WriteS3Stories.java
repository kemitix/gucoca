package net.kemitix.gucoca.stories.s3;

import lombok.extern.java.Log;
import net.kemitix.gucoca.spi.JobStateData;
import net.kemitix.gucoca.spi.Story;

import javax.batch.api.chunk.AbstractItemWriter;
import javax.batch.runtime.context.JobContext;
import javax.batch.runtime.context.StepContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Log
public class WriteS3Stories extends AbstractItemWriter {

    @Inject JobContext jobContext;
    @Inject StepContext stepContext;

    @Override
    public void writeItems(List<Object> items) {
        log.info("New Items: " + items.size());
        List<Object> list = Optional.ofNullable(
                (List<Object>) stepContext.getTransientUserData())
                .orElseGet(ArrayList::new);
        list.addAll(items);
        stepContext.setTransientUserData(list);
        log.info("Collected items: " + list.size());
    }

    @Override
    public void close() {
        JobStateData jobStateData =
                ((JobStateData) jobContext.getTransientUserData());
        List<Story> stories = (List<Story>) stepContext.getTransientUserData();
        jobStateData.setStories(stories);
        jobContext.setTransientUserData(jobStateData);
    }
}
