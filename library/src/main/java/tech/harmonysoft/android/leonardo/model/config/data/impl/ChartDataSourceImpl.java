package tech.harmonysoft.android.leonardo.model.config.data.impl;

import tech.harmonysoft.android.leonardo.model.Interval;
import tech.harmonysoft.android.leonardo.model.Range;
import tech.harmonysoft.android.leonardo.model.data.ChartDataLoader;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Denis Zhdanov
 * @since 11/3/19
 */
public class ChartDataSourceImpl implements ChartDataSource {

    private final String          mLegend;
    private final Range           mDataRange;
    private final ChartDataLoader mLoader;
    private final int             mColor;

    public ChartDataSourceImpl(String legend,
                               Range dataRange,
                               int color,
                               ChartDataLoader loader)
    {
        mLegend = legend;
        mDataRange = dataRange;
        mLoader = loader;
        mColor = color;
    }

    @Nonnull
    @Override
    public String getLegend() {
        return mLegend;
    }

    @Override
    public int getColor() {
        return mColor;
    }

    @Nonnull
    @Override
    public Range getDataRange() {
        return mDataRange;
    }

    @Nullable
    @Override
    public Interval load(Range range) {
        return mLoader.load(range);
    }

    @Override
    public String toString() {
        return mLegend;
    }
}
