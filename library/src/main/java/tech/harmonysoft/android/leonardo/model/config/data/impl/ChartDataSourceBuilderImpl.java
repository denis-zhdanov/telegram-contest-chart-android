package tech.harmonysoft.android.leonardo.model.config.data.impl;

import tech.harmonysoft.android.leonardo.model.Range;
import tech.harmonysoft.android.leonardo.model.data.ChartDataLoader;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;
import tech.harmonysoft.android.leonardo.model.config.data.ChartDataSourceBuilder;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 11/3/19
 */
public class ChartDataSourceBuilderImpl implements ChartDataSourceBuilder {

    private long mMinX = Long.MIN_VALUE;
    private long mMaxX = Long.MAX_VALUE;

    private ChartDataLoader mLoader;
    private Integer         mColor;
    private String          mLegend;

    @Nonnull
    @Override
    public ChartDataSourceBuilder withMinX(long minX) {
        mMinX = minX;
        return this;
    }

    @Nonnull
    @Override
    public ChartDataSourceBuilder withMaxX(long maxX) {
        mMaxX = maxX;
        return this;
    }

    @Nonnull
    @Override
    public ChartDataSourceBuilder withColor(int color) {
        mColor = color;
        return this;
    }

    @Override
    public boolean hasColor() {
        return mColor != null;
    }

    @Nonnull
    @Override
    public ChartDataSourceBuilder withLegend(String legend) {
        mLegend = legend;
        return this;
    }

    @Nonnull
    @Override
    public ChartDataSourceBuilder withLoader(ChartDataLoader loader) {
        mLoader = loader;
        return this;
    }

    @Nonnull
    @Override
    public ChartDataSource build() {
        // Fixate values
        long minX = mMinX;
        long maxX = mMaxX;
        if (minX > maxX) {
            throw new IllegalStateException(String.format("Min X (%d) is greater than max X (%d)", minX, maxX));
        }

        String legend = mLegend;
        if (legend == null) {
            throw new IllegalStateException("Legend is undefined");
        }

        Integer color = mColor;
        if (color == null) {
            throw new IllegalStateException("Color is undefined");
        }

        ChartDataLoader loader = mLoader;
        if (loader == null) {
            throw new IllegalStateException("Data loader is undefined");
        }

        return new ChartDataSourceImpl(legend, new Range(minX, maxX), color, loader);
    }
}
