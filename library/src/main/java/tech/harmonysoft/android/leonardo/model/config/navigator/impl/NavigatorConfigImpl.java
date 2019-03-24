package tech.harmonysoft.android.leonardo.model.config.navigator.impl;

import tech.harmonysoft.android.leonardo.model.config.navigator.NavigatorConfig;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 16/3/19
 */
public class NavigatorConfigImpl implements NavigatorConfig {

    private final int mInactiveChartBackgroundColor;
    private final int mActiveBorderColor;
    private final int mActiveBorderHorizontalWidthInPixels;
    private final int mActiveBorderVerticalHeightInPixels;
    private final int mViewMargin;
    private final int mSelectionOutline;
    private final int mSelectionColor;

    public NavigatorConfigImpl(int inactiveChartBackgroundColor,
                               int activeBorderColor,
                               int activeBorderHorizontalWidthInPixels,
                               int activeBorderVerticalHeightInPixels,
                               int viewMargin,
                               int selectionOutline,
                               int selectionColor)
    {
        mInactiveChartBackgroundColor = inactiveChartBackgroundColor;
        mActiveBorderColor = activeBorderColor;
        mActiveBorderHorizontalWidthInPixels = activeBorderHorizontalWidthInPixels;
        mActiveBorderVerticalHeightInPixels = activeBorderVerticalHeightInPixels;
        mViewMargin = viewMargin;
        mSelectionOutline = selectionOutline;
        mSelectionColor = selectionColor;
    }

    @Override
    public int getInactiveChartBackgroundColor() {
        return mInactiveChartBackgroundColor;
    }

    @Override
    public int getActiveBorderColor() {
        return mActiveBorderColor;
    }

    @Override
    public int getActiveBorderHorizontalWidthInPixels() {
        return mActiveBorderHorizontalWidthInPixels;
    }

    @Override
    public int getActiveBorderVerticalHeightInPixels() {
        return mActiveBorderVerticalHeightInPixels;
    }

    @Override
    public int getViewMargin() {
        return mViewMargin;
    }

    @Override
    public int getSelectionOutline() {
        return mSelectionOutline;
    }

    @Override
    public int getSelectionColor() {
        return mSelectionColor;
    }

    @Nonnull
    @Override
    public String toString() {
        return "inactiveChartBackgroundColor = " + mInactiveChartBackgroundColor
               + ", activeBorderColor = " + mActiveBorderColor
               + ", activeBorderHorizontalWidth = " + mActiveBorderHorizontalWidthInPixels
               + ", activeBorderVerticalHeight = " + mActiveBorderVerticalHeightInPixels
               + ", viewMargin = " + mViewMargin
               + ", selectionOutline = " + mSelectionOutline
               + ", selectionColor = " + mSelectionColor;
    }
}
