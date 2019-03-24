package tech.harmonysoft.android.leonardo.view.util;

/**
 * @author Denis Zhdanov
 * @since 13/3/19
 */
public class XAxisLabelGapStrategy implements AxisStepChooser.MinGapStrategy {

    @Override
    public int calculateMinGap(int labelWidth) {
        return labelWidth * 4 / 3;
    }
}
