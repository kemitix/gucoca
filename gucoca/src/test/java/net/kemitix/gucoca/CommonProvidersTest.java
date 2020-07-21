package net.kemitix.gucoca;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Random;
import java.util.function.Supplier;

class CommonProvidersTest
implements WithAssertions {

    CommonProviders commonProviders = new CommonProviders();

    @Test
    @DisplayName("produces an instance of Random")
    public void producesARandomInstance() {
        //when
        var random = commonProviders.random();
        //then
        assertThat(random)
                .isInstanceOf(Random.class);
    }

    @Test
    @DisplayName("produces an instance of ObjectMapper")
    public void producesAnObjectMapperInstance() {
        //when
        var mapper = commonProviders.objectMapper();
        //then
        assertThat(mapper)
                .isInstanceOf(ObjectMapper.class);
    }

    @Test
    @DisplayName("produces a Supplier of unique instances of Instant")
    public void producesSupplierOfUniqueInstancesOfInstant() {
        //when
        Supplier<Instant> now = commonProviders.now();
        //then
        assertThat(now.get()).isNotSameAs(now.get());
    }
}