package tech.harmonysoft.android.leonardo.view.util;

import tech.harmonysoft.android.leonardo.util.IntSupplier;

/**
 * @author Denis Zhdanov
 * @since 14/3/19
 */
public class YAxisLabelGapStrategy implements AxisStepChooser.MinGapStrategy {

    private final IntSupplier mAdditionalPaddingSupplier;

    public YAxisLabelGapStrategy(IntSupplier additionalPaddingSupplier) {
        mAdditionalPaddingSupplier = additionalPaddingSupplier;
    }

    @Override
    public int calculateMinGap(int labelHeight) {
        return labelHeight * 5 + mAdditionalPaddingSupplier.get();
    }
}
