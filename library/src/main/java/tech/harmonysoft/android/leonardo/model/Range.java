package tech.harmonysoft.android.leonardo.model;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public class Range {

    public static final Range NO_RANGE = new Range(0, 0) {
        @Nonnull
        @Override
        public String toString() {
            return "NO_RANGE";
        }
    };

    private final long mStart;
    private final long mEnd;

    /**
     * @param start     start value (inclusive)
     * @param end       end value (inclusive)
     */
    public Range(long start, long end) {
        mStart = start;
        mEnd = end;
    }

    @Nonnull
    public Range shift(long delta) {
        return new Range(mStart + delta, mEnd + delta);
    }

    public boolean isEmpty() {
        return mStart >= mEnd;
    }

    /**
     * @return      start value (inclusive)
     */
    public long getStart() {
        return mStart;
    }

    /**
     * @return      end value (inclusive)
     */
    public long getEnd() {
        return mEnd;
    }

    public long getPointsNumber() {
        return mEnd - mStart + 1;
    }

    public long findFirstStepValue(long step) {
        if (step <= 0) {
            throw new IllegalArgumentException(String.format("Given step value (%d) is negative. Range: %s",
                                                             step, this));
        }

        if (step > mEnd && step > -mStart) {
            return Long.MIN_VALUE;
        }

        for (long value = step * (mStart / step); ; value += step) {
            if (contains(value)) {
                return value;
            } else if (value > mEnd) {
                return Long.MIN_VALUE;
            }
        }
    }

    public boolean contains(long value) {
        return value >= mStart && value <= mEnd;
    }

    @Nonnull
    public Range padBy(long padSize) {
        long startToUse = mStart;
        long startToPad = mStart % padSize;
        if (startToPad > 0) {
            startToUse -= startToPad;
        } else if (startToPad < 0) {
            startToUse -= padSize + startToPad;
        }

        long endToUse = mEnd;
        long endToPad = mEnd % padSize;
        if (endToPad > 0) {
            endToUse += padSize - endToPad;
        } else if (endToPad < 0) {
            endToUse -= endToPad;
        }

        if (startToUse == mStart && endToUse == mEnd) {
            return this;
        } else {
            return new Range(startToUse, endToUse);
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(mStart, mEnd);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        Range range = (Range) o;
        return mStart == range.mStart && mEnd == range.mEnd;
    }

    @Nonnull
    @Override
    public String toString() {
        return "(" + mStart + "; " + mEnd + ")";
    }
}
