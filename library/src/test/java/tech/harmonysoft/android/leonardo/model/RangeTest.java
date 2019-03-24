package tech.harmonysoft.android.leonardo.model;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * @author Denis Zhdanov
 * @since 14/3/19
 */
class RangeTest {

    @Test
    public void whenRangeHasNegativeValues_andExactStepMatch_thenFirstStepValueIsCorrect() {
        Range range = new Range(-10, 10);
        assertThat(range.findFirstStepValue(5)).isEqualTo(-10);
    }

    @Test
    public void whenRangeHasNegativeValues_andNotExactStepMatch_thenFirstStepValueIsCorrect() {
        Range range = new Range(-10, 10);
        assertThat(range.findFirstStepValue(3)).isEqualTo(-9);
    }

    @Test
    public void whenRangeHadNegativeValues_andStepIsTooBig_thenNoMatch() {
        assertThat(new Range(-2, 2).findFirstStepValue(4)).isEqualTo(Long.MIN_VALUE);
    }

    @Test
    public void whenStartPositive_andEndPositive_thenPadWorksCorrectly() {
        long pad = 5;
        for (long start = 0; start < pad; start++) {
            for (long end = pad + 1; end < pad * 2; end++) {
                assertThat(new Range(start, end).padBy(pad)).isEqualTo(new Range(0, pad * 2));
            }
        }
    }

    @Test
    public void whenStartNegative_andEndPositive_thenPadWorksCorrectly() {
        long pad = 5;
        for (long start = -pad + 1; start < 0; start++) {
            for (long end = 1; end < pad; end++) {
                assertThat(new Range(start, end).padBy(pad)).isEqualTo(new Range(-pad, pad));
            }
        }
    }

    @Test
    public void whenStartNegative_andEndNegative_thenPadWorksCorrectly() {
        long pad = 5;
        for (long start = -pad * 2 + 1; start < -pad; start++) {
            for (long end = -pad * 2 + 1; end < -pad; end++) {
                Range range = new Range(start, end);
                assertThat(range.padBy(pad)).describedAs(range.toString()).isEqualTo(new Range(-pad * 2, -pad));
            }
        }
    }
}