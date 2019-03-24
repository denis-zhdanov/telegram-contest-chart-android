package tech.harmonysoft.android.leonardo.model.data;

import tech.harmonysoft.android.leonardo.model.Range;

import javax.annotation.Nonnull;

/**
 * Represents a chart data source. Every chart is expected to have at least one of them.
 *
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public interface ChartDataSource extends ChartDataLoader {

    @Nonnull
    String getLegend();

    int getColor();

    @Nonnull
    Range getDataRange();
}
