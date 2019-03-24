package tech.harmonysoft.android.leonardo.model.config.axis.impl;

import android.annotation.SuppressLint;
import tech.harmonysoft.android.leonardo.model.axis.AxisLabelTextStrategy;
import tech.harmonysoft.android.leonardo.model.text.TextWrapper;

import javax.annotation.Nonnull;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

/**
 * @author Denis Zhdanov
 * @since 21/3/19
 */
@SuppressLint("SimpleDateFormat")
public class TimeAxisLabelTextStrategy implements AxisLabelTextStrategy {

    public static final long MILLISECOND = 1;
    public static final long SECOND      = TimeUnit.SECONDS.toMillis(1);
    public static final long MINUTE      = TimeUnit.MINUTES.toMillis(1);
    public static final long HOUR        = TimeUnit.HOURS.toMillis(1);
    public static final long DAY         = TimeUnit.DAYS.toMillis(1);
    public static final long MONTH       = DAY * 30;
    public static final long YEAR        = 365 * DAY;
    public static final long LEAP_YEAR   = YEAR + DAY + 30 * MINUTE;
    public static final long TWO_YEARS   = 2 * YEAR;
    public static final long YEAR_CYCLE  = 4 * YEAR + DAY;

    private static final DayInfo[] MONTHS = new DayInfo[365];

    static {
        int offset = 0;
        offset += addMonths("Jan".toCharArray(), offset, 31);
        offset += addMonths("Feb".toCharArray(), offset, 28);
        offset += addMonths("Mar".toCharArray(), offset, 31);
        offset += addMonths("Apr".toCharArray(), offset, 30);
        offset += addMonths("May".toCharArray(), offset, 31);
        offset += addMonths("Jun".toCharArray(), offset, 30);
        offset += addMonths("Jul".toCharArray(), offset, 31);
        offset += addMonths("Aug".toCharArray(), offset, 31);
        offset += addMonths("Sep".toCharArray(), offset, 30);
        offset += addMonths("Oct".toCharArray(), offset, 31);
        offset += addMonths("Nov".toCharArray(), offset, 30);
        addMonths("Dec".toCharArray(), offset, 31);
    }

    private static int addMonths(char[] monthName, int offset, int length) {
        for (int i = 0; i < length; i++) {
            MONTHS[offset + i] = new DayInfo(monthName, i + 1);
        }
        return length;
    }

    private static final long FEBRUARY_29 = 31 + 28 - 1;

    private static final Formatter FORMATTER_MILLISECOND = (value, text) -> {
        long trimmed = value % MINUTE;
        appendAndPadIfNecessary(trimmed / SECOND, 10, text);
        text.append('.');
        appendAndPadIfNecessary(trimmed % SECOND, 100, text);
    };

    private static final Formatter FORMATTER_SECOND = (value, text) -> {
        long trimmed = value % HOUR;
        appendAndPadIfNecessary(trimmed / MINUTE, 10, text);
        text.append(':');
        appendAndPadIfNecessary((trimmed % MINUTE) / SECOND, 10, text);
    };

    private static final Formatter FORMATTER_MINUTE = (value, text) -> {
        long trimmed = value % DAY;
        appendAndPadIfNecessary(trimmed / HOUR, 10, text);
        text.append(':');
        appendAndPadIfNecessary((trimmed % HOUR) / MINUTE, 10, text);
    };

    private static final Formatter FORMATTER_DAY =
            (value, text) -> processLeapAwareTime(value, text, false, true, true);

    private static final Formatter FORMATTER_MONTH =
            (value, text) -> processLeapAwareTime(value, text, false, true, false);

    private static final Formatter FORMATTER_YEAR =
            (value, text) -> processLeapAwareTime(value, text, true, false, false);

    private static void processLeapAwareTime(long value,
                                             TextWrapper text,
                                             boolean printYear,
                                             boolean printMonth,
                                             boolean printDay)
    {
        int year = 1968;
        long trimmed = (value + TWO_YEARS) % YEAR_CYCLE;
        year += 4 * ((value + TWO_YEARS) / YEAR_CYCLE);
        boolean leap = trimmed <= LEAP_YEAR;
        while (trimmed >= YEAR) {
            trimmed -= YEAR;
            year++;
        }

        long days = trimmed / DAY;

        if (printYear) {
            text.append(year);
            if (!printMonth) {
                return;
            }
        }

        if (leap && days == FEBRUARY_29) {
            text.append(MONTHS[32].month);
            if (printDay) {
                text.append(" 29");
            }

        } else {
            DayInfo dayInfo = MONTHS[(int) days];
            text.append(dayInfo.month);
            if (printDay) {
                text.append(' ');
                text.append(dayInfo.day);
            }
        }
    }

    private static void appendAndPadIfNecessary(long value, long base, TextWrapper text) {
        while (base > 0) {
            text.append(value / base);
            value %= base;
            base /= 10;
        }
    }

    private final TextWrapper mText = new TextWrapper();

    private final long mOffset;

    public TimeAxisLabelTextStrategy() {
        this(TimeZone.getDefault());
    }

    public TimeAxisLabelTextStrategy(TimeZone timeZone) {
        mOffset = timeZone.getRawOffset();
    }

    @Nonnull
    @Override
    public TextWrapper getLabel(final long value, long step) {
        mText.reset();
        final Formatter formatter;
        if (step >= MILLISECOND && step < SECOND) {
            formatter = FORMATTER_MILLISECOND;
        } else if (step >= SECOND && step < MINUTE) {
            formatter = FORMATTER_SECOND;
        } else if (step >= MINUTE && step < DAY) {
            formatter = FORMATTER_MINUTE;
        } else if (step >= DAY && step < MONTH) {
            formatter = FORMATTER_DAY;
        } else if (step >= MONTH && step < YEAR) {
            formatter = FORMATTER_MONTH;
        } else {
            formatter = FORMATTER_YEAR;
        }
        formatter.format(value + mOffset, mText);
        return mText;
    }

    @Nonnull
    @Override
    public TextWrapper getMinifiedLabel(long value, long step) {
        return getLabel(value, step);
    }

    private interface Formatter {

        void format(long value, TextWrapper text);
    }

    private static class DayInfo {

        public final char[] month;
        public final int    day;

        public DayInfo(char[] month, int day) {
            this.month = month;
            this.day = day;
        }
    }
}
