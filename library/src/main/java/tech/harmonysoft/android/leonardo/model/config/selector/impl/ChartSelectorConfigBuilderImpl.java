package tech.harmonysoft.android.leonardo.model.config.selector.impl;

import android.content.Context;
import harmonysoft.tech.android.leonardo.R;
import tech.harmonysoft.android.leonardo.model.config.selector.ChartSelectorConfig;
import tech.harmonysoft.android.leonardo.model.config.selector.ChartSelectorConfigBuilder;

import javax.annotation.Nonnull;

import static tech.harmonysoft.android.leonardo.util.LeonardoUtil.getColor;
import static tech.harmonysoft.android.leonardo.util.LeonardoUtil.getDimensionSizeInPixels;

/**
 * @author Denis Zhdanov
 * @since 18/3/19
 */
public class ChartSelectorConfigBuilderImpl implements ChartSelectorConfigBuilder {

    private Integer mFontSizeInPixels;
    private Integer mLegendTextColor;
    private Integer mBackgroundColor;
    private Integer mRowSeparatorColor;
    private Integer mCheckSignColor;
    private Context mContext;

    @Nonnull
    @Override
    public ChartSelectorConfigBuilder withFontSizeInPixels(int size) {
        mFontSizeInPixels = size;
        return this;
    }

    @Nonnull
    @Override
    public ChartSelectorConfigBuilder withLegendTextColor(int color) {
        mLegendTextColor = color;
        return this;
    }

    @Nonnull
    @Override
    public ChartSelectorConfigBuilder withBackgroundColor(int color) {
        mBackgroundColor = color;
        return this;
    }

    @Nonnull
    @Override
    public ChartSelectorConfigBuilder withRowSeparatorColor(int color) {
        mRowSeparatorColor = color;
        return this;
    }

    @Nonnull
    @Override
    public ChartSelectorConfigBuilder withCheckSignColor(int color) {
        mCheckSignColor = color;
        return this;
    }

    @Nonnull
    @Override
    public ChartSelectorConfigBuilder withContext(Context context) {
        mContext = context;
        return this;
    }

    @Nonnull
    @Override
    public ChartSelectorConfig build() throws IllegalStateException {
        int fontSize = getDimensionSizeInPixels("Font size",
                                                        R.attr.leonardo_legend_selector_font_size,
                                                        mFontSizeInPixels,
                                                        mContext);

        int legendTextColor = getColor("Legend text color",
                                       R.attr.leonardo_legend_selector_legend_text_color,
                                       mLegendTextColor,
                                       mContext);

        int backgroundColor = getColor("Background color",
                                       R.attr.leonardo_legend_selector_background_color,
                                       mBackgroundColor,
                                       mContext);

        int rowSeparatorColor = getColor("Row separator color",
                                         R.attr.leonardo_legend_selector_row_separator_color,
                                         mRowSeparatorColor,
                                         mContext);

        int checkSignColor = getColor("Check sign color",
                                      R.attr.leonardo_legend_selector_check_sign_color,
                                      mCheckSignColor,
                                      mContext);

        return new ChartSelectorConfigImpl(fontSize,
                                           legendTextColor,
                                           backgroundColor, rowSeparatorColor, checkSignColor);
    }
}
