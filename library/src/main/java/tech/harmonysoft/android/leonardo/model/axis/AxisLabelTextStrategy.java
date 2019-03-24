package tech.harmonysoft.android.leonardo.model.axis;

import tech.harmonysoft.android.leonardo.model.text.TextWrapper;

import javax.annotation.Nonnull;

/**
 * Defines contract for a custom strategy to map chart data to human-readable values.
 *
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public interface AxisLabelTextStrategy {

    /**
     * Allows getting a custom value to use for the axis label. E.g. suppose our data points use time as X.
     * Then we can map the value to a human-readable text like {@code '16:42'}.
     *
     * @param value     target point's value
     * @param step      a step used by the current axis. E.g. when we draw time stored in seconds, we'd like
     *                  to use seconds label if possible, like {@code '12:23:43'}. 'Step' value would be {@code '1'}
     *                  then. However, it might be that we have so many data points shown at the moment,
     *                  that it would be reasonable to fallback to seconds, like {@code '12:23'}. 'Step' would
     *                  be {@code '60'} in this case. Further on, we can fallback to hours (step {@code 3600}) etc.
     * @return          human-readable text representation of the target value with the given step
     */
    @Nonnull
    TextWrapper getLabel(long value, long step);

    @Nonnull
    TextWrapper getMinifiedLabel(long value, long step);
}
