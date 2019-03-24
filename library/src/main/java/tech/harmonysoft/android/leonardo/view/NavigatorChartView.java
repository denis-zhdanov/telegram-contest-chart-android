package tech.harmonysoft.android.leonardo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import tech.harmonysoft.android.leonardo.controller.NavigatorShowcase;
import tech.harmonysoft.android.leonardo.log.LogUtil;
import tech.harmonysoft.android.leonardo.model.DataPoint;
import tech.harmonysoft.android.leonardo.model.Range;
import tech.harmonysoft.android.leonardo.model.config.LeonardoConfigFactory;
import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfig;
import tech.harmonysoft.android.leonardo.model.config.chart.ChartConfig;
import tech.harmonysoft.android.leonardo.model.config.navigator.NavigatorConfig;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;
import tech.harmonysoft.android.leonardo.model.runtime.ChartModel;
import tech.harmonysoft.android.leonardo.model.runtime.ChartModelListener;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Denis Zhdanov
 * @since 16/3/19
 */
public class NavigatorChartView extends View {

    private static final long MIN_WIDTH_IN_PIXELS               = 40;
    private static final long CLICK_RECOGNITION_ERROR_IN_PIXELS = 30;

    private NavigatorConfig   mConfig;
    private ChartModel        mModel;
    private ChartView         mView;
    private NavigatorShowcase mShowCase;

    private Paint mInactiveBackgroundPaint;
    private Paint mActiveBackgroundPaint;
    private Paint mActiveBorderPaint;
    private Paint mSelectionPaint;

    private ActionType mCurrentAction;
    private Float      mPreviousActionVisualX;

    public NavigatorChartView(Context context) {
        super(context);
        init();
    }

    public NavigatorChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public NavigatorChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mView = new ChartView(getContext());
        setLogMarker();
    }

    private void setLogMarker() {
        LogUtil.putMarker(mView, String.format("%s-inner-chart-view", LogUtil.getMarker(this)));
    }

    public void apply(NavigatorConfig navigatorConfig, ChartConfig chartConfig) {
        mConfig = navigatorConfig;
        mActiveBackgroundPaint = createPaint(chartConfig.getBackgroundColor());
        mInactiveBackgroundPaint = createPaint(mConfig.getInactiveChartBackgroundColor());
        mActiveBorderPaint = createPaint(mConfig.getActiveBorderColor());
        mSelectionPaint = createPaint(mConfig.getSelectionColor());
        int plotLineWidth = Math.max(1, chartConfig.getPlotLineWidthInPixels() / 2);
        AxisConfig axisConfig = LeonardoConfigFactory.newAxisConfigBuilder()
                                                     .disableLabels()
                                                     .disableAxis()
                                                     .build();
        mView.apply(LeonardoConfigFactory.newChartConfigBuilder()
                                         .withConfig(chartConfig)
                                         .withInsetsInPixels(0)
                                         .withPlotLineWidthInPixels(plotLineWidth)
                                         .disableSelection()
                                         .disableBackground()
                                         .disableAnimations()
                                         .withXAxisConfig(axisConfig)
                                         .withYAxisConfig(axisConfig)
                                         .withContext(getContext())
                                         .build());
        invalidate();
        setLogMarker();
    }

    public void apply(ChartModel model) {
        mModel = model;
        mView.apply(model);
        setupListener(model);
        refreshMyRange();
        invalidate();
        setLogMarker();
    }

    public void apply(NavigatorShowcase showCase) {
        mShowCase = showCase;
    }

    private void setupListener(ChartModel model) {
        model.addListener(new ChartModelListener() {
            @Override
            public void onRangeChanged(Object anchor) {
                if (anchor == getModelAnchor()) {
                    refreshMyRange();
                } else if (anchor == mShowCase.getDataAnchor()) {
                    LogUtil.debug(getModelAnchor(),
                                  "Requesting repaint for %s",
                                  LogUtil.getMarker(mView));
                    invalidate();
                }
            }

            @Override
            public void onDataSourceEnabled(ChartDataSource dataSource) {
                invalidate();
            }

            @Override
            public void onDataSourceDisabled(ChartDataSource dataSource) {
                invalidate();
            }

            @Override
            public void onDataSourceAdded(ChartDataSource dataSource) {
                invalidate();
            }

            @Override
            public void onDataSourceRemoved(ChartDataSource dataSource) {
                invalidate();
            }

            @Override
            public void onActiveDataPointsLoaded(Object anchor) {
                invalidate();
            }

            @Override
            public void onSelectionChange() {
            }
        });
    }

    private void refreshMyRange() {
        Range navigatorRange = mModel.getActiveRange(getModelAnchor());
        mModel.setActiveRange(navigatorRange, mView);
        long dependencyPointsNumber = Math.max(1, navigatorRange.getPointsNumber() / 4);
        long dependencyRangeShift = (navigatorRange.getPointsNumber() - dependencyPointsNumber) / 2;
        long dependencyRangeStart = navigatorRange.getStart() + dependencyRangeShift;
        Range dependencyRange = new Range(dependencyRangeStart,
                                          dependencyRangeStart + dependencyPointsNumber);
        mModel.setActiveRange(dependencyRange, mShowCase.getDataAnchor());
        invalidate();
    }

    @Nonnull
    private Object getModelAnchor() {
        return this;
    }

    private void mayBeInitialize() {
        mayBeSetInnerChartDimensions();
    }

    private void mayBeSetInnerChartDimensions() {
        int margin = mConfig.getViewMargin();
        if (mView.getWidth() + 2 * margin != getWidth()) {
            mView.setLeft(0);
            mView.setTop(0);
            mView.setRight(getWidth() - 2 * margin);
            mView.setBottom(getHeight() - 2 * margin);
        }
    }

    @Nonnull
    private Paint createPaint(int color) {
        Paint paint = new Paint();
        paint.setColor(color);
        paint.setStyle(Paint.Style.FILL);
        return paint;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int margin = 0;
        if (mConfig != null) {
            margin = mConfig.getViewMargin();
        }

        int width = MeasureSpec.getSize(widthMeasureSpec);
        int chartHeight = getChartHeight(width);

        setMeasuredDimension(width, chartHeight + 2 * margin);
    }

    private int getChartHeight(int width) {
        return width / 9;
    }

    @SuppressLint("WrongCall")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mayBeInitialize();

        canvas.translate(mConfig.getViewMargin(), mConfig.getViewMargin());

        float dx = getNavigatorVisualShift();
        drawInactiveBackground(canvas, dx);
        drawActiveBackground(canvas, dx);
        drawActiveBorder(canvas, dx);
        mView.onDraw(canvas);
        drawSelection(canvas);
    }

    private void drawInactiveBackground(Canvas canvas, float dx) {
        Range activeRange = mModel.getActiveRange(mShowCase.getDataAnchor());
        if (activeRange == Range.NO_RANGE) {
            return;
        }

        Range wholeRange = mModel.getActiveRange(mView);

        if (wholeRange.getStart() < activeRange.getStart()) {
            float activeXStart = mView.dataPointToVisualPoint(new DataPoint(activeRange.getStart(), 0)).getX();
            canvas.drawRect(0, 0, activeXStart + dx, mView.getHeight(), mInactiveBackgroundPaint);
        }

        if (wholeRange.getEnd() > activeRange.getEnd()) {
            float activeXEnd = mView.dataPointToVisualPoint(new DataPoint(activeRange.getEnd(), 0)).getX();
            canvas.drawRect(activeXEnd + dx, 0, mView.getWidth(), mView.getHeight(), mInactiveBackgroundPaint);
        }
    }

    private void drawActiveBackground(Canvas canvas, float dx) {
        Range activeRange = mModel.getActiveRange(mShowCase.getDataAnchor());
        if (activeRange == Range.NO_RANGE) {
            return;
        }

        float activeXStart = mView.dataXToVisualX(activeRange.getStart());
        float activeXEnd = mView.dataXToVisualX(activeRange.getEnd());
        canvas.drawRect(activeXStart + dx, 0, activeXEnd + dx, mView.getHeight(), mActiveBackgroundPaint);
    }

    private void drawActiveBorder(Canvas canvas, float dx) {
        Range activeRange = mModel.getActiveRange(mShowCase.getDataAnchor());
        if (activeRange == Range.NO_RANGE) {
            return;
        }

        // Left edge
        float activeXStart = mView.dataXToVisualX(activeRange.getStart());
        canvas.drawRect(activeXStart + dx,
                        0,
                        activeXStart + mConfig.getActiveBorderHorizontalWidthInPixels(),
                        mView.getHeight(),
                        mActiveBorderPaint);

        // Right edge
        float activeXEnd = mView.dataXToVisualX(activeRange.getEnd());
        canvas.drawRect(activeXEnd + -mConfig.getActiveBorderHorizontalWidthInPixels() + dx,
                        0,
                        activeXEnd + dx,
                        mView.getHeight(),
                        mActiveBorderPaint);

        // Top edge
        canvas.drawRect(activeXStart + dx,
                        0,
                        activeXEnd + dx,
                        mConfig.getActiveBorderVerticalHeightInPixels(),
                        mActiveBorderPaint);

        // Bottom edge
        canvas.drawRect(activeXStart + dx,
                        mView.getHeight(),
                        activeXEnd + dx,
                        mView.getHeight() - mConfig.getActiveBorderVerticalHeightInPixels(),
                        mActiveBorderPaint);
    }

    private void drawSelection(Canvas canvas) {
        if (mCurrentAction == null) {
            return;
        }

        Range activeRange = mModel.getActiveRange(mShowCase.getDataAnchor());
        if (activeRange == Range.NO_RANGE) {
            return;
        }

        int margin = mConfig.getViewMargin();

        final float x;
        switch (mCurrentAction) {
            case MOVE_ACTIVE_INTERVAL_START:
                x = mView.dataXToVisualX(activeRange.getStart());
                break;
            case MOVE_COMPLETE_ACTIVE_INTERVAL:
                float startX = mView.dataXToVisualX(activeRange.getStart());
                float width = mView.dataXToVisualX(activeRange.getEnd()) - startX;
                x = startX + width / 2;
                break;
            case MOVE_ACTIVE_INTERVAL_END:
                x = mView.dataXToVisualX(activeRange.getEnd());
                break;
            default:
                return;
        }

        final float y = mView.getHeight() / 2f;

        int configuredSelectionOutline = mConfig.getSelectionOutline();
        final float effectiveSelectionOutline;
        if (margin <= 0) {
            effectiveSelectionOutline = 0;
        } else {
            if (configuredSelectionOutline > 0) {
                effectiveSelectionOutline = Math.min(margin, configuredSelectionOutline);
            } else {
                effectiveSelectionOutline = Math.min(margin, mView.getHeight() * 3f / 10);
            }
        }
        final float radius = y + effectiveSelectionOutline - 1;

        canvas.drawCircle(x, y, radius, mSelectionPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                startAction(event.getX());
                break;
            case MotionEvent.ACTION_MOVE:
                move(event.getX());
                break;
            case MotionEvent.ACTION_UP:
                release(event.getX());
                break;
        }
        return true;
    }

    private void startAction(float visualX) {
        mCurrentAction = getActionType(visualX);
        if (mCurrentAction != null) {
            mPreviousActionVisualX = visualX;
            invalidate();
        }
    }

    private void move(final float visualX) {
        Float previousVisualX = mPreviousActionVisualX;
        mPreviousActionVisualX = visualX;

        if (mCurrentAction == null || previousVisualX == null) {
            return;
        }

        float navigatorVisualDeltaX = previousVisualX - visualX;
        if (navigatorVisualDeltaX == 0) {
            return;
        }

        float ratio = getShowCaseVisualRatio();

        final float translatedVisualX = visualX - mConfig.getViewMargin();

        if (mCurrentAction == ActionType.MOVE_COMPLETE_ACTIVE_INTERVAL) {
            mShowCase.scrollHorizontally(navigatorVisualDeltaX * ratio);
            invalidate();
        } else if (mCurrentAction == ActionType.MOVE_ACTIVE_INTERVAL_START) {
            Range showCaseDataRange = mModel.getActiveRange(mShowCase.getDataAnchor());
            float endRangeVisualX = mView.dataXToVisualX(showCaseDataRange.getEnd());
            if (endRangeVisualX - translatedVisualX < MIN_WIDTH_IN_PIXELS) {
                // Don't allow selector to become too narrow
                return;
            }
            long newStartDataX = mView.visualXToDataX(Math.max(translatedVisualX, 0));
            if (newStartDataX != showCaseDataRange.getStart()) {
                mModel.setActiveRange(new Range(newStartDataX, showCaseDataRange.getEnd()), mShowCase.getDataAnchor());
            }
        } else {
            Range showCaseDataRange = mModel.getActiveRange(mShowCase.getDataAnchor());
            Range myDataRange = mModel.getActiveRange(getModelAnchor());
            float startRangeVisualX = mView.dataXToVisualX(showCaseDataRange.getStart());
            if (translatedVisualX - startRangeVisualX < MIN_WIDTH_IN_PIXELS) {
                // Don't allow selector to become too narrow
                return;
            }
            long newEndDataX = mView.visualXToDataX(Math.max(translatedVisualX, 0));
            if (newEndDataX <= myDataRange.getEnd() && newEndDataX != showCaseDataRange.getEnd()) {
                mModel.setActiveRange(new Range(showCaseDataRange.getStart(), newEndDataX), mShowCase.getDataAnchor());
            }
        }
    }

    private void release(float visualX) {
        move(visualX);
        mCurrentAction = null;
        mPreviousActionVisualX = null;
        invalidate();
    }

    @Nullable
    private ActionType getActionType(float x) {
        Range activeRange = mModel.getActiveRange(mShowCase.getDataAnchor());
        if (activeRange == Range.NO_RANGE) {
            return null;
        }

        float dx = getNavigatorVisualShift();
        float startX = mView.dataXToVisualX(activeRange.getStart()) + dx + mConfig.getViewMargin();
        float endX = mView.dataXToVisualX(activeRange.getEnd()) + dx + mConfig.getViewMargin();

        if (x + CLICK_RECOGNITION_ERROR_IN_PIXELS < startX || x - CLICK_RECOGNITION_ERROR_IN_PIXELS > endX) {
            return null;
        }

        if (Math.abs(startX - x) < CLICK_RECOGNITION_ERROR_IN_PIXELS) {
            return ActionType.MOVE_ACTIVE_INTERVAL_START;
        } else if (Math.abs(endX - x) < CLICK_RECOGNITION_ERROR_IN_PIXELS) {
            return ActionType.MOVE_ACTIVE_INTERVAL_END;
        } else {
            return ActionType.MOVE_COMPLETE_ACTIVE_INTERVAL;
        }
    }

    private float getShowCaseVisualRatio() {
        Range navigatorDataRange = mModel.getActiveRange(getModelAnchor());
        Range showCaseDataRange = mModel.getActiveRange(mShowCase.getDataAnchor());
        return (navigatorDataRange.getPointsNumber() - 1f) / (showCaseDataRange.getPointsNumber() - 1f);
    }

    private float getNavigatorVisualShift() {
        float dx = 0;
        float showCaseVisualXShift = mShowCase.getVisualXShift();
        if (showCaseVisualXShift != 0) {
            dx = showCaseVisualXShift / getShowCaseVisualRatio();
        }
        return -dx;
    }

    private enum ActionType {
        MOVE_COMPLETE_ACTIVE_INTERVAL, MOVE_ACTIVE_INTERVAL_START, MOVE_ACTIVE_INTERVAL_END
    }
}
