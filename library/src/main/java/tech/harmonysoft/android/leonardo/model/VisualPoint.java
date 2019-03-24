package tech.harmonysoft.android.leonardo.model;

import android.annotation.SuppressLint;

import javax.annotation.Nonnull;
import java.util.Objects;

/**
 * @author Denis Zhdanov
 * @since 15/3/19
 */
public class VisualPoint {

    private final float mX;
    private final float mY;

    public VisualPoint(float x, float y) {
        mX = x;
        mY = y;
    }

    public float getX() {
        return mX;
    }

    public float getY() {
        return mY;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (o == null || getClass() != o.getClass()) { return false; }
        VisualPoint that = (VisualPoint) o;
        return Float.compare(that.mX, mX) == 0 &&
               Float.compare(that.mY, mY) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(mX, mY);
    }

    @Nonnull
    @SuppressLint("DefaultLocale")
    @Override
    public String toString() {
        return String.format("(%f; %f)", mX, mY);
    }
}
