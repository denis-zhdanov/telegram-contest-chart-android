package tech.harmonysoft.android.leonardo.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * @author Denis Zhdanov
 * @since 16/3/19
 */
public class RoundedRectangleDrawer {

    public void draw(RectF rect, Paint borderPaint, Paint fillPaint, float radius, Canvas canvas) {
        drawRoundedEdges(rect, borderPaint, radius, canvas);
        drawBackground(rect, fillPaint, radius, borderPaint.getStrokeWidth(), canvas);
        drawEdges(rect, borderPaint, radius, canvas);
    }

    private void drawRoundedEdges(RectF rect, Paint paint, float radius, Canvas canvas) {
        // Top-left corner
        canvas.drawCircle(rect.left + radius, rect.top + radius, radius, paint);

        // Top-right corner
        canvas.drawCircle(rect.right - radius, rect.top + radius, radius, paint);

        // Bottom-right corner
        canvas.drawCircle(rect.right - radius, rect.bottom - radius, radius, paint);

        // Bottom-left corner
        canvas.drawCircle(rect.left + radius, rect.bottom - radius, radius, paint);
    }

    private void drawBackground(RectF rect, Paint paint, float radius, float borderWidth, Canvas canvas) {
        // Top-left corner
        canvas.drawCircle(rect.left + radius, rect.top + radius, radius - borderWidth, paint);

        // Top-right corner
        canvas.drawCircle(rect.right - radius, rect.top + radius, radius - borderWidth, paint);

        // Bottom-right corner
        canvas.drawCircle(rect.right - radius, rect.bottom - radius, radius - borderWidth, paint);

        // Bottom-left corner
        canvas.drawCircle(rect.left + radius, rect.bottom - radius - borderWidth, radius, paint);

        // Center area
        canvas.drawRect(rect.left + radius, rect.top, rect.right - radius, rect.bottom, paint);

        // Left area
        canvas.drawRect(rect.left, rect.top + radius, rect.left + radius, rect.bottom - radius, paint);

        // Right area
        canvas.drawRect(rect.right - radius, rect.top + radius, rect.right, rect.bottom - radius, paint);
    }

    private void drawEdges(RectF rect, Paint paint, float radius, Canvas canvas) {
        // Left edge
        canvas.drawLine(rect.left, rect.top + radius, rect.left, rect.bottom - radius, paint);

        // Top edge
        canvas.drawLine(rect.left + radius, rect.top, rect.right - radius, rect.top, paint);

        // Right edge
        canvas.drawLine(rect.right, rect.top + radius, rect.right, rect.bottom - radius, paint);

        // Bottom edge
        canvas.drawLine(rect.left + radius, rect.bottom, rect.right - radius, rect.bottom, paint);
    }
}
