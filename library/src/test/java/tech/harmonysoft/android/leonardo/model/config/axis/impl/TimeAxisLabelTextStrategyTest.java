package tech.harmonysoft.android.leonardo.model.config.axis.impl;

import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

import static org.assertj.core.api.Assertions.assertThat;
import static tech.harmonysoft.android.leonardo.model.config.axis.impl.TimeAxisLabelTextStrategy.*;

/**
 * @author Denis Zhdanov
 * @since 21/3/19
 */
class TimeAxisLabelTextStrategyTest {

    private static final SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");

    static {
        FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
    }

    private final TimeAxisLabelTextStrategy mLabelStrategy = new TimeAxisLabelTextStrategy(TimeZone.getTimeZone("UTC"));

    @Test
    public void whenStepMillisecondIsUsed_thenFormattedValueIsCorrect() {
        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 01:10:10.456"), MILLISECOND).toString())
                .isEqualTo("10.456");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 01:01:01.456"), MILLISECOND).toString())
                .isEqualTo("01.456");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 01:12:13.456"), MILLISECOND).toString())
                .isEqualTo("13.456");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 01:12:13.100"), MILLISECOND).toString())
                .isEqualTo("13.100");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 01:12:13.010"), MILLISECOND).toString())
                .isEqualTo("13.010");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 01:12:13.001"), MILLISECOND).toString())
                .isEqualTo("13.001");
    }

    @Test
    public void whenStepSecondIsUsed_thenFormattedValueIsCorrect() {
        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 01:02:14.456"), SECOND).toString())
                .isEqualTo("02:14");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 01:20:14.456"), SECOND).toString())
                .isEqualTo("20:14");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 01:20:04.456"), SECOND).toString())
                .isEqualTo("20:04");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 01:20:50.456"), SECOND).toString())
                .isEqualTo("20:50");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 01:12:14.456"), SECOND).toString())
                .isEqualTo("12:14");
    }

    @Test
    public void whenStepMinuteIsUsed_thenFormattedValueIsCorrect() {
        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 20:12:14.456"), MINUTE).toString())
                .isEqualTo("20:12");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 05:12:14.456"), MINUTE).toString())
                .isEqualTo("05:12");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 15:10:14.456"), MINUTE).toString())
                .isEqualTo("15:10");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 15:01:14.456"), MINUTE).toString())
                .isEqualTo("15:01");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 15:12:14.456"), MINUTE).toString())
                .isEqualTo("15:12");
    }

    @Test
    public void whenStepHourIsUsed_thenFormattedValueIsCorrect() {
        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 10:16:00.00"), HOUR).toString())
                .isEqualTo("10:16");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 03:16:00.00"), HOUR).toString())
                .isEqualTo("03:16");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 03:06:00.00"), HOUR).toString())
                .isEqualTo("03:06");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 03:10:00.00"), HOUR).toString())
                .isEqualTo("03:10");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-21 15:16:00.00"), HOUR).toString())
                .isEqualTo("15:16");
    }

    @Test
    public void whenStepDayIsUsed_thenFormattedValueIsCorrect() {
        assertThat(mLabelStrategy.getLabel(parse("1979-01-16 15:00:00.000"), DAY).toString())
                .isEqualTo("Jan 16");

        assertThat(mLabelStrategy.getLabel(parse("1980-01-01 00:00:00.000"), DAY).toString())
                .isEqualTo("Jan 1");

        assertThat(mLabelStrategy.getLabel(parse("1980-12-31 23:59:59.999"), DAY).toString())
                .isEqualTo("Dec 31");

        assertThat(mLabelStrategy.getLabel(parse("1980-02-29 00:00:00.000"), DAY).toString())
                .isEqualTo("Feb 29");

        assertThat(mLabelStrategy.getLabel(parse("1980-02-29 23:59:59.999"), DAY).toString())
                .isEqualTo("Feb 29");

        assertThat(mLabelStrategy.getLabel(parse("1980-03-01 00:00:00.000"), DAY).toString())
                .isEqualTo("Mar 1");

        assertThat(mLabelStrategy.getLabel(parse("1981-01-01 00:00:00.000"), DAY).toString())
                .isEqualTo("Jan 1");

        assertThat(mLabelStrategy.getLabel(parse("1981-12-31 23:59:59.999"), DAY).toString())
                .isEqualTo("Dec 31");

        assertThat(mLabelStrategy.getLabel(parse("1981-01-01 01:00:00.000"), DAY).toString())
                .isEqualTo("Jan 1");

        assertThat(mLabelStrategy.getLabel(parse("1981-01-16 15:00:00.000"), DAY).toString())
                .isEqualTo("Jan 16");

        assertThat(mLabelStrategy.getLabel(parse("1989-01-16 15:00:00.000"), DAY).toString())
                .isEqualTo("Jan 16");

        assertThat(mLabelStrategy.getLabel(parse("2019-01-16 15:00:00.000"), DAY).toString())
                .isEqualTo("Jan 16");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-04 15:00:00.000"), DAY).toString())
                .isEqualTo("Mar 4");

        assertThat(mLabelStrategy.getLabel(parse("2019-12-18 15:00:00.000"), DAY).toString())
                .isEqualTo("Dec 18");
    }

    @Test
    public void whenStepMonthIsUsed_thenFormattedValueIsCorrect() {
        assertThat(mLabelStrategy.getLabel(parse("1979-01-16 15:00:00.000"), MONTH).toString())
                .isEqualTo("Jan");

        assertThat(mLabelStrategy.getLabel(parse("1980-01-01 00:00:00.000"), MONTH).toString())
                .isEqualTo("Jan");

        assertThat(mLabelStrategy.getLabel(parse("1980-12-31 23:59:59.999"), MONTH).toString())
                .isEqualTo("Dec");

        assertThat(mLabelStrategy.getLabel(parse("1980-02-29 00:00:00.000"), MONTH).toString())
                .isEqualTo("Feb");

        assertThat(mLabelStrategy.getLabel(parse("1980-02-29 23:59:59.999"), MONTH).toString())
                .isEqualTo("Feb");

        assertThat(mLabelStrategy.getLabel(parse("1980-03-01 00:00:00.000"), MONTH).toString())
                .isEqualTo("Mar");

        assertThat(mLabelStrategy.getLabel(parse("1981-01-01 00:00:00.000"), MONTH).toString())
                .isEqualTo("Jan");

        assertThat(mLabelStrategy.getLabel(parse("1981-12-31 23:59:59.999"), MONTH).toString())
                .isEqualTo("Dec");

        assertThat(mLabelStrategy.getLabel(parse("1981-01-01 01:00:00.000"), MONTH).toString())
                .isEqualTo("Jan");

        assertThat(mLabelStrategy.getLabel(parse("1981-01-16 15:00:00.000"), MONTH).toString())
                .isEqualTo("Jan");

        assertThat(mLabelStrategy.getLabel(parse("1989-01-16 15:00:00.000"), MONTH).toString())
                .isEqualTo("Jan");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-04 15:00:00.00"), MONTH).toString())
                .isEqualTo("Mar");
    }

    @Test
    public void whenStepYearIsUsed_thenFormattedValueIsCorrect() {
        assertThat(mLabelStrategy.getLabel(parse("1979-01-16 15:00:00.000"), YEAR).toString())
                .isEqualTo("1979");

        assertThat(mLabelStrategy.getLabel(parse("1980-01-01 00:00:00.000"), YEAR).toString())
                .isEqualTo("1980");

        assertThat(mLabelStrategy.getLabel(parse("1980-12-31 23:59:59.999"), YEAR).toString())
                .isEqualTo("1980");

        assertThat(mLabelStrategy.getLabel(parse("1980-02-29 00:00:00.000"), YEAR).toString())
                .isEqualTo("1980");

        assertThat(mLabelStrategy.getLabel(parse("1980-02-29 23:59:59.999"), YEAR).toString())
                .isEqualTo("1980");

        assertThat(mLabelStrategy.getLabel(parse("1980-03-01 00:00:00.000"), YEAR).toString())
                .isEqualTo("1980");

        assertThat(mLabelStrategy.getLabel(parse("1981-01-01 00:00:00.000"), YEAR).toString())
                .isEqualTo("1981");

        assertThat(mLabelStrategy.getLabel(parse("1981-12-31 23:59:59.999"), YEAR).toString())
                .isEqualTo("1981");

        assertThat(mLabelStrategy.getLabel(parse("1981-01-01 01:00:00.000"), YEAR).toString())
                .isEqualTo("1981");

        assertThat(mLabelStrategy.getLabel(parse("1981-01-16 15:00:00.000"), YEAR).toString())
                .isEqualTo("1981");

        assertThat(mLabelStrategy.getLabel(parse("1989-01-16 15:00:00.000"), YEAR).toString())
                .isEqualTo("1989");

        assertThat(mLabelStrategy.getLabel(parse("2019-01-16 15:00:00.000"), YEAR).toString())
                .isEqualTo("2019");

        assertThat(mLabelStrategy.getLabel(parse("2019-03-04 15:00:00.000"), YEAR).toString())
                .isEqualTo("2019");
        assertThat(mLabelStrategy.getLabel(parse("2019-03-04 15:00:00.00"), YEAR).toString())
                .isEqualTo("2019");
    }

    @Test
    public void whenStepGoesBeyondYear_thenFormattedValueIsCorrect() {
        assertThat(mLabelStrategy.getLabel(parse("2019-03-04 15:00:00.00"), YEAR * 2).toString())
                .isEqualTo("2019");
    }

    private static long parse(String date) {
        try {
            return FORMAT.parse(date).getTime();
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
    }
}