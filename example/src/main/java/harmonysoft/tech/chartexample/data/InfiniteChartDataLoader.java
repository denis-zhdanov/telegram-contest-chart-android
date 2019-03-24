package harmonysoft.tech.chartexample.data;

import android.util.LongSparseArray;
import tech.harmonysoft.android.leonardo.model.DataPoint;
import tech.harmonysoft.android.leonardo.model.Interval;
import tech.harmonysoft.android.leonardo.model.Range;
import tech.harmonysoft.android.leonardo.model.data.ChartDataLoader;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public class InfiniteChartDataLoader implements ChartDataLoader {

//    private static final int Y_MIN = -24;
//    private static final int Y_MAX = 24;
    private static final int Y_MIN = 0;
    private static final int Y_MAX = 10;
    private static final int Y_RANGE_LENGTH = Y_MAX - Y_MIN + 1;

    private final LongSparseArray<Integer> mCache = new LongSparseArray<>();

    @Nullable
    @Override
    public Interval load(Range range) {
        List<DataPoint> points = new ArrayList<>();
        for (long x = range.getStart(); x <= range.getEnd(); x++) {
            Integer y = mCache.get(x);
            if (y == null) {
                mCache.put(x, y = new Random().nextInt(Y_RANGE_LENGTH) + Y_MIN);
            }
            points.add(new DataPoint(x, y));
        }
        return new Interval(points);
    }
}
