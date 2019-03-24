package tech.harmonysoft.android.leonardo.model.config.navigator.impl;

import android.content.Context;
import harmonysoft.tech.android.leonardo.R;
import tech.harmonysoft.android.leonardo.model.config.navigator.NavigatorConfig;
import tech.harmonysoft.android.leonardo.model.config.navigator.NavigatorConfigBuilder;

import javax.annotation.Nonnull;

import static tech.harmonysoft.android.leonardo.util.LeonardoUtil.getColor;
import static tech.harmonysoft.android.leonardo.util.LeonardoUtil.getDimensionSizeInPixels;

/**
 * @author Denis Zhdanov
 * @since 16/3/19
 */
public class NavigatorConfigBuilderImpl implements NavigatorConfigBuilder {

    private Integer mInactiveBackgroundColor;
    private Integer mActiveBorderColor;
    private Integer mActiveBorderHorizontalWidthInPixels;
    private Integer mActiveBorderVerticalHeightInPixels;
    private Integer mSelectionColor;
    private Context mContext;

    private int mViewMargin;
    private int mSelectionOutline;

    @Nonnull
    @Override
    public NavigatorConfigBuilder withInactiveBackgroundColor(int color) {
        mInactiveBackgroundColor = color;
        return this;
    }

    @Nonnull
    @Override
    public NavigatorConfigBuilder withActiveBorderColor(int color) {
        mActiveBorderColor = color;
        return this;
    }

    @Override
    public NavigatorConfigBuilder withContext(Context context) {
        mContext = context;
        return this;
    }

    @Nonnull
    @Override
    public NavigatorConfigBuilder withActiveBorderHorizontalWidthInPixels(int widthInPixels) {
        mActiveBorderHorizontalWidthInPixels = widthInPixels;
        return this;
    }

    @Nonnull
    @Override
    public NavigatorConfigBuilder withActiveBorderVerticalHeightInPixels(int heightInPixels) {
        mActiveBorderVerticalHeightInPixels = heightInPixels;
        return this;
    }

    @Nonnull
    @Override
    public NavigatorConfigBuilder withViewMargin(int marginInPixels) {
        mViewMargin = marginInPixels;
        return this;
    }

    @Nonnull
    @Override
    public NavigatorConfigBuilder withSelectionOutline(int outlineInPixels) {
        mSelectionOutline = outlineInPixels;
        return this;
    }

    @Nonnull
    @Override
    public NavigatorConfigBuilder withSelectionColor(int selectionColor) {
        mSelectionColor = selectionColor;
        return this;
    }

    @Nonnull
    @Override
    public NavigatorConfig build() throws IllegalStateException {
        int inactiveBackgroundColor = getColor("Navigator chart background",
                                               R.attr.leonardo_navigator_chart_background_inactive,
                                               mInactiveBackgroundColor,
                                               mContext);

        int activeBorderColor = getColor("Navigator chart active border color",
                                         R.attr.leonardo_navigator_chart_active_border_color,
                                         mActiveBorderColor,
                                         mContext);

        int activeBorderHorizontalWidth = getDimensionSizeInPixels(
                "Navigator chart active border width",
                R.attr.leonardo_navigator_chart_active_border_horizontal_size,
                mActiveBorderHorizontalWidthInPixels,
                mContext);

        int activeBorderVerticalHeight = getDimensionSizeInPixels(
                "Navigator chart active border height",
                R.attr.leonardo_navigator_chart_active_border_vertical_size,
                mActiveBorderVerticalHeightInPixels,
                mContext);

        int selectionColor = getColor("Selection color",
                                      R.attr.leonardo_navigator_chart_selection_color,
                                      mSelectionColor,
                                      mContext);

        return new NavigatorConfigImpl(inactiveBackgroundColor,
                                       activeBorderColor,
                                       activeBorderHorizontalWidth,
                                       activeBorderVerticalHeight,
                                       mViewMargin,
                                       mSelectionOutline,
                                       selectionColor);
    }
}
