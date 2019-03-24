package tech.harmonysoft.android.leonardo.model.data;

import tech.harmonysoft.android.leonardo.model.Interval;
import tech.harmonysoft.android.leonardo.model.Range;

import javax.annotation.Nullable;

/**
 * @author Denis Zhdanov
 * @since 11/3/19
 */
public interface ChartDataLoader {

    /**
     * Loads target data. Is assumed to be called from a non-main thread.
     *
     * @param range     target X range
     * @return          an interval for the target X range if it's within the current dataset's range;
     *                  {@code null} otherwise
     */
    @Nullable
    Interval load(Range range);
}
