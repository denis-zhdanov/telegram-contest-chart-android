package tech.harmonysoft.android.leonardo.model.config;

import tech.harmonysoft.android.leonardo.controller.NavigatorShowcase;
import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfigBuilder;
import tech.harmonysoft.android.leonardo.model.config.chart.ChartConfigBuilder;
import tech.harmonysoft.android.leonardo.model.config.chart.impl.ChartConfigBuilderImpl;
import tech.harmonysoft.android.leonardo.model.config.data.ChartDataSourceBuilder;
import tech.harmonysoft.android.leonardo.model.config.axis.impl.AxisConfigBuilderImpl;
import tech.harmonysoft.android.leonardo.model.config.data.ChartDataSourcesBuilder;
import tech.harmonysoft.android.leonardo.model.config.data.impl.ChartDataSourceBuilderImpl;
import tech.harmonysoft.android.leonardo.model.config.data.impl.ChartDataSourcesBuilderImpl;
import tech.harmonysoft.android.leonardo.model.config.selector.ChartSelectorConfigBuilder;
import tech.harmonysoft.android.leonardo.model.config.selector.impl.ChartSelectorConfigBuilderImpl;
import tech.harmonysoft.android.leonardo.model.config.navigator.NavigatorConfigBuilder;
import tech.harmonysoft.android.leonardo.model.config.navigator.impl.NavigatorConfigBuilderImpl;
import tech.harmonysoft.android.leonardo.view.ChartView;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public class LeonardoConfigFactory {

    private LeonardoConfigFactory() {
    }

    @Nonnull
    public static ChartDataSourceBuilder newDataSourceBuilder() {
        return new ChartDataSourceBuilderImpl();
    }

    @Nonnull
    public static ChartDataSourcesBuilder newDataSourcesBuilder() {
        return new ChartDataSourcesBuilderImpl();
    }

    @Nonnull
    public static ChartConfigBuilder newChartConfigBuilder() {
        return new ChartConfigBuilderImpl();
    }

    @Nonnull
    public static AxisConfigBuilder newAxisConfigBuilder() {
        return new AxisConfigBuilderImpl();
    }

    @Nonnull
    public static NavigatorConfigBuilder newNavigatorConfigBuilder() {
        return new NavigatorConfigBuilderImpl();
    }

    @Nonnull
    public static ChartSelectorConfigBuilder newChartSelectorConfigBuilder() {
        return new ChartSelectorConfigBuilderImpl();
    }

    @Nonnull
    public static NavigatorShowcase asNavigatorShowCase(@Nonnull ChartView view) {
        return new NavigatorShowcase() {
            @Nonnull
            @Override
            public Object getDataAnchor() {
                return view.getDataAnchor();
            }

            @Override
            public float getVisualXShift() {
                return view.getXVisualShift();
            }

            @Override
            public void scrollHorizontally(float deltaVisualX) {
                view.scrollHorizontally(deltaVisualX);
            }
        };
    }
}
