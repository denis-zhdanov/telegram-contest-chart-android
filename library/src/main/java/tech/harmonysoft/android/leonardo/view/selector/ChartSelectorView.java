package tech.harmonysoft.android.leonardo.view.selector;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import tech.harmonysoft.android.leonardo.model.config.selector.ChartSelectorConfig;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;
import tech.harmonysoft.android.leonardo.model.runtime.ChartModel;
import tech.harmonysoft.android.leonardo.util.LeonardoUtil;
import tech.harmonysoft.android.leonardo.view.RoundedRectangleDrawer;
import tech.harmonysoft.android.leonardo.view.util.TextWidthMeasurer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

/**
 * @author Denis Zhdanov
 * @since 17/3/19
 */
public class ChartSelectorView extends View {

    private static final Comparator<ChartDataSource> COMPARATOR = (s1, s2) -> s1.getLegend().compareTo(s2.getLegend());

    private static final int MIN_PADDING = 30;

    private final List<ChartDataSource>  mDataSources            = new ArrayList<>();
    private final RoundedRectangleDrawer mRoundedRectangleDrawer = new RoundedRectangleDrawer();

    private ChartSelectorConfig mConfig;
    private boolean             mConfigApplied;

    private ChartModel mChartModel;

    private Paint             mPaint;
    private Paint             mBackgroundPaint;
    private Paint             mRowSeparatorPaint;
    private float             mTextHeight;
    private TextWidthMeasurer mLegendTextWidthMeasurer;

    private float mXShift;
    private float mYShift;
    private float mVirtualBottom;
    private float mVirtualRight;

    private float   mPreviousEventX;
    private float   mPreviousEventY;
    private boolean mMoved;

    public ChartSelectorView(@Nonnull Context context) {
        super(context);
    }

    public ChartSelectorView(@Nonnull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public ChartSelectorView(@Nonnull Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void apply(ChartSelectorConfig config) {
        mConfig = config;
        mConfigApplied = false;
        applyConfig();
    }

    public void apply(ChartModel model) {
        mChartModel = model;
        invalidate();
    }

    public void setDataSources(Collection<ChartDataSource> dataSources) {
        mDataSources.clear();
        mDataSources.addAll(dataSources);
        Collections.sort(mDataSources, COMPARATOR);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        mXShift = 0;
        mYShift = 0;
        int width = MeasureSpec.getSize(widthMeasureSpec);
        if (mTextHeight <= 0 || mDataSources.isEmpty()) {
            setMeasuredDimension(width, 0);
            return;
        }

        int height = MeasureSpec.getSize(heightMeasureSpec);
        int availableVisualRows = (int) (height / mTextHeight);
        float padding = (height % mTextHeight) / (mDataSources.size() * 2);
        if (padding < MIN_PADDING) {
            availableVisualRows--;
        }
        if (availableVisualRows == mDataSources.size()) {
            setMeasuredDimension(width, height);
            return;
        }

        if (availableVisualRows > mDataSources.size()) {
            float heightToUse = Math.min(
                    height,
                    mDataSources.size() * (mTextHeight + 2 * getDesiredVerticalPadding())
            );
            setMeasuredDimension(width, (int) heightToUse);
            return;
        }

        setMeasuredDimension(width,
                             (int) (mDataSources.size() * (mTextHeight + 2 * getDesiredVerticalPadding())));
    }

    private float getDesiredVerticalPadding() {
        return mTextHeight * 57f / 37f;
    }

    private float getDesiredHorizontalTextPadding() {
        return mTextHeight * 2f;
    }

    private float getDesiredCheckBoxEdge() {
        return mTextHeight * 52f / 33f;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        applyConfig();

        canvas.drawPaint(mBackgroundPaint);

        final float verticalPadding = getVerticalPadding();
        final float checkBoxEdge = getCheckBoxEdge(verticalPadding);

        final float internalPadding;
        int maxTextWidth = getMaxTextWidth();
        float remainingX = getWidth() - checkBoxEdge - getDesiredHorizontalTextPadding() - maxTextWidth;
        if (remainingX >= 0) {
            internalPadding = getDesiredHorizontalTextPadding();
        } else if (-remainingX >= MIN_PADDING) {
            internalPadding = -remainingX;
        } else {
            internalPadding = MIN_PADDING;
        }
        mVirtualRight = Math.max(getWidth(), checkBoxEdge + internalPadding + maxTextWidth);

        canvas.translate(-mXShift, -mYShift);

        boolean first = true;
        float y = 0;
        for (ChartDataSource dataSource : mDataSources) {
            if (first) {
                first = false;
            } else {
                canvas.drawLine(checkBoxEdge + internalPadding, y, getWidth(), y, mRowSeparatorPaint);
            }
            drawDataSource(dataSource, y, verticalPadding, checkBoxEdge, internalPadding, canvas);
            y += verticalPadding * 2 + mTextHeight;
        }

        mVirtualBottom = y;
    }

    private void applyConfig() {
        if (mConfigApplied) {
            return;
        }
        if (mConfig == null) {
            throw new IllegalStateException("Legend selector view config is not set");
        }
        initTextPaint();
        initBackgroundPaint();
        initRowSeparatorPaint();

        mConfigApplied = true;
        invalidate();
    }

    private void initTextPaint() {
        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(mConfig.getFontSize());
        Rect bounds = new Rect();
        paint.getTextBounds("Z", 0, 1, bounds);
        mTextHeight = bounds.height();
        mLegendTextWidthMeasurer = new TextWidthMeasurer(paint);
        mPaint = paint;
    }

    private void initBackgroundPaint() {
        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setColor(mConfig.getBackgroundColor());
        mBackgroundPaint = paint;
    }

    private void initRowSeparatorPaint() {
        Paint paint = new Paint();
        paint.setColor(mConfig.getRowSeparatorColor());
        mRowSeparatorPaint = paint;
    }

    private float getVerticalPadding() {
        if (mDataSources.size() * (mTextHeight + 2 * MIN_PADDING) >= getHeight()) {
            return getDesiredVerticalPadding();
        }

        return Math.min(getDesiredVerticalPadding(),
                        (getHeight() - mDataSources.size() * mTextHeight) / (mDataSources.size() * 2));
    }

    private float getCheckBoxEdge(float verticalPadding) {
        float rowHeight = mTextHeight + 2 * verticalPadding;
        return Math.min(rowHeight / 2, getDesiredCheckBoxEdge());
    }

    private int getMaxTextWidth() {
        int result = 0;
        for (ChartDataSource dataSource : mDataSources) {
            int candidate = mLegendTextWidthMeasurer.measureVisualSpace(dataSource.getLegend());
            if (candidate > result) {
                result = candidate;
            }
        }
        return result;
    }

    private void drawDataSource(ChartDataSource dataSource,
                                float lineTopY,
                                float verticalPadding,
                                float checkBoxEdge,
                                float horizontalTextPadding,
                                Canvas canvas)
    {
        mPaint.setColor(dataSource.getColor());

        float lineHeight = mTextHeight + verticalPadding * 2;
        float checkBoxTop = lineTopY + (lineHeight - checkBoxEdge) / 2f;
        drawCheckBox(mChartModel.isActive(dataSource), checkBoxTop, checkBoxEdge, canvas);

        mPaint.setColor(mConfig.getLegendTextColor());
        mPaint.setStyle(Paint.Style.FILL);
        canvas.drawText(dataSource.getLegend(),
                        checkBoxEdge + horizontalTextPadding,
                        lineTopY + verticalPadding + mTextHeight,
                        mPaint);
    }

    private void drawCheckBox(boolean selected, float top, float checkBoxEdge, Canvas canvas) {
        mPaint.setStyle(Paint.Style.FILL);
        RectF checkBoxRect = new RectF(0, top, 0 + checkBoxEdge, top + checkBoxEdge);
        float radius = checkBoxEdge / 6;

        mRoundedRectangleDrawer.draw(checkBoxRect, mPaint, mPaint, radius, canvas);

        // Fill left - right
        canvas.drawRect(0, top + radius, 0 + checkBoxEdge, top + checkBoxEdge - radius, mPaint);

        // Fill top - bottom
        canvas.drawRect(0 + radius, top, 0 + checkBoxEdge - radius, top + checkBoxEdge, mPaint);

        Path path = new Path();
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(mConfig.getCheckSignColor());
        float strokeWidth = mPaint.getStrokeWidth();
        mPaint.setStrokeWidth(mTextHeight / 5f);
        if (selected) {
            path.moveTo(checkBoxEdge * 12f / 54f, top + checkBoxEdge * 31f / 54f);
            path.lineTo(checkBoxEdge * 20f / 54f, top + checkBoxEdge * 39f / 54f);
            path.lineTo(checkBoxEdge * 43f / 54f, top + checkBoxEdge * 17f / 54f);
        } else {
            float signPadding = checkBoxEdge / 4f;
            canvas.drawLine(signPadding,
                            top + signPadding,
                            checkBoxEdge - signPadding,
                            top + checkBoxEdge - signPadding,
                            mPaint);
            canvas.drawLine(signPadding,
                            top + checkBoxEdge - signPadding,
                            checkBoxEdge - signPadding,
                            top + signPadding,
                            mPaint);
        }
        canvas.drawPath(path, mPaint);
        mPaint.setStrokeWidth(strokeWidth);
    }


    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_UP:
                mayBeMove(event.getX(), event.getY());
                if (!mMoved) {
                    handleClick(event.getX(), event.getY());
                }
            case MotionEvent.ACTION_DOWN:
                mPreviousEventX = -1;
                mPreviousEventY = -1;
                mMoved = false;
                break;
            case MotionEvent.ACTION_MOVE:
                mayBeMove(event.getX(), event.getY());
        }
        return true;
    }

    private void mayBeMove(float x, float y) {
        if (mPreviousEventX < 0 || mPreviousEventY < 0) {
            mPreviousEventX = x;
            mPreviousEventY = y;
            return;
        }

        if (Math.abs(mPreviousEventX - x) <= 10 && Math.abs(mPreviousEventY - y) <= 10) {
            // We experienced that plain clicks result in a couple of pixel's shift on a real phone
            return;
        }

        float dx = mPreviousEventX - x;
        float dy = mPreviousEventY - y;
        mPreviousEventX = x;
        mPreviousEventY = y;

        if (changeXOnMove(dx) || changeYOnMove(dy)) {
            invalidate();
            mMoved = true;
        }
    }

    private boolean changeXOnMove(float dx) {
        if (dx == 0) {
            return false;
        }

        if (dx > 0) {
            float remaining = mVirtualRight - mXShift - getWidth();
            if (remaining <= 0) {
                return false;
            }
            mXShift += Math.min(remaining, dx);
            return true;
        }

        if (mXShift <= 0) {
            return false;
        }
        mXShift = Math.max(0, mXShift + dx);
        return true;
    }

    private boolean changeYOnMove(float dy) {
        if (dy == 0) {
            return false;
        }

        if (dy > 0) {
            float remaining = mVirtualBottom - mYShift - getHeight();
            if (remaining <= 0) {
                return false;
            }
            mYShift += Math.min(remaining, dy);
            return true;
        }

        if (mYShift <= 0) {
            return false;
        }
        mYShift = Math.max(0, mYShift + dy);
        return true;
    }

    private void handleClick(float x, float y) {
        final float verticalPadding = getVerticalPadding();
        final float checkBoxEdge = getCheckBoxEdge(verticalPadding);

        float virtualX = x + mXShift;
        if (virtualX > checkBoxEdge + LeonardoUtil.ACTION_START_AUTO_EXPAND_AREA_IN_PIXELS) {
            return;
        }

        float rowHeight = mTextHeight + 2 * verticalPadding;
        float virtualY = y + mYShift;
        int row = Math.round(100 * virtualY / rowHeight) / 100;
        if (row >= mDataSources.size()) {
            return;
        }

        float checkBoxTop = row * rowHeight + (rowHeight - checkBoxEdge) / 2f;
        if (virtualY < checkBoxTop - LeonardoUtil.ACTION_START_AUTO_EXPAND_AREA_IN_PIXELS
            || virtualY > checkBoxTop + checkBoxEdge + LeonardoUtil.ACTION_START_AUTO_EXPAND_AREA_IN_PIXELS)
        {
            return;
        }

        ChartDataSource dataSource = mDataSources.get(row);
        if (mChartModel.isActive(dataSource)) {
            mChartModel.disableDataSource(dataSource);
        } else {
            mChartModel.enableDataSource(dataSource);
        }
        invalidate();
    }
}
