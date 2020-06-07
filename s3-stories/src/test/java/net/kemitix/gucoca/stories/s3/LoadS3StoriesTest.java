package net.kemitix.gucoca.stories.s3;

import com.amazonaws.services.s3.model.ListObjectsV2Request;
import com.amazonaws.services.s3.model.ListObjectsV2Result;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.JobStateData;
import net.kemitix.gucoca.utils.ServiceSupplier;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.batch.runtime.context.JobContext;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class LoadS3StoriesTest
        implements WithAssertions {

    private final LoadS3Stories loadS3Stories = new LoadS3Stories();

    private final ServiceSupplier serviceSupplier;

    private final AmazonS3Service amazonS3;
    private final ListObjectsV2Result page1Results;
    private final ListObjectsV2Result page2Results;
    private final String page2Token = UUID.randomUUID().toString();
    private final String bucketName = UUID.randomUUID().toString();
    private final String bucketPrefix = UUID.randomUUID().toString();

    private final S3ObjectSummary item1;
    private final S3ObjectSummary item2;
    private final S3ObjectSummary item3;
    private final List<S3ObjectSummary> page1Summaries;
    private final List<S3ObjectSummary> page2Summaries;
    private final JobContext jobContext;

    public LoadS3StoriesTest(
            @Mock ServiceSupplier serviceSupplier,
            @Mock AmazonS3Service amazonS3,
            @Mock ListObjectsV2Result page1Results,
            @Mock ListObjectsV2Result page2Results,
            @Mock S3ObjectSummary item1,
            @Mock S3ObjectSummary item2,
            @Mock S3ObjectSummary item3,
            @Mock JobContext jobContext
    ) {
        this.serviceSupplier = serviceSupplier;
        this.amazonS3 = amazonS3;
        this.page1Results = page1Results;
        this.page2Results = page2Results;
        this.item1 = item1;
        this.item2 = item2;
        this.item3 = item3;
        this.jobContext = jobContext;
        page1Summaries = Arrays.asList(item1, item2);
        page2Summaries = Collections.singletonList(item3);
    }

    @BeforeEach
    public void setUp() {
        loadS3Stories.serviceSupplier = serviceSupplier;
        loadS3Stories.jobContext = jobContext;
        JobStateData jobStateData = new JobStateData();
        GucocaConfig config = new GucocaConfig();
        config.setS3BucketName(bucketName);
        config.setS3BucketPrefix(bucketPrefix);
        jobStateData.setConfig(config);
        given(jobContext.getTransientUserData()).willReturn(jobStateData);

        given(serviceSupplier.findOne(AmazonS3Service.class))
                .willReturn(amazonS3);

        given(amazonS3.listObjectsV2(any(ListObjectsV2Request.class)))
                .willReturn(page1Results)
                .willReturn(page2Results);
        given(page1Results.isTruncated()).willReturn(true);
        given(page1Results.getNextContinuationToken()).willReturn(page2Token);
        given(page2Results.isTruncated()).willReturn(false);

        given(page1Results.getObjectSummaries()).willReturn(page1Summaries);
        given(page2Results.getObjectSummaries()).willReturn(page2Summaries);

        loadS3Stories.open(null);
    }

    @Test
    @DisplayName("Reads items from both pages")
    public void readsItemsOnBothPages() {
        //when
        Object a = loadS3Stories.readItem();
        Object b = loadS3Stories.readItem();
        Object c = loadS3Stories.readItem();
        Object d = loadS3Stories.readItem();
        //then
        assertThat(a).isEqualTo(item1);
        assertThat(b).isEqualTo(item2);
        assertThat(c).isEqualTo(item3);
        assertThat(d).isNull();
    }
}