package harmonysoft.tech.chartexample.data;

import tech.harmonysoft.android.leonardo.model.DataPoint;
import tech.harmonysoft.android.leonardo.model.Interval;
import tech.harmonysoft.android.leonardo.model.Range;
import tech.harmonysoft.android.leonardo.model.data.ChartDataLoader;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.NavigableSet;
import java.util.TreeSet;

/**
 * @author Denis Zhdanov
 * @since 20/3/19
 */
public class PreDefinedChartDataLoader implements ChartDataLoader {

    private final NavigableSet<DataPoint> mPoints = new TreeSet<>(DataPoint.COMPARATOR_BY_X);

    public PreDefinedChartDataLoader(Collection<DataPoint> points) {
        mPoints.addAll(points);
    }

    @Nullable
    @Override
    public Interval load(Range range) {
        if (mPoints.isEmpty() || range.getStart() > mPoints.last().getX() || range.getEnd() < mPoints.first().getX()) {
            return null;
        }
        return new Interval(mPoints.headSet(new DataPoint(range.getEnd(), 0), true)
                                   .tailSet(new DataPoint(range.getStart(), 0), true));
    }
}
