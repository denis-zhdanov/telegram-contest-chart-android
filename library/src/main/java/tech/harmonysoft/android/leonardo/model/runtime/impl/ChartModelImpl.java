package tech.harmonysoft.android.leonardo.model.runtime.impl;

import tech.harmonysoft.android.leonardo.log.LogUtil;
import tech.harmonysoft.android.leonardo.model.DataPoint;
import tech.harmonysoft.android.leonardo.model.Interval;
import tech.harmonysoft.android.leonardo.model.Range;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;
import tech.harmonysoft.android.leonardo.model.runtime.ChartModel;
import tech.harmonysoft.android.leonardo.model.runtime.ChartModelListener;
import tech.harmonysoft.android.leonardo.util.RangesList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Denis Zhdanov
 * @since 13/3/19
 */
public class ChartModelImpl implements ChartModel {

    private final Collection<ChartModelListener> mListeners = new ArrayList<>();

    private final Map<ChartDataSource, NavigableSet<DataPoint>> mPoints              = new HashMap<>();
    private final Map<ChartDataSource, RangesList>              mLoadedRanges        = new HashMap<>();
    private final Map<Object, Range>                            mActiveRanges        = new HashMap<>();
    private final Set<ChartDataSource>                          mDisabledDataSources = new HashSet<>();

    private final int mBufferPagesCount;

    private Range   mCompoundActiveRange;
    private Range   mBufferRange;
    private boolean mHasSelection;
    private long    mSelectedX;

    /**
     * @param bufferPagesCount number of chart data pages to keep in memory. E.g. if {@code 1} is returned,
     *                         then the chart would keep one page before the current interval and one page
     *                         after the current interval
     */
    public ChartModelImpl(int bufferPagesCount) {
        if (bufferPagesCount <= 0) {
            throw new IllegalArgumentException("Expected to get a positive buffer pages count but got "
                                               + bufferPagesCount);
        }
        mBufferPagesCount = bufferPagesCount;
        mCompoundActiveRange = Range.NO_RANGE;
        mBufferRange = Range.NO_RANGE;
    }

    @Override
    public boolean hasSelection() {
        return mHasSelection;
    }

    @Override
    public long getSelectedX() {
        if (!mHasSelection) {
            throw new IllegalStateException("Detected a call for a selection when there is no selection");
        }
        return mSelectedX;
    }

    @Override
    public void setSelectedX(long x) {
        if (mHasSelection && mSelectedX == x) {
            return;
        }
        mHasSelection = true;
        mSelectedX = x;
        notifyListeners(ChartModelListener::onSelectionChange);
    }

    @Override
    public void resetSelection() {
        mHasSelection = false;
        notifyListeners(ChartModelListener::onSelectionChange);
    }

    @Nonnull
    @Override
    public Range getActiveRange(Object anchor) {
        Range range = mActiveRanges.get(anchor);
        return range == null ? Range.NO_RANGE : range;
    }

    @Nonnull
    @Override
    public Range getBufferRange() {
        return mBufferRange;
    }

    @Override
    public void setActiveRange(Range range, Object anchor) {
        LogUtil.debug(this, "setActiveRange(): range=%s, anchor=%s", range, LogUtil.getMarker(anchor));

        mActiveRanges.put(anchor, range);
        final Range newCompoundRange;
        if (mCompoundActiveRange == Range.NO_RANGE) {
            newCompoundRange = range;
        } else {
            long min = Long.MAX_VALUE;
            long max = Long.MIN_VALUE;
            for (Range value : mActiveRanges.values()) {
                min = Math.min(min, value.getStart());
                max = Math.max(max, value.getEnd());
            }
            newCompoundRange = new Range(min, max);
        }

        LogUtil.debug(this,
                      "setActiveRange(): new compound active range=%s, previous compound active range=%s",
                      newCompoundRange, mCompoundActiveRange);

        if (mCompoundActiveRange.equals(newCompoundRange)) {
            LogUtil.debug(this,
                          "setActiveRange(): new compound range is the same as before, skipping data change");
        } else {
            mCompoundActiveRange = newCompoundRange;
            refreshBufferRange();

            LogUtil.debug(this, "setActiveRange(): new buffer range=%s", mBufferRange);

            for (RangesList rangesList : mLoadedRanges.values()) {
                rangesList.keepOnly(mBufferRange);
            }
            for (NavigableSet<DataPoint> points : mPoints.values()) {
                points.tailSet(new DataPoint(mBufferRange.getEnd(), 0), false).clear();
                points.headSet(new DataPoint(mBufferRange.getStart(), 0), false).clear();
            }
        }

        notifyListeners(listener -> listener.onRangeChanged(anchor));
    }

    private void refreshBufferRange() {
        mBufferRange = new Range(
                mCompoundActiveRange.getStart() - mBufferPagesCount * mCompoundActiveRange.getPointsNumber(),
                mCompoundActiveRange.getEnd() + mBufferPagesCount * mCompoundActiveRange.getPointsNumber()
        );
    }

    @Override
    public boolean isActive(ChartDataSource dataSource) {
        return mPoints.containsKey(dataSource) && !mDisabledDataSources.contains(dataSource);
    }

    @Nonnull
    @Override
    public Collection<ChartDataSource> getRegisteredDataSources() {
        return new ArrayList<>(mPoints.keySet());
    }

    @Override
    public void addDataSource(ChartDataSource dataSource) {
        LogUtil.debug(this, "addDataSource(): %s", dataSource.getLegend());
        if (mPoints.containsKey(dataSource)) {
            throw new IllegalArgumentException(String.format(
                    "Data source '%s' is already registered (all registered data sources: %s)",
                    dataSource, mPoints.keySet()
            ));
        }
        mPoints.put(dataSource, new TreeSet<>(DataPoint.COMPARATOR_BY_X));
        mLoadedRanges.put(dataSource, new RangesList());
        notifyListeners(listener -> listener.onDataSourceAdded(dataSource));
    }

    @Override
    public void removeDataSource(ChartDataSource dataSource) {
        LogUtil.debug(this, "removeDataSource(): %s", dataSource.getLegend());
        if (!mPoints.containsKey(dataSource)) {
            throw new IllegalArgumentException(String.format("Data source '%s' is not registered. Registered: %s",
                                                             dataSource, mPoints.keySet()));
        }
        mPoints.remove(dataSource);
        mLoadedRanges.remove(dataSource);
        mActiveRanges.remove(dataSource);
        mDisabledDataSources.remove(dataSource);
        notifyListeners(listener -> listener.onDataSourceRemoved(dataSource));
    }

    @Override
    public void disableDataSource(ChartDataSource dataSource) {
        if (!mPoints.containsKey(dataSource)) {
            throw new IllegalArgumentException(String.format("Data source '%s' is not registered. Registered: %s",
                                                             dataSource, mPoints.keySet()));
        }
        boolean changed = mDisabledDataSources.add(dataSource);
        if (changed) {
            notifyListeners(listener -> listener.onDataSourceDisabled(dataSource));
        }
    }

    @Override
    public void enableDataSource(ChartDataSource dataSource) {
        if (!mPoints.containsKey(dataSource)) {
            throw new IllegalArgumentException(String.format("Data source '%s' is not registered. Registered: %s",
                                                             dataSource, mPoints.keySet()));
        }
        boolean changed = mDisabledDataSources.remove(dataSource);
        if (changed) {
            notifyListeners(listener -> listener.onDataSourceEnabled(dataSource));
        }
    }

    @Override
    public boolean arePointsForActiveRangeLoaded(ChartDataSource dataSource, Object anchor) {
        Range range = getActiveRange(anchor);
        if (range == Range.NO_RANGE) {
            return true;
        }
        RangesList rangesList = mLoadedRanges.get(dataSource);
        return rangesList != null && rangesList.contains(range);
    }

    @Nonnull
    @Override
    public Interval getCurrentRangePoints(ChartDataSource dataSource, Object anchor) {
        Range range = getActiveRange(anchor);
        if (range == Range.NO_RANGE) {
            return Interval.EMPTY;
        }
        NavigableSet<DataPoint> points = mPoints.get(dataSource);
        if (points == null) {
            return Interval.EMPTY;
        }
        return new Interval(points.headSet(new DataPoint(range.getEnd(), 0), true)
                                  .tailSet(new DataPoint(range.getStart(), 0), true));
    }

    @Nullable
    @Override
    public DataPoint getPreviousPointForActiveRange(ChartDataSource dataSource, Object anchor) {
        Range range = getActiveRange(anchor);
        if (range == Range.NO_RANGE) {
            return null;
        }
        NavigableSet<DataPoint> points = mPoints.get(dataSource);
        if (points == null) {
            return null;
        }
        DataPoint ceiling = points.ceiling(new DataPoint(range.getStart(), 0));
        if (ceiling == null || ceiling.getX() > range.getEnd()) {
            return null;
        }
        return points.lower(ceiling);
    }

    @Nullable
    @Override
    public DataPoint getNextPointForActiveRange(ChartDataSource dataSource, Object anchor) {
        Range range = getActiveRange(anchor);
        if (range == Range.NO_RANGE) {
            return null;
        }
        NavigableSet<DataPoint> points = mPoints.get(dataSource);
        if (points == null) {
            return null;
        }
        DataPoint floor = points.floor(new DataPoint(range.getEnd(), 0));
        if (floor == null || floor.getX() < range.getStart()) {
            return null;
        }
        return points.higher(floor);
    }

    @Nonnull
    @Override
    public RangesList getLoadedRanges(ChartDataSource dataSource) {
        RangesList result = mLoadedRanges.get(dataSource);
        if (result == null) {
            throw new IllegalArgumentException("No range info is found for data source " + dataSource);
        }
        return result;
    }

    @Override
    public void onPointsLoaded(ChartDataSource dataSource, Range range, Interval interval) {
        LogUtil.debug(this,
                      "onPointsLoaded(): source=%s, range=%s, interval=%s",
                      dataSource, range, interval);
        NavigableSet<DataPoint> points = mPoints.get(dataSource);
        RangesList rangesList = mLoadedRanges.get(dataSource);
        if (points == null || rangesList == null) {
            return;
        }

        if (interval.getMinX() > mBufferRange.getEnd() || interval.getMaxX() < mBufferRange.getStart()) {
            return;
        }

        Set<Object> anchorWithChangedActiveRange = new HashSet<>();
        for (DataPoint point : interval.getPoints()) {
            if (!mBufferRange.contains(point.getX())) {
                continue;
            }

            points.add(point);
            for (Map.Entry<Object, Range> entry : mActiveRanges.entrySet()) {
                if (entry.getValue().contains(point.getX())) {
                    anchorWithChangedActiveRange.add(entry.getKey());
                }
            }
        }

        rangesList.add(range);
        rangesList.keepOnly(mBufferRange);

        for (Object anchor : anchorWithChangedActiveRange) {
            notifyListeners(listener -> listener.onActiveDataPointsLoaded(anchor));
        }
    }

    @Override
    public void addListener(ChartModelListener listener) {
        mListeners.add(listener);
    }

    @Override
    public void removeListener(ChartModelListener listener) {
        mListeners.remove(listener);
    }

    private void notifyListeners(ListenerAction action) {
        for (ChartModelListener listener : mListeners) {
            action.doFor(listener);
        }
    }

    private interface ListenerAction {

        void doFor(ChartModelListener listener);
    }
}
