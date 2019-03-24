package tech.harmonysoft.android.leonardo.model.config.axis.impl;

import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfig;
import tech.harmonysoft.android.leonardo.model.axis.AxisLabelTextStrategy;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public class AxisConfigImpl implements AxisConfig {

    private final AxisLabelTextStrategy mLabelTextStrategy;
    private final float                 mFontSizeInPixels;
    private final int                   mLabelColor;
    private final boolean               mDrawLabels;
    private final boolean               mDrawAxis;

    public AxisConfigImpl(AxisLabelTextStrategy labelTextStrategy,
                          float fontSizeInPixels,
                          int color,
                          boolean drawLabels,
                          boolean drawAxis)
    {
        mLabelTextStrategy = labelTextStrategy;
        mFontSizeInPixels = fontSizeInPixels;
        mLabelColor = color;
        mDrawLabels = drawLabels;
        mDrawAxis = drawAxis;
    }

    @Override
    public boolean shouldDrawLabels() {
        return mDrawLabels;
    }

    @Override
    public boolean shouldDrawAxis() {
        return mDrawAxis;
    }

    @Nonnull
    @Override
    public AxisLabelTextStrategy getLabelTextStrategy() {
        return mLabelTextStrategy;
    }

    @Override
    public float getFontSizeInPixels() {
        return mFontSizeInPixels;
    }

    @Override
    public int getLabelColor() {
        return mLabelColor;
    }

    @Nonnull
    @Override
    public String toString() {
        return "labelTextStrategy = " + mLabelTextStrategy
               + ", fontSizeInPixels = " + mFontSizeInPixels
               + ", labelColor = " + mLabelColor
               + ", drawLabels = " + mDrawLabels
               + ", drawAxis = " + mDrawAxis;
    }
}
