package tech.harmonysoft.android.leonardo.view.util;

import tech.harmonysoft.android.leonardo.model.Range;
import tech.harmonysoft.android.leonardo.model.axis.AxisLabelTextStrategy;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 13/3/19
 */
public class AxisStepChooser {

    public long choose(AxisLabelTextStrategy textStrategy,
                       MinGapStrategy minGapStrategy,
                       Range currentRange,
                       final int availableVisualSpace,
                       TextSpaceMeasurer measurer)
    {
        long low = 1;
        long high = 10;
        while (true) {
            long value = currentRange.findFirstStepValue(high);
            CheckResult checkResult = checkStep(textStrategy,
                                                measurer,
                                                minGapStrategy,
                                                currentRange,
                                                availableVisualSpace,
                                                high,
                                                value);
            if (checkResult == CheckResult.OK) {
                return high;
            } else if (checkResult == CheckResult.TOO_BIG) {
                break;
            } else {
                low = high;
                high *= 10;
            }
        }

        while (low <= high) {
            long candidate = low + (high - low) / 2;
            if (candidate == low) {
                return low;
            }
            long value = currentRange.findFirstStepValue(high);
            CheckResult checkResult = checkStep(textStrategy,
                                                measurer,
                                                minGapStrategy,
                                                currentRange,
                                                availableVisualSpace,
                                                candidate,
                                                value);
            switch (checkResult) {
                case OK: return candidate;
                case TOO_BIG:
                    high = candidate;
                    break;
                case TOO_SMALL:
                    low = candidate;
                    break;
            }
        }
        return low;
    }

    @Nonnull
    private CheckResult checkStep(AxisLabelTextStrategy textStrategy,
                                  TextSpaceMeasurer measurer,
                                  MinGapStrategy minGapStrategy,
                                  Range range,
                                  int availableVisualSpace,
                                  long step,
                                  final long firstValue)
    {
        if (firstValue == Long.MIN_VALUE) {
            return CheckResult.TOO_BIG;
        }
        int labelsNumber = 0;
        for (long value = firstValue; range.contains(value) && availableVisualSpace >= 0; ) {
            int labelSize = measurer.measureVisualSpace(textStrategy.getLabel(value, step));
            availableVisualSpace -= labelSize;
            value += step;
            labelsNumber++;
            if (value > range.getEnd()) {
                break;
            }
            if (value != firstValue) {
                availableVisualSpace -= minGapStrategy.calculateMinGap(labelSize);
            }
        }
        if (availableVisualSpace >= 0) {
            if (labelsNumber > 2) {
                return CheckResult.OK;
            } else {
                return CheckResult.TOO_BIG;
            }
        } else {
            return CheckResult.TOO_SMALL;
        }
    }

    private enum CheckResult {
        TOO_BIG, TOO_SMALL, OK
    }

    /**
     * We want to avoid a situation when axis labels are drawn one next to another without visual spaces,
     * i.e. we want to ensure that there are gaps between them. Current strategy defines contract for gap calculation
     */
    public interface MinGapStrategy {

        int calculateMinGap(int labelSize);
    }
}
