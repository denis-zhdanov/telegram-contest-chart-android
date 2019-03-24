package tech.harmonysoft.android.leonardo.model.config.selector;

import android.content.Context;
import harmonysoft.tech.android.leonardo.R;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 18/3/19
 */
public interface ChartSelectorConfigBuilder {

    /**
     * <p>Specifies font size to use.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@link R.attr#leonardo_legend_selector_font_size} from its theme is used if defined.
     * </p>
     *
     * @param size     font size to use
     * @return         current builder
     */
    @Nonnull
    ChartSelectorConfigBuilder withFontSizeInPixels(int size);

    /**
     * <p>Specifies legend text color to use.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@link R.attr#leonardo_chart_legend_text_title_color} from its theme is used if defined.
     * </p>
     *
     * @param color     legend text color to use
     * @return          current builder
     */
    @Nonnull
    ChartSelectorConfigBuilder withLegendTextColor(int color);

    /**
     * <p>Specifies background color to use.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@link R.attr#leonardo_legend_selector_background_color} from its theme is used if defined.
     * </p>
     *
     * @param color     background color to use
     * @return          current builder
     */
    @Nonnull
    ChartSelectorConfigBuilder withBackgroundColor(int color);

    /**
     * <p>Specifies row separator color to use.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@link R.attr#leonardo_legend_selector_row_separator_color} from its theme is used if defined.
     * </p>
     *
     * @param color     row separator color to use
     * @return          current builder
     */
    @Nonnull
    ChartSelectorConfigBuilder withRowSeparatorColor(int color);

    /**
     * <p>Specifies check sign color to use.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@link R.attr#leonardo_legend_selector_check_sign_color} from its theme is used if defined.
     * </p>
     *
     * @param color     check sign color to use
     * @return          current builder
     */
    @Nonnull
    ChartSelectorConfigBuilder withCheckSignColor(int color);

    /**
     * Applies default values to various graphic elements obtained from the given context's
     * {@link Context#getTheme() theme}.
     *
     * @param context   theme holder which defaults should be applied unless explicitly specified
     * @return          current builder
     */
    @Nonnull
    ChartSelectorConfigBuilder withContext(Context context);

    @Nonnull
    ChartSelectorConfig build() throws IllegalStateException;
}
