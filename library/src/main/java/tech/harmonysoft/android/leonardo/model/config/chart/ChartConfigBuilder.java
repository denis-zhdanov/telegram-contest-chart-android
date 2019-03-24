package tech.harmonysoft.android.leonardo.model.config.chart;

import android.content.Context;
import harmonysoft.tech.android.leonardo.R;
import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfig;
import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfigBuilder;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public interface ChartConfigBuilder {

    @Nonnull
    ChartConfigBuilder withXAxisConfig(AxisConfig config);

    @Nonnull
    ChartConfigBuilder withXAxisConfigBuilder(AxisConfigBuilder builder);

    @Nonnull
    ChartConfigBuilder withYAxisConfig(AxisConfig config);

    @Nonnull
    ChartConfigBuilder withYAxisConfigBuilder(AxisConfigBuilder builder);

    @Nonnull
    ChartConfigBuilder disableBackground();

    /**
     * <p>Specifies background color to use.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@link R.attr#leonardo_chart_background_color} from its theme is used if defined.
     * </p>
     *
     * @param color     background color to use
     * @return          current builder
     */
    @Nonnull
    ChartConfigBuilder withBackgroundColor(int color);

    /**
     * <p>Custom grid lines width in pixels to use.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     the {@link R.attr#leonardo_chart_grid_width} is used if defined.
     * </p>
     *
     * @param widthInPixels     grid line width in pixels to use
     * @return                  current builder
     */
    @Nonnull
    ChartConfigBuilder withGridLineWidthInPixels(int widthInPixels);

    /**
     * <p>Specifies color to use for drawing grid and axis.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@link R.attr#leonardo_chart_grid_color} from its theme is used.
     * </p>
     *
     * @param color     background color to use
     * @return          current builder
     */
    @Nonnull
    ChartConfigBuilder withGridColor(int color);

    /**
     * <p>Specifies chart insets in pixels.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@link R.attr#leonardo_chart_insets} from its theme is used.
     * </p>
     *
     * @param insets    insets in pixels to use
     * @return          current builder
     */
    ChartConfigBuilder withInsetsInPixels(int insets);

    /**
     * <p>Custom plot lines width in pixels to use.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     the {@link R.attr#leonardo_chart_plot_width} is used if defined.
     * </p>
     *
     * @param widthInPixels     plot line width in pixels to use
     * @return                  current builder
     */
    @Nonnull
    ChartConfigBuilder withPlotLineWidthInPixels(int widthInPixels);

    @Nonnull
    ChartConfigBuilder disableSelection();

    /**
     * <p>Custom selection sign radius in pixels to use.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     the {@link R.attr#leonardo_chart_selection_sign_radius} is used if defined.
     * </p>
     *
     * @param radiusInPixels    selection sign radius in pixels to use
     * @return                  current builder
     */
    @Nonnull
    ChartConfigBuilder withSelectionSignRadiusInPixels(int radiusInPixels);

    /**
     * <p>Specifies color to use for drawing legend title.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@link R.attr#leonardo_chart_legend_text_title_color} from its theme is used.
     * </p>
     *
     * @param color     legend text title color to use
     * @return          current builder
     */
    @Nonnull
    ChartConfigBuilder withLegendTextTitleColor(int color);

    /**
     * <p>Specifies color to use for drawing legend background.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@link R.attr#leonardo_chart_legend_background_color} from its theme is used.
     * </p>
     *
     * @param color     legend background color to use
     * @return          current builder
     */
    @Nonnull
    ChartConfigBuilder withLegendBackgroundColor(int color);

    @Nonnull
    ChartConfigBuilder disableAnimations();

    /**
     * Applies default values to various graphic elements obtained from the given context's
     * {@link Context#getTheme() theme}.
     *
     * @param context   theme holder which defaults should be applied unless explicitly specified
     * @return          current builder
     */
    @Nonnull
    ChartConfigBuilder withContext(Context context);

    @Nonnull
    ChartConfigBuilder withConfig(ChartConfig config);

    @Nonnull
    ChartConfig build() throws IllegalStateException;
}
