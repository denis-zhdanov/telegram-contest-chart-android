package tech.harmonysoft.android.leonardo.model.config.data;

import tech.harmonysoft.android.leonardo.model.data.ChartDataLoader;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 11/3/19
 */
public interface ChartDataSourceBuilder {

    @Nonnull
    ChartDataSourceBuilder withMinX(long minX);

    @Nonnull
    ChartDataSourceBuilder withMaxX(long maxX);

    @Nonnull
    ChartDataSourceBuilder withColor(int color);

    boolean hasColor();

    @Nonnull
    ChartDataSourceBuilder withLegend(String legend);

    @Nonnull
    ChartDataSourceBuilder withLoader(ChartDataLoader loader);

    @Nonnull
    ChartDataSource build();
}
