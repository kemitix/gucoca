package net.kemitix.gucoca;

import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class FilteredSlipTest
        implements WithAssertions {

    @Test
    @DisplayName("Filter routes on blacklist")
    public void filterRoutesOnBlacklist() {
        //given
        var filteredSlip = new FilteredSlip();
        filteredSlip.blacklist = String.join(",",
                "route-2", "route-4");
        String in = String.join("," ,
                "route-1", "route-2", "route-3", "route-4");
        String expected = String.join(",",
                "route-1", "route-3");

        //when
        String result = filteredSlip.filter(in);

        //then
        assertThat(result).isEqualTo(expected);
    }

}