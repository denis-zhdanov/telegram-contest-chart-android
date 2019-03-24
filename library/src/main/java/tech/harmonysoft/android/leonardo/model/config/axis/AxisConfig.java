package tech.harmonysoft.android.leonardo.model.config.axis;

import tech.harmonysoft.android.leonardo.model.axis.AxisLabelTextStrategy;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public interface AxisConfig {

    boolean shouldDrawAxis();

    /**
     * @return      {@code true} if target axis labels should be drawn; the labels are drawn by default
     */
    boolean shouldDrawLabels();

    /**
     * @return      a value-text strategy to use
     */
    @Nonnull
    AxisLabelTextStrategy getLabelTextStrategy();

    float getFontSizeInPixels();

    int getLabelColor();
}
