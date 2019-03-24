package tech.harmonysoft.android.leonardo.model.config.impl;

import org.junit.jupiter.api.Test;
import tech.harmonysoft.android.leonardo.model.config.axis.impl.DefaultAxisLabelTextStrategy;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Denis Zhdanov
 * @since 12/3/19
 */
public class DefaultAxisLabelTextStrategyTest {

    private DefaultAxisLabelTextStrategy mStrategy = DefaultAxisLabelTextStrategy.INSTANCE;

    @Test
    public void whenValueIsLessThan1000_thenItIsReturnedAsIs() {
        assertThat(mStrategy.getLabel(999, 100).toString()).isEqualTo("999");
    }

    @Test
    public void whenValueIs1000_thenItIsShortenedToK() {
        assertThat(mStrategy.getLabel(1000, 100).toString()).isEqualTo("1K");
    }

    @Test
    public void whenValueIsMoreThan1000AndDivisibleBy1000AndLessThanMillion_thenItIsShortenedToK() {
        assertThat(mStrategy.getLabel(900000, 100).toString()).isEqualTo("900K");
    }

    @Test
    public void whenValueIsMoreThan1000AndNotDivisibleBy1000_thenItIsReturnedAsIs() {
        assertThat(mStrategy.getLabel(900001, 100).toString()).isEqualTo("900001");
    }

    @Test
    public void whenValueIs1000000_thenItIsShortenedToM() {
        assertThat(mStrategy.getLabel(1000000, 100).toString()).isEqualTo("1M");
    }

    @Test
    public void whenValueIsMoreThan1000000AndDivisibleBy1000000_thenItIsShortenedToM() {
        assertThat(mStrategy.getLabel(9000000, 100).toString()).isEqualTo("9M");
    }

    @Test
    public void whenValueIsMoreThan1000000AndNotDivisibleBy1000000_thenItIsReturnedAsIs() {
        assertThat(mStrategy.getLabel(9000001, 100).toString()).isEqualTo("9000001");
    }

    @Test
    public void whenValueIsDivisibleBy100_thenItIsShortenedCorrectly() {
        assertThat(mStrategy.getLabel(6500, 100).toString()).isEqualTo("6.5K");
        assertThat(mStrategy.getLabel(6300, 100).toString()).isEqualTo("6.3K");
        assertThat(mStrategy.getLabel(6500, 500).toString()).isEqualTo("6.5K");
        assertThat(mStrategy.getLabel(6500, 1000).toString()).isEqualTo("6.5K");
        assertThat(mStrategy.getLabel(6500, 5000).toString()).isEqualTo("6.5K");
        assertThat(mStrategy.getLabel(16500, 5000).toString()).isEqualTo("16.5K");
        assertThat(mStrategy.getLabel(923700, 5000).toString()).isEqualTo("923.7K");
        assertThat(mStrategy.getLabel(18900000, 5000).toString()).isEqualTo("18.9M");
    }

    @Test
    public void whenMinifiedValueIsAsked_thenItIsProducedCorrectly() {
        assertThat(mStrategy.getMinifiedLabel(183, 100).toString()).isEqualTo("183");
        assertThat(mStrategy.getMinifiedLabel(183, 1000).toString()).isEqualTo("183");
        assertThat(mStrategy.getMinifiedLabel(1582, 1000).toString()).isEqualTo("~2K");
        assertThat(mStrategy.getMinifiedLabel(2000, 1000).toString()).isEqualTo("2K");
        assertThat(mStrategy.getMinifiedLabel(2387, 1000).toString()).isEqualTo("~2K");
        assertThat(mStrategy.getMinifiedLabel(23687, 1000).toString()).isEqualTo("~24K");
        assertThat(mStrategy.getMinifiedLabel(15823, 1000).toString()).isEqualTo("~16K");
        assertThat(mStrategy.getMinifiedLabel(19823, 1000).toString()).isEqualTo("~20K");
        assertThat(mStrategy.getMinifiedLabel(7000000, 1000000).toString()).isEqualTo("7M");
        assertThat(mStrategy.getMinifiedLabel(7123456, 1000000).toString()).isEqualTo("~7M");
        assertThat(mStrategy.getMinifiedLabel(7500000, 1000000).toString()).isEqualTo("~8M");
        assertThat(mStrategy.getMinifiedLabel(7987654, 1000000).toString()).isEqualTo("~8M");
        assertThat(mStrategy.getMinifiedLabel(312345678L, 1000000).toString()).isEqualTo("~312M");
    }
}