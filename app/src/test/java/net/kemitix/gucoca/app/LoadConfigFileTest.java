package net.kemitix.gucoca.app;

import net.kemitix.gucoca.spi.GucocaConfig;
import net.kemitix.gucoca.spi.JobStateData;
import net.kemitix.gucoca.spi.JsonObjectParser;
import net.kemitix.gucoca.utils.ServiceSupplier;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.batch.runtime.context.JobContext;

import java.io.InputStream;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@ExtendWith(MockitoExtension.class)
public class LoadConfigFileTest
        implements WithAssertions {

    private final LoadConfigFile loadConfigFile =
            new LoadConfigFile();

    private final JobContext jobContext;
    private final ServiceSupplier serviceSupplier;
    private final JsonObjectParser jsonObjectParser;
    private final ArgumentCaptor<Object> dataCaptor =
            ArgumentCaptor.forClass(Object.class);

    public LoadConfigFileTest(
            @Mock JobContext jobContext,
            @Mock ServiceSupplier serviceSupplier,
            @Mock JsonObjectParser jsonObjectParser
    ) {
        this.jobContext = jobContext;
        this.serviceSupplier = serviceSupplier;
        this.jsonObjectParser = jsonObjectParser;
    }

    @BeforeEach
    public void setUp() {
        loadConfigFile.jobContext = jobContext;
        loadConfigFile.serviceSupplier = serviceSupplier;

        given(serviceSupplier.findOne(JsonObjectParser.class))
                .willReturn(jsonObjectParser);
    }

    @Test
    @DisplayName("Load a Config File")
    public void loadConfigFile() throws Exception {
        //given
        loadConfigFile.configFile = LoadConfigFileTest.class
                .getResource("gucoca-config.json")
                .getFile();
        GucocaConfig gucocaConfig = new GucocaConfig();
        given(jsonObjectParser.fromJson(
                any(InputStream.class), eq(GucocaConfig.class)))
                .willReturn(gucocaConfig);
        //when
        loadConfigFile.process();
        //then
        then(jobContext).should()
                .setTransientUserData(dataCaptor.capture());
        assertThat(dataCaptor.getValue()).isInstanceOf(JobStateData.class);
        JobStateData stateData = (JobStateData) dataCaptor.getValue();
        assertThat(stateData.getConfig())
                .isEqualTo(gucocaConfig);
    }
}