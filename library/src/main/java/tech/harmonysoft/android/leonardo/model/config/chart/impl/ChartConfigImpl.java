package tech.harmonysoft.android.leonardo.model.config.chart.impl;

import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfig;
import tech.harmonysoft.android.leonardo.model.config.chart.ChartConfig;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public class ChartConfigImpl implements ChartConfig {

    private final AxisConfig mXAxisConfig;
    private final AxisConfig mYAxisConfig;
    private final int        mBackgroundColor;
    private final boolean    mDrawBackground;
    private final int        mGridColor;
    private final int        mGridLineWidthInPixels;
    private final int        mInsetsInPixels;
    private final int        mPlotLineWidthInPixels;
    private final int        mSelectionSignRadiusInPixels;
    private final int        mLegendTextTitleColor;
    private final int        mLegendBackgroundColor;
    private final boolean    mDrawSelection;
    private final boolean    mSelectionAllowed;
    private final boolean    mAnimationEnabled;

    ChartConfigImpl(AxisConfig xAxisConfig,
                    AxisConfig yAxisConfig,
                    boolean drawBackground,
                    int gridLineWidthInPixels,
                    int backgroundColor,
                    int gridColor,
                    int insetsInPixels,
                    int plotLineWidthInPixels,
                    int selectionSignRadiusInPixels,
                    int legendTextTitleColor,
                    int legendBackgroundColor,
                    boolean drawSelection,
                    boolean selectionAllowed,
                    boolean animationEnabled)
    {
        mDrawBackground = drawBackground;
        mInsetsInPixels = insetsInPixels;
        mPlotLineWidthInPixels = plotLineWidthInPixels;
        mSelectionSignRadiusInPixels = selectionSignRadiusInPixels;
        mXAxisConfig = xAxisConfig;
        mYAxisConfig = yAxisConfig;
        mGridLineWidthInPixels = gridLineWidthInPixels;
        mBackgroundColor = backgroundColor;
        mGridColor = gridColor;
        mLegendTextTitleColor = legendTextTitleColor;
        mLegendBackgroundColor = legendBackgroundColor;
        mDrawSelection = drawSelection;
        mSelectionAllowed = selectionAllowed;
        mAnimationEnabled = animationEnabled;
    }

    @Override
    @Nonnull
    public AxisConfig getXAxisConfig() {
        return mXAxisConfig;
    }

    @Override
    @Nonnull
    public AxisConfig getYAxisConfig() {
        return mYAxisConfig;
    }

    @Override
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    @Override
    public int getGridColor() {
        return mGridColor;
    }

    @Override
    public int getGridLineWidthInPixels() {
        return mGridLineWidthInPixels;
    }

    @Override
    public int getInsetsInPixels() {
        return mInsetsInPixels;
    }

    @Override
    public int getPlotLineWidthInPixels() {
        return mPlotLineWidthInPixels;
    }

    @Override
    public int getSelectionSignRadiusInPixels() {
        return mSelectionSignRadiusInPixels;
    }

    @Override
    public int getLegendTextTitleColor() {
        return mLegendTextTitleColor;
    }

    @Override
    public int getLegendBackgroundColor() {
        return mLegendBackgroundColor;
    }

    @Override
    public boolean isDrawSelection() {
        return mDrawSelection;
    }

    @Override
    public boolean isDrawBackground() {
        return mDrawBackground;
    }

    @Override
    public boolean isSelectionAllowed() {
        return mSelectionAllowed;
    }

    @Override
    public boolean isAnimationEnabled() {
        return mAnimationEnabled;
    }

    @Nonnull
    @Override
    public String toString() {
        return "xAxisConfig = " + mXAxisConfig
               + ", yAxisConfig = " + mYAxisConfig
               + ", drawBackground = " + mDrawBackground
               + ", backgroundColor = " + mBackgroundColor
               + ", gridColor = " + mGridColor
               + ", gridWidthInPixels = " + mGridLineWidthInPixels
               + ", insetsInPixels = " + mInsetsInPixels
               + ", plotLineWidthInPixels = " + mPlotLineWidthInPixels
               + ", selectionSignRadiusInPixels = " + mSelectionSignRadiusInPixels
               + ", legendTextTitleColor = " + mLegendTextTitleColor
               + ", legendBackgroundColor = " + mLegendBackgroundColor
               + ", drawSelection = " + mDrawSelection
               + ", selectionAllowed = " + mSelectionAllowed
               + ", animationEnabled = " + mAnimationEnabled;
    }
}
