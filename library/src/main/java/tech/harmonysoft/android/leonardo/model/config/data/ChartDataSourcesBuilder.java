package tech.harmonysoft.android.leonardo.model.config.data;

import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;

import javax.annotation.Nonnull;
import java.util.Collection;

/**
 * @author Denis Zhdanov
 * @since 16/3/19
 */
public interface ChartDataSourcesBuilder {

    @Nonnull
    ChartDataSourcesBuilder addDataSource(ChartDataSource dataSource);

    @Nonnull
    ChartDataSourcesBuilder addDataSourceBuilder(ChartDataSourceBuilder builder);

    @Nonnull
    Collection<ChartDataSource> build();
}
