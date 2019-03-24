package tech.harmonysoft.android.leonardo.controller;

import android.annotation.SuppressLint;
import android.os.AsyncTask;
import tech.harmonysoft.android.leonardo.log.LogUtil;
import tech.harmonysoft.android.leonardo.model.Interval;
import tech.harmonysoft.android.leonardo.model.Range;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;
import tech.harmonysoft.android.leonardo.model.runtime.ChartModel;
import tech.harmonysoft.android.leonardo.model.runtime.ChartModelListener;
import tech.harmonysoft.android.leonardo.util.RangesList;

import javax.annotation.Nonnull;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * @author Denis Zhdanov
 * @since 14/3/19
 */
public class ChartDataManager implements ChartModelListener {

    private final Set<ChartDataLoadTask> mTasks = new HashSet<>();

    private final ChartModel mModel;

    public ChartDataManager(ChartModel model) {
        mModel = model;
        model.addListener(this);
        mayBeLoadRanges();
    }

    @Override
    public void onRangeChanged(Object anchor) {
        mayBeLoadRanges();
    }

    private void mayBeLoadRanges() {
        for (ChartDataLoadTask task : mTasks) {
            task.cancel(true);
        }
        mTasks.clear();
        Range bufferRange = mModel.getBufferRange();

        LogUtil.debug(this, "mayBeLoadRanges(): buffer range=%s", bufferRange);

        if (bufferRange.isEmpty()) {
            return;
        }
        for (ChartDataSource dataSource : mModel.getRegisteredDataSources()) {
            RangesList loadedRanges = mModel.getLoadedRanges(dataSource);
            LogUtil.debug(this,
                          "mayBeLoadRanges(): loaded ranges for source '%s'=%s",
                          dataSource, loadedRanges);
            Collection<Range> rangesToLoad = loadedRanges.getMissing(bufferRange);
            LogUtil.debug(this,
                          "mayBeLoadRanges(): ranges to load for source '%s'=%s",
                          dataSource, rangesToLoad);

            for (Range rangeToLoad : rangesToLoad) {
                ChartDataLoadTask task = new ChartDataLoadTask();
                mTasks.add(task);
                task.execute(new LoadRequest(dataSource, rangeToLoad));
            }
        }
    }

    @Override
    public void onDataSourceEnabled(ChartDataSource dataSource) {
    }

    @Override
    public void onDataSourceDisabled(ChartDataSource dataSource) {
    }

    @Override
    public void onDataSourceAdded(ChartDataSource dataSource) {
        mayBeLoadRanges();
    }

    @Override
    public void onDataSourceRemoved(ChartDataSource dataSource) {
    }

    @Override
    public void onActiveDataPointsLoaded(Object anchor) {
    }

    @Override
    public void onSelectionChange() {
    }

    @SuppressLint("StaticFieldLeak")
    private class ChartDataLoadTask extends AsyncTask<LoadRequest, Void, LoadResult> {

        @Override
        protected LoadResult doInBackground(LoadRequest... requests) {
            if (requests.length != 1) {
                throw new IllegalArgumentException(String.format("Expected to get a single load request but got %d",
                                                                 requests.length));
            }
            LoadRequest request = requests[0];
            Interval interval = request.getDataSource().load(request.getRange());
            return new LoadResult(interval == null ? Interval.EMPTY : interval,
                                  request.getRange(),
                                  request.getDataSource());
        }

        @Override
        protected void onPostExecute(LoadResult loadResult) {
            mTasks.remove(this);
            mModel.onPointsLoaded(loadResult.getSource(), loadResult.getRange(), loadResult.getInterval());
        }
    }

    private static final class LoadRequest {

        private final ChartDataSource mDataSource;
        private final Range           mRange;

        public LoadRequest(ChartDataSource dataSource, Range range) {
            mDataSource = dataSource;
            mRange = range;
        }

        @Nonnull
        public ChartDataSource getDataSource() {
            return mDataSource;
        }

        @Nonnull
        public Range getRange() {
            return mRange;
        }
    }

    private static class LoadResult {

        private final Interval        mInterval;
        private final Range           mRange;
        private final ChartDataSource mSource;

        public LoadResult(Interval interval, Range range, ChartDataSource source) {
            mInterval = interval;
            mRange = range;
            mSource = source;
        }

        @Nonnull
        public Interval getInterval() {
            return mInterval;
        }

        @Nonnull
        public Range getRange() {
            return mRange;
        }

        @Nonnull
        public ChartDataSource getSource() {
            return mSource;
        }
    }
}
