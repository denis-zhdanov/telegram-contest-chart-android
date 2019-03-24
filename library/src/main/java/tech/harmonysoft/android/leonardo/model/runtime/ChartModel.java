package tech.harmonysoft.android.leonardo.model.runtime;

import tech.harmonysoft.android.leonardo.model.Interval;
import tech.harmonysoft.android.leonardo.model.DataPoint;
import tech.harmonysoft.android.leonardo.model.Range;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;
import tech.harmonysoft.android.leonardo.util.RangesList;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;

/**
 * All methods are assumed to be called from UI thread.
 *
 * @author Denis Zhdanov
 * @since 13/3/19
 */
public interface ChartModel {

    boolean hasSelection();
    long getSelectedX();
    void setSelectedX(long x);
    void resetSelection();

    @Nonnull
    Range getActiveRange(Object anchor);
    @Nonnull
    Range getBufferRange();
    void setActiveRange(Range range, Object anchor);

    boolean isActive(ChartDataSource dataSource);
    @Nonnull
    Collection<ChartDataSource> getRegisteredDataSources();
    void addDataSource(ChartDataSource dataSource);
    void disableDataSource(ChartDataSource dataSource);
    void enableDataSource(ChartDataSource dataSource);
    void removeDataSource(ChartDataSource dataSource);

    boolean arePointsForActiveRangeLoaded(ChartDataSource dataSource, Object anchor);
    @Nonnull
    Interval getCurrentRangePoints(ChartDataSource dataSource, Object anchor);
    @Nullable
    DataPoint getPreviousPointForActiveRange(ChartDataSource dataSource, Object anchor);
    @Nullable
    DataPoint getNextPointForActiveRange(ChartDataSource dataSource, Object anchor);
    @Nonnull
    RangesList getLoadedRanges(ChartDataSource dataSource);
    void onPointsLoaded(ChartDataSource dataSource, Range range, Interval interval);

    void addListener(ChartModelListener listener);
    void removeListener(ChartModelListener listener);
}
