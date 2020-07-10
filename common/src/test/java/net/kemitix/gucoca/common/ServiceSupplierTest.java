package net.kemitix.gucoca.common;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

public class ServiceSupplierTest
        implements WithAssertions {

    @Test
    @DisplayName("Can find an existing service")
    public void canFindExistingService() {
        assertThat(ServiceSupplier.create()
                .findOne(TestService.class))
                .isInstanceOf(TestServiceImpl.class);
    }

    @Test
    @DisplayName("Throws exception when service not found")
    public void throwsWhenServiceNotFound() {
        assertThatExceptionOfType(ServiceSupplier.MissingService.class)
                .isThrownBy(() ->
                        ServiceSupplier.create()
                                .findOne(ServiceSupplierTest.class));
    }
}