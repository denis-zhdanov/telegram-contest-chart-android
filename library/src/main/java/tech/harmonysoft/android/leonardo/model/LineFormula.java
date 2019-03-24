package tech.harmonysoft.android.leonardo.model;

import android.annotation.SuppressLint;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author Denis Zhdanov
 * @since 15/3/19
 */
public class LineFormula {

    private final float mA;
    private final float mB;

    public LineFormula(float a, float b) {
        mA = a;
        mB = b;
    }

    public float getY(float x) {
        return mA * x + mB;
    }

    public float getX(float y) {
        return (y - mB) / mA;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        LineFormula that = (LineFormula) o;
        return Float.compare(that.mA, mA) == 0 &&
               Float.compare(that.mB, mB) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mA, mB);
    }

    @Nonnull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("%f * x + %f", mA, mB);
    }
}
