package tech.harmonysoft.android.leonardo.model;

import android.annotation.SuppressLint;

import javax.annotation.Nonnull;
import java.util.Comparator;
import java.util.Objects;

/**
 * @author Denis Zhdanov
 * @since 12/3/19
 */
public class DataPoint {

    public static final Comparator<DataPoint> COMPARATOR_BY_X = (p1, p2) ->
            Long.compare(p1.getX(), p2.getX());

    private final long mX;
    private final long mY;

    public DataPoint(long x, long y) {
        mX = x;
        mY = y;
    }

    public long getX() {
        return mX;
    }

    public long getY() {
        return mY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        DataPoint point = (DataPoint) o;
        return mX == point.mX &&
               mY == point.mY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mX, mY);
    }

    @Nonnull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("(%d; %d)", mX, mY);
    }
}
