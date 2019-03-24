package tech.harmonysoft.android.leonardo.util;

import tech.harmonysoft.android.leonardo.model.Range;

import javax.annotation.Nonnull;
import java.util.*;

/**
 * @author Denis Zhdanov
 * @since 13/3/19
 */
public class RangesList {

    private static final Comparator<Range> COMPARATOR = (r1, r2) -> Long.compare(r1.getStart(), r2.getStart());

    private final List<Range> mRanges = new ArrayList<>();

    public void add(Range range) {
        int i = Collections.binarySearch(mRanges, range, COMPARATOR);
        if (i >= 0) {
            Range r = mRanges.get(i);
            if (r.getEnd() >= range.getEnd()) {
                return;
            }
            mRanges.set(i, range);
            mayBeMerge(i);
            return;
        }

        i = -(i + 1);
        mRanges.add(i, range);
        if (i > 0) {
            mayBeMerge(i - 1);
        } else {
            mayBeMerge(i);
        }
    }

    private void mayBeMerge(int i) {
        Range previous = mRanges.get(i++);
        while (i < mRanges.size()) {
            Range next = mRanges.get(i);
            if (previous.getEnd() < next.getStart() - 1) {
                // Disjoint
                return;
            }
            mRanges.set(i - 1, previous = new Range(previous.getStart(), Math.max(previous.getEnd(), next.getEnd())));
            mRanges.remove(i);
        }
    }

    public boolean contains(Range range) {
        int i = Collections.binarySearch(mRanges, range, COMPARATOR);
        if (i >= 0) {
            return mRanges.get(i).getEnd() >= range.getEnd();
        }
        i = -(i + 1);
        return i > 0 && mRanges.get(i - 1).getEnd() >= range.getEnd();
    }

    public void keepOnly(Range range) {
        List<Range> toKeep = new ArrayList<>();
        int i = Collections.binarySearch(mRanges, range, COMPARATOR);
        if (i < 0) {
            i = -(i + 1);
            if (i > 0) {
                Range previous = mRanges.get(i - 1);
                if (previous.getEnd() >= range.getStart()) {
                    toKeep.add(new Range(range.getStart(), Math.min(previous.getEnd(), range.getEnd())));
                }
            }
        }
        for (; i < mRanges.size(); i++) {
            Range r = mRanges.get(i);
            if (r.getEnd() < range.getEnd()) {
                toKeep.add(r);
            } else {
                toKeep.add(new Range(r.getStart(), range.getEnd()));
                break;
            }
        }
        mRanges.clear();
        mRanges.addAll(toKeep);
    }

    @Nonnull
    public Collection<Range> getMissing(Range target) {
        Collection<Range> result = new ArrayList<>();
        int i = Collections.binarySearch(mRanges, target, COMPARATOR);
        if (i < 0) {
            i = -(i + 1);
            if (i > 0) {
                i--;
            }
        }

        long targetStart = target.getStart();
        for (; i < mRanges.size(); i++) {
            Range range = mRanges.get(i);
            if (range.getStart() > target.getEnd()) {
                break;
            }

            if (range.getStart() > targetStart) {
                result.add(new Range(targetStart, range.getStart() - 1));
            }
            targetStart = range.getEnd() + 1;
        }

        if (targetStart <= target.getEnd()) {
            result.add(new Range(targetStart, target.getEnd()));
        }
        return result;
    }

    @Nonnull
    public List<Range> getRanges() {
        return mRanges;
    }

    @Nonnull
    @Override
    public String toString() {
        return "ranges = " + mRanges;
    }
}
