package net.kemitix.gucoca.stories.s3;

import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.JobStateData;
import net.kemitix.gucoca.spi.JsonObjectParser;
import net.kemitix.gucoca.spi.Story;
import net.kemitix.gucoca.utils.ServiceSupplier;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.batch.runtime.context.JobContext;
import java.util.UUID;

import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class ConvertS3StoriesTest
        implements WithAssertions {

    private final ConvertS3Stories processor = new ConvertS3Stories();

    private final ServiceSupplier serviceSupplier;

    private final S3ObjectSummary summary;
    private final AmazonS3Service amazonS3;
    private final String bucketName = UUID.randomUUID().toString();
    private final String bucketKey = UUID.randomUUID().toString();
    private final JsonObjectParser jsonObjectParser = new MockJsonObjectParser();
    private final S3Object s3Object;
    private final S3ObjectInputStream objectContent;
    private final Story story;
    private final JobContext jobContext;

    public ConvertS3StoriesTest(
            @Mock ServiceSupplier serviceSupplier,
            @Mock S3ObjectSummary summary,
            @Mock AmazonS3Service amazonS3,
            @Mock S3Object s3Object,
            @Mock S3ObjectInputStream objectContent,
            @Mock Story story,
            @Mock JobContext jobContext
    ) {
        this.serviceSupplier = serviceSupplier;
        this.summary = summary;
        this.amazonS3 = amazonS3;
        this.s3Object = s3Object;
        this.objectContent = objectContent;
        this.story = story;
        this.jobContext = jobContext;
    }

    @BeforeEach
    public void setUp() {
        processor.serviceSupplier = serviceSupplier;
        processor.jobContext = jobContext;
        JobStateData jobStateData = new JobStateData();
        GucocaConfig config = new GucocaConfig();
        config.setStoryFilename("target");
        jobStateData.setConfig(config);
        given(jobContext.getTransientUserData()).willReturn(jobStateData);

        given(serviceSupplier.findOne(AmazonS3Service.class))
                .willReturn(amazonS3);
        given(serviceSupplier.findOne(JsonObjectParser.class))
                .willReturn(jsonObjectParser);
    }

    @Test
    @DisplayName("Ignores non-story files")
    public void ignoresNonStoryFile() {
        //given
        given(summary.getKey()).willReturn("/path/to/invalid");
        //when
        Object result = processor.processItem(summary);
        //then
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Converts story files")
    public void convertsStoryFile() {
        //given
        String key = bucketKey + "/target";
        given(summary.getBucketName()).willReturn(bucketName);
        given(summary.getKey()).willReturn(key);
        given(amazonS3.getObject(bucketName, key)).willReturn(s3Object);
        given(s3Object.getObjectContent()).willReturn(objectContent);
        MockJsonObjectParser.fromJson.put(objectContent, story);
        //when
        Object result = processor.processItem(summary);
        //then
        assertThat(result).isSameAs(story);
    }

}