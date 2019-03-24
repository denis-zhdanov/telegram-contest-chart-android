package tech.harmonysoft.android.leonardo.model.runtime;

import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;

/**
 * @author Denis Zhdanov
 * @since 13/3/19
 */
public interface ChartModelListener {

    void onRangeChanged(Object anchor);

    void onDataSourceEnabled(ChartDataSource dataSource);

    void onDataSourceDisabled(ChartDataSource dataSource);

    void onDataSourceAdded(ChartDataSource dataSource);

    void onDataSourceRemoved(ChartDataSource dataSource);

    void onActiveDataPointsLoaded(Object anchor);

    void onSelectionChange();
}
