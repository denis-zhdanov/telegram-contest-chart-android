package tech.harmonysoft.android.leonardo.model.config.chart.impl;

import android.content.Context;
import harmonysoft.tech.android.leonardo.R;
import tech.harmonysoft.android.leonardo.model.config.LeonardoConfigFactory;
import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfig;
import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfigBuilder;
import tech.harmonysoft.android.leonardo.model.config.chart.ChartConfig;
import tech.harmonysoft.android.leonardo.model.config.chart.ChartConfigBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import static tech.harmonysoft.android.leonardo.util.LeonardoUtil.getColor;
import static tech.harmonysoft.android.leonardo.util.LeonardoUtil.getDimensionSizeInPixels;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public class ChartConfigBuilderImpl implements ChartConfigBuilder {

    private boolean mDrawSelection    = true;
    private boolean mAllowSelection   = true;
    private boolean mDrawBackground   = true;
    private boolean mEnableAnimations = true;

    private Integer           mBackgroundColor;
    private Integer           mGridColor;
    private AxisConfig        mXAxisConfig;
    private AxisConfigBuilder mXAxisConfigBuilder;
    private AxisConfig        mYAxisConfig;
    private AxisConfigBuilder mYAxisConfigBuilder;
    private Integer           mGridLineWidthInPixels;
    private Integer           mInsetsInPixels;
    private Context           mContext;
    private Integer           mPlotLineWidthInPixels;
    private Integer           mSelectionSignRadiusInPixels;
    private Integer           mLegendTitleColor;
    private Integer           mLegendBackgroundColor;

    @Nonnull
    @Override
    public ChartConfigBuilder withXAxisConfig(AxisConfig config) {
        AxisConfigBuilder configBuilder = mXAxisConfigBuilder;
        if (configBuilder != null) {
            throw new IllegalStateException(String.format(
                    "Detected an overlapping X axis setup - either config (%s) or config builder (%s) are provided",
                    config, configBuilder
            ));
        }
        mXAxisConfig = config;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withXAxisConfigBuilder(AxisConfigBuilder builder) {
        AxisConfig config = mXAxisConfig;
        if (config != null) {
            throw new IllegalStateException(String.format(
                    "Detected an overlapping X axis setup - either config (%s) or config builder (%s) are provided",
                    config, builder
            ));
        }
        mXAxisConfigBuilder = builder;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withYAxisConfig(AxisConfig config) {
        AxisConfigBuilder builder = mYAxisConfigBuilder;
        if (builder != null) {
            throw new IllegalStateException(String.format(
                    "Detected an overlapping Y axis setup - either config (%s) or config builder (%s) are provided",
                    config, builder
            ));
        }
        mYAxisConfig = config;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withYAxisConfigBuilder(AxisConfigBuilder builder) {
        AxisConfig config = mYAxisConfig;
        if (config != null) {
            throw new IllegalStateException(String.format(
                    "Detected an overlapping Y axis setup - either config (%s) or config builder (%s) are provided",
                    config, builder
            ));
        }
        mYAxisConfigBuilder = builder;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withGridLineWidthInPixels(int widthInPixels) {
        if (widthInPixels < 0) {
            throw new IllegalArgumentException("Can't use negative chart grid width - " + widthInPixels);
        }
        mGridLineWidthInPixels = widthInPixels;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder disableBackground() {
        mDrawBackground = false;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withBackgroundColor(int color) {
        mBackgroundColor = color;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withGridColor(int color) {
        mGridColor = color;
        return this;
    }

    @Override
    public ChartConfigBuilder withInsetsInPixels(int insets) {
        if (insets < 0) {
            throw new IllegalArgumentException("Can't use negative insets - " + insets);
        }
        mInsetsInPixels = insets;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder disableSelection() {
        mDrawSelection = false;
        mAllowSelection = false;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withSelectionSignRadiusInPixels(int radiusInPixels) {
        if (radiusInPixels <= 0) {
            throw new IllegalArgumentException("Can't use non-positive selection sign radius in pixels - "
                                               + radiusInPixels);
        }
        mSelectionSignRadiusInPixels = radiusInPixels;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withLegendTextTitleColor(int color) {
        mLegendTitleColor = color;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withLegendBackgroundColor(int color) {
        mLegendBackgroundColor = color;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder disableAnimations() {
        mEnableAnimations = false;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withContext(Context context) {
        mContext = context;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withConfig(ChartConfig config) {
        ChartConfigBuilder result = withXAxisConfig(config.getXAxisConfig())
                .withYAxisConfig(config.getYAxisConfig())
                .withBackgroundColor(config.getBackgroundColor())
                .withGridLineWidthInPixels(config.getGridLineWidthInPixels())
                .withGridColor(config.getGridColor())
                .withInsetsInPixels(config.getInsetsInPixels())
                .withPlotLineWidthInPixels(config.getPlotLineWidthInPixels())
                .withSelectionSignRadiusInPixels(config.getSelectionSignRadiusInPixels())
                .withLegendTextTitleColor(config.getLegendTextTitleColor())
                .withLegendBackgroundColor(config.getLegendBackgroundColor());
        if (!config.isDrawSelection()) {
            result.disableSelection();
        }
        return result;
    }

    @Nonnull
    @Override
    public ChartConfigBuilder withPlotLineWidthInPixels(int widthInPixels) {
        mPlotLineWidthInPixels = widthInPixels;
        return this;
    }

    @Nonnull
    @Override
    public ChartConfigImpl build() {
        AxisConfig xAxisConfig = getAxisConfig(mXAxisConfig, mXAxisConfigBuilder);
        AxisConfig yAxisConfig = getAxisConfig(mYAxisConfig, mYAxisConfigBuilder);
        int gridLineWidth = getDimensionSizeInPixels("Grid line width",
                                                     R.attr.leonardo_chart_grid_width,
                                                     mGridLineWidthInPixels,
                                                     mContext);
        int backgroundColor = getColor("Chart background color",
                                       R.attr.leonardo_chart_background_color,
                                       mBackgroundColor,
                                       mContext);
        int gridColor = getColor("Chart grid color", R.attr.leonardo_chart_grid_color, mGridColor, mContext);
        int insets = getDimensionSizeInPixels("Chart insets", R.attr.leonardo_chart_insets, mInsetsInPixels, mContext);
        int plotLineWidth = getDimensionSizeInPixels("Plot line width",
                                                     R.attr.leonardo_chart_plot_width,
                                                     mPlotLineWidthInPixels,
                                                     mContext);
        int selectionSignRadiusInPixels = getDimensionSizeInPixels("Selection sign radius",
                                                                   R.attr.leonardo_chart_selection_sign_radius,
                                                                   mSelectionSignRadiusInPixels,
                                                                   mContext);
        int legendTitleColor = getColor("Legend text title color",
                                        R.attr.leonardo_chart_legend_text_title_color,
                                        mLegendTitleColor,
                                        mContext);

        int legendBackgroundColor = getColor("Legend background color",
                                             R.attr.leonardo_chart_legend_background_color,
                                             mLegendBackgroundColor,
                                             mContext);

        return new ChartConfigImpl(xAxisConfig,
                                   yAxisConfig,
                                   mDrawBackground,
                                   gridLineWidth,
                                   backgroundColor,
                                   gridColor,
                                   insets,
                                   plotLineWidth,
                                   selectionSignRadiusInPixels,
                                   legendTitleColor,
                                   legendBackgroundColor,
                                   mDrawSelection,
                                   mAllowSelection,
                                   mEnableAnimations);
    }

    @Nonnull
    private AxisConfig getAxisConfig(@Nullable AxisConfig config, @Nullable AxisConfigBuilder builder) {
        if (config != null) {
            return config;
        }
        if (builder == null) {
            builder = LeonardoConfigFactory.newAxisConfigBuilder();
        }
        Context context = mContext;
        if (context != null) {
            builder.withContext(context);
        }
        return builder.build();
    }
}
