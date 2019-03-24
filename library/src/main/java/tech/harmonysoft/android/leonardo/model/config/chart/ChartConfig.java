package tech.harmonysoft.android.leonardo.model.config.chart;

import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfig;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public interface ChartConfig {

    @Nonnull
    AxisConfig getXAxisConfig();

    @Nonnull
    AxisConfig getYAxisConfig();

    int getInsetsInPixels();

    int getBackgroundColor();

    int getGridColor();

    int getGridLineWidthInPixels();

    int getPlotLineWidthInPixels();

    int getSelectionSignRadiusInPixels();

    int getLegendTextTitleColor();

    int getLegendBackgroundColor();

    boolean isSelectionAllowed();

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    boolean isDrawSelection();

    boolean isDrawBackground();

    boolean isAnimationEnabled();
}
