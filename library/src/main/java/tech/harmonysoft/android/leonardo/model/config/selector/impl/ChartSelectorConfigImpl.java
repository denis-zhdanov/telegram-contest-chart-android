package tech.harmonysoft.android.leonardo.model.config.selector.impl;

import tech.harmonysoft.android.leonardo.model.config.selector.ChartSelectorConfig;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 18/3/19
 */
public class ChartSelectorConfigImpl implements ChartSelectorConfig {

    private final int mFontSize;
    private final int mLegendTextColor;
    private final int mBackgroundColor;
    private final int mRowSeparatorColor;
    private final int mCheckSignColor;

    public ChartSelectorConfigImpl(int fontSize,
                                   int legendTextColor,
                                   int backgroundColor,
                                   int rowSeparatorColor,
                                   int checkSignColor)
    {
        mFontSize = fontSize;
        mLegendTextColor = legendTextColor;
        mBackgroundColor = backgroundColor;
        mRowSeparatorColor = rowSeparatorColor;
        mCheckSignColor = checkSignColor;
    }

    @Override
    public int getFontSize() {
        return mFontSize;
    }

    @Override
    public int getLegendTextColor() {
        return mLegendTextColor;
    }

    @Override
    public int getBackgroundColor() {
        return mBackgroundColor;
    }

    @Override
    public int getRowSeparatorColor() {
        return mRowSeparatorColor;
    }

    @Override
    public int getCheckSignColor() {
        return mCheckSignColor;
    }

    @Nonnull
    @Override
    public String toString() {
        return "fontSize = " + mFontSize
               + ", legendTextColor = " + mLegendTextColor
               + ", backgroundColor = " + mBackgroundColor
               + ", rowSeparatorColor = " + mRowSeparatorColor
               + ", checkSignColor = " + mCheckSignColor;
    }
}
