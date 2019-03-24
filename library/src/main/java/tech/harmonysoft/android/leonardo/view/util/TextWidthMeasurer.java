package tech.harmonysoft.android.leonardo.view.util;

import android.graphics.Paint;
import android.graphics.Rect;
import tech.harmonysoft.android.leonardo.model.text.TextWrapper;

/**
 * @author Denis Zhdanov
 * @since 13/3/19
 */
public class TextWidthMeasurer implements TextSpaceMeasurer {

    private final Rect  mBounds = new Rect();
    private final Paint mPaint;

    public TextWidthMeasurer(Paint paint) {
        mPaint = paint;
    }

    @Override
    public int measureVisualSpace(String text) {
        mPaint.getTextBounds(text, 0, text.length(), mBounds);
        return mBounds.width();
    }

    @Override
    public int measureVisualSpace(TextWrapper text) {
        mPaint.getTextBounds(text.getData(), 0, text.getLength(), mBounds);
        return mBounds.width();
    }
}
