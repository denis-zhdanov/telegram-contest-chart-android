package tech.harmonysoft.android.leonardo.model;

import android.annotation.SuppressLint;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public class Interval {

    public static final Interval EMPTY = new Interval(Collections.emptyList());

    private final List<DataPoint> mPoints = new ArrayList<>();

    private final long mMinX;
    private final long mMaxX;
    private final long mMinY;
    private final long mMaxY;

    /**
     * @param points    assumed to be sorted
     */
    public Interval(Collection<DataPoint> points) {
        mPoints.addAll(points);

        long minX = Integer.MAX_VALUE;
        long minY = Integer.MAX_VALUE;
        long maxX = Integer.MIN_VALUE;
        long maxY = Integer.MIN_VALUE;

        for (int i = 0; i < mPoints.size(); i++) {
            DataPoint point = mPoints.get(i);
            minX = Math.min(minX, point.getX());
            maxX = Math.max(maxX, point.getX());
            minY = Math.min(minY, point.getY());
            maxY = Math.max(maxY, point.getY());
            if (i > 0) {
                DataPoint prevPoint = mPoints.get(i - 1);
                if (point.getX() == prevPoint.getX()) {
                    throw new IllegalArgumentException(String.format(
                            "Failed to build and interval - multiple points with the same 'x=%d' are found: "
                            + "'%s' and '%s'. All points: %s", point.getX(), point, prevPoint, mPoints
                    ));
                }
            }
        }

        mMinX = minX;
        mMaxX = maxX;
        mMinY = minY;
        mMaxY = maxY;
    }

    public long getMinX() {
        return mMinX;
    }

    public long getMaxX() {
        return mMaxX;
    }

    @Nonnull
    public List<DataPoint> getPoints() {
        return mPoints;
    }

    @Nonnull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%d points: X=[%d; %d], Y=[%d; %d]", mPoints.size(), mMinX, mMaxX, mMinY, mMaxY);
    }
}
