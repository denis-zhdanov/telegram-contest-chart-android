package tech.harmonysoft.android.leonardo.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.*;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.View;
import tech.harmonysoft.android.leonardo.log.LogUtil;
import tech.harmonysoft.android.leonardo.model.*;
import tech.harmonysoft.android.leonardo.model.axis.AxisLabelTextStrategy;
import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfig;
import tech.harmonysoft.android.leonardo.model.config.chart.ChartConfig;
import tech.harmonysoft.android.leonardo.model.data.ChartDataSource;
import tech.harmonysoft.android.leonardo.model.runtime.ChartModel;
import tech.harmonysoft.android.leonardo.model.runtime.ChartModelListener;
import tech.harmonysoft.android.leonardo.model.text.TextWrapper;
import tech.harmonysoft.android.leonardo.util.IntSupplier;
import tech.harmonysoft.android.leonardo.util.LeonardoUtil;
import tech.harmonysoft.android.leonardo.view.util.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

import static tech.harmonysoft.android.leonardo.util.LeonardoUtil.ANIMATION_DURATION_MILLIS;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public class ChartView extends View {

    private static final Typeface TYPEFACE_BOLD = Typeface.create(Typeface.DEFAULT, Typeface.BOLD);

    private static final Comparator<ChartDataSource> COMPARATOR = (s1, s2) -> s1.getLegend().compareTo(s2.getLegend());

    private final AxisStepChooser mAxisStepChooser = new AxisStepChooser();

    private final Handler  mHandler    = new Handler(Looper.getMainLooper());
    private final Runnable mRedrawTask = this::invalidate;

    private final Map<ChartDataSource, DataSourceAnimationContext> mAnimationDataSourceInfo = new HashMap<>();

    /**
     * Keep own data sources list in order to work with them in lexicographically, e.g. when showing selection legend
     */
    private final List<ChartDataSource>          mDataSources            = new ArrayList<>();
    private final AxisStepChooser.MinGapStrategy mXAxisGapStrategy       = new XAxisLabelGapStrategy();
    private final RoundedRectangleDrawer         mRoundedRectangleDrawer = new RoundedRectangleDrawer();
    private final AxisStepChooser.MinGapStrategy mYAxisGapStrategy       = new YAxisLabelGapStrategy(new IntSupplier() {
        @Override
        public int get() {
            return mYAxisLabelVerticalPadding;
        }
    });

    private final ChartModelListener mModelListener = new ChartModelListener() {
        @Override
        public void onRangeChanged(Object anchor) {
            if (anchor == getDataAnchor()) {
                refreshXAxisSetupIfNecessary();
                invalidate();
            }
        }

        @Override
        public void onDataSourceEnabled(ChartDataSource dataSource) {
            startDataSourceFadeInAnimation(dataSource);
        }

        @Override
        public void onDataSourceDisabled(ChartDataSource dataSource) {
            startDataSourceFadeOutAnimation(dataSource);
        }

        @Override
        public void onDataSourceAdded(ChartDataSource dataSource) {
            refreshDataSources();
            startDataSourceFadeInAnimation(dataSource);
            invalidate();
        }

        @Override
        public void onDataSourceRemoved(ChartDataSource dataSource) {
            refreshDataSources();
            stopDataSourceFadeAnimation(dataSource);
            invalidate();
        }

        @Override
        public void onActiveDataPointsLoaded(Object anchor) {
            if (anchor == getDataAnchor()) {
                invalidate();
            }
        }

        @Override
        public void onSelectionChange() {
            invalidate();
        }
    };

    private ChartConfig mChartConfig;
    private boolean     mConfigApplied;

    private ChartModel mChartModel;

    private Paint mBackgroundPaint;
    private Paint mGridPaint;
    private Paint mPlotPaint;

    private Paint             mLegendValuePaint;
    private TextSpaceMeasurer mLegendValueWidthMeasurer;
    private int               mLegendValueHeight;

    private AxisConfig        mXAxisConfig;
    private Paint             mXLabelPaint;
    private TextSpaceMeasurer mXAxisLabelWidthMeasurer;
    private Range             mCurrentXRange;
    private int               mXAxisLabelPadding;
    private int               mXAxisLabelHeight;
    private long              mXAxisStep;
    private float             mXUnitVisualWidth;
    private float             mXVisualShift;
    private boolean           mPendingXRescale;

    private AxisConfig        mYAxisConfig;
    private Paint             mYLabelPaint;
    private Range             mCurrentYRange;
    private TextSpaceMeasurer mYAxisLabelWidthMeasurer;
    private TextSpaceMeasurer mYAxisLabelHeightMeasurer;
    private int               mYAxisLabelHeight;
    private int               mYAxisLabelHorizontalPadding;
    private int               mYAxisLabelVerticalPadding;
    private long              mYAxisStep;
    private int               mMaxYLabelWidth;

    private float mLastClickVisualX;
    private float mLastClickVisualY;

    private RectF mLegendRect;

    private long  mYAnimationFirstDataValue;
    private long  mYAnimationAxisStep;
    private float mYAnimationInitialUnitHeight;
    private float mYAnimationCurrentUnitHeight;
    private float mYAnimationFinalUnitHeight;
    private long  mYAnimationStartTimeMs;
    private int   mYAnimationOngoingLabelAlpha;
    private int   mYAnimationOngoingGridAlpha;
    private int   mYAnimationFinalLabelAlpha;
    private int   mYAnimationFinalGridAlpha;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        setOnTouchListener((v, event) -> {
            mLastClickVisualX = event.getX();
            mLastClickVisualY = event.getY();
            return false;
        });
        setOnClickListener(v -> {
            if (mLegendRect != null
                && mChartModel != null
                && mLegendRect.contains(mLastClickVisualX, mLastClickVisualY))
            {
                mChartModel.resetSelection();
                return;
            }

            if (mChartConfig == null || mChartModel == null || !mChartConfig.isSelectionAllowed()) {
                return;
            }

            mChartModel.setSelectedX(visualXToDataX(mLastClickVisualX));
        });
    }

    @Nonnull
    public Object getDataAnchor() {
        return this;
    }

    public void apply(ChartConfig config) {
        mChartConfig = config;
        mConfigApplied = false;
        applyConfig();
    }

    private void applyConfig() {
        if (mConfigApplied) {
            return;
        }
        if (mChartConfig == null) {
            throw new IllegalStateException("Chart view config is not set");
        }

        initBackground();
        initLegend();
        initGrid();
        initXAxis();
        initYAxis();
        initPlot();

        mConfigApplied = true;
        invalidate();
    }

    private void initBackground() {
        Paint paint = new Paint();
        paint.setColor(mChartConfig.getBackgroundColor());
        paint.setStyle(Paint.Style.FILL);
        mBackgroundPaint = paint;
    }

    private void initLegend() {
        Paint paint = new Paint();
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(mChartConfig.getYAxisConfig().getFontSizeInPixels() * 3 / 2);
        mLegendValuePaint = paint;
        Paint.FontMetrics fontMetrics = mLegendValuePaint.getFontMetrics();
        mLegendValueHeight = (int) (fontMetrics.descent - fontMetrics.ascent);
        mLegendValueWidthMeasurer = new TextWidthMeasurer(mLegendValuePaint);
    }

    private void initGrid() {
        Paint paint = new Paint();
        paint.setColor(mChartConfig.getGridColor());
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(mChartConfig.getGridLineWidthInPixels());
        mGridPaint = paint;
    }

    private void initXAxis() {
        mXAxisConfig = mChartConfig.getXAxisConfig();
        mXLabelPaint = getAxisLabelPaint(mChartConfig.getXAxisConfig());
        mXAxisLabelWidthMeasurer = new TextWidthMeasurer(mXLabelPaint);
        Paint.FontMetrics fontMetrics = mXLabelPaint.getFontMetrics();
        mXAxisLabelHeight = (int) (fontMetrics.descent - fontMetrics.ascent);
        mXAxisLabelPadding = mXAxisLabelHeight / 2;
    }

    private void initYAxis() {
        mYAxisConfig = mChartConfig.getYAxisConfig();
        mYLabelPaint = getAxisLabelPaint(mChartConfig.getYAxisConfig());
        mYAxisLabelWidthMeasurer = new TextWidthMeasurer(mYLabelPaint);
        mYAxisLabelHeightMeasurer = new TextHeightMeasurer(mYLabelPaint);
        Paint.FontMetrics fontMetrics = mYLabelPaint.getFontMetrics();
        mYAxisLabelHeight = (int) (fontMetrics.descent - fontMetrics.ascent);
        mYAxisLabelVerticalPadding = mYAxisLabelHeight * 3 / 5;
        mYAxisLabelHorizontalPadding = mYAxisLabelVerticalPadding;
    }

    @Nonnull
    private Paint getAxisLabelPaint(AxisConfig config) {
        Paint paint = new Paint();
        paint.setColor(config.getLabelColor());
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextSize(config.getFontSizeInPixels());
        return paint;
    }

    private void initPlot() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStrokeWidth(mChartConfig.getPlotLineWidthInPixels());
        mPlotPaint = paint;
    }

    public void apply(ChartModel chartModel) {
        if (chartModel != mChartModel) {
            if (mChartModel != null) {
                mChartModel.removeListener(mModelListener);
            }
            chartModel.addListener(mModelListener);
        }
        mChartModel = chartModel;
        refreshDataSources();
        mCurrentXRange = null;
        mCurrentYRange = null;
        invalidate();
    }

    private void refreshDataSources() {
        mDataSources.clear();
        mDataSources.addAll(mChartModel.getRegisteredDataSources());
        Collections.sort(mDataSources, COMPARATOR);
    }

    public int getChartBottom() {
        int result = getHeight() - mChartConfig.getInsetsInPixels();
        if (mXAxisConfig.shouldDrawAxis() || mXAxisConfig.shouldDrawLabels()) {
            result -= mXAxisLabelHeight + mXAxisLabelPadding;
        }
        return result;
    }

    public int getChartLeft() {
        return mChartConfig.getInsetsInPixels();
    }

    public int getChartRight() {
        return getWidth() - mChartConfig.getInsetsInPixels();
    }

    public int getChartTop() {
        return mChartConfig.getInsetsInPixels();
    }

    public int getChartHeight() {
        return getChartBottom() - getChartTop();
    }

    public int getChartWidth() {
        return getChartRight() - getChartLeft();
    }

    public float getXVisualShift() {
        return mXVisualShift;
    }

    public int getPlotLeft() {
        return getChartLeft() + mMaxYLabelWidth + mYAxisLabelHorizontalPadding;
    }

    public int getPlotRight() {
        return getChartRight();
    }

    @Nonnull
    public VisualPoint dataPointToVisualPoint(DataPoint point) {
        Range yRange = mCurrentYRange;
        if (yRange == null) {
            yRange = getCurrentYRange();
        }

        final float yUnitHeight;
        if (isYRescaleAnimationInProgress()) {
            yUnitHeight = mYAnimationCurrentUnitHeight;
        } else {
            yUnitHeight = getChartHeight() / (yRange.getPointsNumber() - 1f);
        }

        float y = getChartBottom() - (point.getY() - yRange.getStart()) * yUnitHeight;

        return new VisualPoint(dataXToVisualX(point.getX()), y);
    }

    public float dataXToVisualX(long dataX) {
        refreshXAxisSetupIfNecessary();
        Range xRange = mCurrentXRange;
        return getChartLeft() + mXVisualShift + (dataX - xRange.getStart()) * mXUnitVisualWidth;
    }

    public long visualXToDataX(float visualX) {
        refreshXAxisSetupIfNecessary();
        Range xRange = mCurrentXRange;
        return (long) ((visualX - getChartLeft() - mXVisualShift) / mXUnitVisualWidth + xRange.getStart());
    }

    public void scrollHorizontally(final float deltaVisualX) {
        float effectiveDeltaVisualX = deltaVisualX + mXVisualShift;
        refreshXAxisSetupIfNecessary();
        Range currentXRange = mCurrentXRange;
        long deltaDataX = (long) (-effectiveDeltaVisualX / mXUnitVisualWidth);

        long minDataX = Long.MAX_VALUE;
        long maxDataX = Long.MIN_VALUE;
        for (ChartDataSource activeDataSource : mDataSources) {
            Range range = activeDataSource.getDataRange();
            if (range.getStart() > Long.MIN_VALUE && range.getStart() < minDataX) {
                minDataX = range.getStart();
            }
            if (range.getEnd() < Long.MAX_VALUE && range.getEnd() > maxDataX) {
                maxDataX = range.getEnd();
            }
        }

        if ((deltaDataX > 0 && currentXRange.contains(maxDataX))
            || (deltaDataX < 0 && currentXRange.contains(minDataX)))
        {
            // We can't scroll more as we're already at the min/max possible edge
            return;
        }

        mXVisualShift = effectiveDeltaVisualX % mXUnitVisualWidth;
        LogUtil.debug(this,
                      "scrollHorizontally(): given delta visual X=%f, effective delta visual X=%f, "
                      + "X unit visual width=%f, delta data X=%d, new visual X shift=%f",
                      deltaVisualX, effectiveDeltaVisualX, mXUnitVisualWidth, deltaDataX, mXVisualShift);
        if (deltaDataX != 0) {
            mChartModel.setActiveRange(currentXRange.shift(deltaDataX), getDataAnchor());
        }
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int edge = MeasureSpec.getSize(widthMeasureSpec);
        setMeasuredDimension(edge, edge);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        applyConfig();
        mayBeTickAnimations();
        drawBackground(canvas);
        drawGrid(canvas);
        drawPlots(canvas);
        drawSelection(canvas);
    }

    private void drawBackground(Canvas canvas) {
        if (mChartConfig.isDrawBackground()) {
            mBackgroundPaint.setColor(mChartConfig.getBackgroundColor());
            canvas.drawPaint(mBackgroundPaint);
        }
    }

    private void drawGrid(Canvas canvas) {
        drawXAxis(canvas);
        drawYAxis(canvas);
    }

    private void drawXAxis(Canvas canvas) {
        refreshXAxisSetupIfNecessary();
        drawXAxisLine(canvas);
        drawXAxisLabels(canvas);
    }

    /**
     * Is assumed to be called when {@link #mXAxisLabelWidthMeasurer} is initialized and {@link #getWidth()} is known.
     */
    private void refreshXAxisSetupIfNecessary() {
        boolean rescale = false;
        Range activeRange = mChartModel.getActiveRange(getDataAnchor());
        if (mPendingXRescale
            || mCurrentXRange == null
            || activeRange.getPointsNumber() != mCurrentXRange.getPointsNumber())
        {
            rescale = true;
        }

        mCurrentXRange = activeRange;

        if (rescale) {
            if (mXAxisConfig == null || getChartWidth() <= 0) {
                mPendingXRescale = true;
            } else {
                mPendingXRescale = false;
                AxisLabelTextStrategy labelTextStrategy = mXAxisConfig.getLabelTextStrategy();
                mXAxisStep = mAxisStepChooser.choose(labelTextStrategy,
                                                     mXAxisGapStrategy,
                                                     mCurrentXRange,
                                                     getWidth(),
                                                     mXAxisLabelWidthMeasurer);
                mXUnitVisualWidth = getChartWidth() / (mCurrentXRange.getPointsNumber() - 1f);
                mXVisualShift = 0;
                LogUtil.debug(this,
                              "refreshXAxisSetupIfNecessary(): setting X unit visual width to %f. Current chart "
                              + "width=%d, current data range=%s", mXUnitVisualWidth, getChartWidth(), mCurrentXRange);
            }
        }
    }

    private void drawXAxisLine(Canvas canvas) {
        if (!mXAxisConfig.shouldDrawAxis()) {
            return;
        }
        int y = getChartBottom();
        Rect clipBounds = canvas.getClipBounds();
        if (y <= clipBounds.bottom) {
            canvas.drawLine(mChartConfig.getInsetsInPixels(),
                            y,
                            getChartRight(),
                            y,
                            mGridPaint);
        }
    }

    private void drawXAxisLabels(Canvas canvas) {
        if (!mXAxisConfig.shouldDrawLabels()) {
            return;
        }
        Rect clipBounds = canvas.getClipBounds();
        int height = getHeight();
        int minY = height - mChartConfig.getInsetsInPixels() - mXAxisLabelHeight;
        if (clipBounds.bottom <= minY) {
            return;
        }

        AxisLabelTextStrategy labelTextStrategy = mXAxisConfig.getLabelTextStrategy();
        refreshXAxisSetupIfNecessary();
        Range range = mCurrentXRange;
        int y = height - mChartConfig.getInsetsInPixels();
        for (long value = range.findFirstStepValue(mXAxisStep); range.contains(value); value += mXAxisStep) {
            float xAnchor = getChartLeft() + mXVisualShift + (mXUnitVisualWidth * (value - range.getStart()));
            TextWrapper label = labelTextStrategy.getLabel(value, mXAxisStep);
            canvas.drawText(label.getData(), 0, label.getLength(), xAnchor, y, mXLabelPaint);
        }
    }

    private void drawYAxis(Canvas canvas) {
        refreshYAxisSetupIfNecessary();
        drawYGrid(canvas);
    }

    private void refreshYAxisSetupIfNecessary() {
        Range previousRange = mCurrentYRange;
        Range currentYRange = getCurrentYRange();
        if (currentYRange == Range.NO_RANGE) {
            mCurrentYRange = currentYRange;
            return;
        }

        boolean rescale = false;
        if (mCurrentYRange == null || mCurrentYRange.getPointsNumber() != currentYRange.getPointsNumber()) {
            rescale = true;
        }

        mCurrentYRange = currentYRange;

        if (rescale) {
            AxisLabelTextStrategy labelTextStrategy = mYAxisConfig.getLabelTextStrategy();
            Range nonPaddedRange = getCurrentYRange(false);
            long newYAxisStep = mAxisStepChooser.choose(labelTextStrategy,
                                                        mYAxisGapStrategy,
                                                        nonPaddedRange,
                                                        getChartHeight(),
                                                        mYAxisLabelHeightMeasurer);

            Range currentRange = mayBeExpandYRange(nonPaddedRange);
            float unitHeight = getChartHeight() / (currentRange.getPointsNumber() - 1f);
            long previousStep = mYAxisStep;

            mYAxisStep = newYAxisStep;
            mCurrentYRange = mayBeExpandYRange(nonPaddedRange);

            if (isYRescaleAnimationInProgress()) {
                if (unitHeight != mYAnimationFinalUnitHeight) {
                    startYRescaleAnimation(previousRange, previousStep, mCurrentYRange);
                }
            } else if (previousRange != null && previousStep > 0 && !previousRange.equals(currentRange)) {
                startYRescaleAnimation(previousRange, previousStep, mCurrentYRange);
            }
        }
    }

    @Nonnull
    private Range getCurrentYRange() {
        return getCurrentYRange(true);
    }

    @Nonnull
    private Range getCurrentYRange(boolean padByStepSize) {
        long min = Long.MAX_VALUE;
        long max = Long.MIN_VALUE;
        for (ChartDataSource dataSource : mDataSources) {
            if (!mChartModel.isActive(dataSource)) {
                continue;
            }

            if (mChartModel.arePointsForActiveRangeLoaded(dataSource, getDataAnchor())) {
                Interval interval = mChartModel.getCurrentRangePoints(dataSource, getDataAnchor());
                for (DataPoint point : interval.getPoints()) {
                    if (min > point.getY()) {
                        min = point.getY();
                    }
                    if (max < point.getY()) {
                        max = point.getY();
                    }
                }
            }
        }

        if (min > max) {
            return Range.NO_RANGE;
        }

        Range range = new Range(min, max);
        if (padByStepSize) {
            return mayBeExpandYRange(range);
        } else {
            return range;
        }
    }

    /**
     * There is a possible case that current Y range is not divisible by step size, e.g. current range
     * is {@code [4; 24]} and current step is {@code 10}. We want to expand the range to {@code [0; 30]} then
     * in order to draw Y axis labels through the same intervals.
     *
     * @param range Y range to process
     * @return Y range to use
     */
    @Nonnull
    private Range mayBeExpandYRange(Range range) {
        if (mYAxisStep <= 0 || !mYAxisConfig.shouldDrawAxis()) {
            return range;
        }
        return range.padBy(mYAxisStep);
    }

    private void drawYGrid(Canvas canvas) {
        if (!mYAxisConfig.shouldDrawAxis() || mCurrentYRange == Range.NO_RANGE) {
            mMaxYLabelWidth = 0;
            return;
        }

        mYLabelPaint.setColor(mYAxisConfig.getLabelColor());
        mYLabelPaint.setTypeface(Typeface.DEFAULT);

        if (isYRescaleAnimationInProgress()) {
            drawYGrid(canvas,
                      mYAnimationFirstDataValue,
                      mYAnimationAxisStep,
                      mYAnimationCurrentUnitHeight,
                      mYAnimationOngoingGridAlpha,
                      mYAnimationOngoingLabelAlpha);
            drawYGrid(canvas,
                      mCurrentYRange.findFirstStepValue(mYAxisStep),
                      mYAxisStep,
                      getChartHeight() / (mCurrentYRange.getPointsNumber() - 1f),
                      mYAnimationFinalGridAlpha,
                      mYAnimationFinalLabelAlpha);
        } else {
            drawYGrid(canvas,
                      mCurrentYRange.findFirstStepValue(mYAxisStep),
                      mYAxisStep,
                      getChartHeight() / (mCurrentYRange.getPointsNumber() - 1f),
                      255,
                      255);
        }
    }

    private void drawYGrid(Canvas canvas,
                           long firstValue,
                           long dataStep,
                           float unitHeight,
                           int gridAlpha,
                           int labelAlpha)
    {
        mGridPaint.setAlpha(gridAlpha);
        mYLabelPaint.setAlpha(labelAlpha);
        for (long value = firstValue; ; value += dataStep) {
            int y = (int) (getChartBottom() - unitHeight * (value - mCurrentYRange.getStart()));
            if (y <= getChartTop()) {
                break;
            }
            canvas.drawLine(getChartLeft(), y, getChartRight(), y, mGridPaint);
            TextWrapper label = mYAxisConfig.getLabelTextStrategy().getLabel(value, mYAxisStep);
            y -= mYAxisLabelVerticalPadding;
            if (y - mYAxisLabelHeight <= getChartTop()) {
                break;
            }
            if (mYAxisConfig.shouldDrawLabels()) {
                canvas.drawText(label.getData(), 0, label.getLength(), getChartLeft(), y, mYLabelPaint);
            }
            mMaxYLabelWidth = Math.max(mMaxYLabelWidth, mYAxisLabelWidthMeasurer.measureVisualSpace(label));
        }
    }

    private void drawPlots(Canvas canvas) {
        for (ChartDataSource dataSource : mDataSources) {
            drawPlot(dataSource, canvas);
        }
    }

    private void drawPlot(ChartDataSource dataSource, Canvas canvas) {
        mPlotPaint.setColor(dataSource.getColor());
        DataSourceAnimationContext animationContext = mAnimationDataSourceInfo.get(dataSource);
        if (animationContext == null) {
            if (!mChartModel.isActive(dataSource)) {
                return;
            } else {
                mPlotPaint.setAlpha(255);
            }
        } else {
            mPlotPaint.setAlpha(animationContext.currentAlpha);
        }
        mPlotPaint.setStyle(Paint.Style.STROKE);
        Interval interval = mChartModel.getCurrentRangePoints(dataSource, getDataAnchor());

        float minX = getPlotLeft();
        float maxX = getPlotRight();

        List<DataPoint> points = new ArrayList<>(interval.getPoints().size() + 2);
        DataPoint previous = mChartModel.getPreviousPointForActiveRange(dataSource, getDataAnchor());
        if (previous != null) {
            points.add(previous);
        }
        points.addAll(interval.getPoints());
        DataPoint next = mChartModel.getNextPointForActiveRange(dataSource, getDataAnchor());
        if (next != null) {
            points.add(next);
        }

        Path path = new Path();
        boolean first = true;
        boolean stop = false;
        VisualPoint previousVisualPoint = null;
        for (int i = 0; !stop && i < points.size(); i++) {
            DataPoint point = points.get(i);
            VisualPoint visualPoint = dataPointToVisualPoint(point);
            if (i == 0) {
                previousVisualPoint = visualPoint;
                continue;
            }

            if (previousVisualPoint.getX() < minX && visualPoint.getX() <= minX) {
                previousVisualPoint = visualPoint;
                continue;
            }

            float x;
            float y;
            if (previousVisualPoint.getX() < minX) {
                LineFormula formula = calculateLineFormula(previousVisualPoint, visualPoint);
                x = minX;
                y = formula.getY(x);
                if (y > getChartBottom()) {
                    y = getChartBottom();
                    x = formula.getX(y);
                }
            } else if (visualPoint.getX() > maxX) {
                if (first) {
                    first = false;
                    path.moveTo(previousVisualPoint.getX(), previousVisualPoint.getY());
                } else {
                    path.lineTo(previousVisualPoint.getX(), previousVisualPoint.getY());
                }
                LineFormula formula = calculateLineFormula(previousVisualPoint, visualPoint);
                x = maxX;
                y = formula.getY(x);
                if (y > getChartBottom()) {
                    y = getChartBottom();
                    x = formula.getX(y);
                }
                stop = true;
            } else {
                x = previousVisualPoint.getX();
                y = previousVisualPoint.getY();
            }

            if (first) {
                first = false;
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }

            if (!stop && i == points.size() - 1) {
                float endX;
                float endY;
                if (visualPoint.getX() <= maxX) {
                    endX = visualPoint.getX();
                    endY = visualPoint.getY();
                } else {
                    LineFormula formula = calculateLineFormula(previousVisualPoint, visualPoint);
                    endX = maxX;
                    endY = formula.getY(endX);
                    if (endY > getChartBottom()) {
                        endY = getChartBottom();
                        endX = formula.getX(endY);
                    }
                }
                path.lineTo(endX, endY);
            }

            previousVisualPoint = visualPoint;
        }

        canvas.drawPath(path, mPlotPaint);
    }

    @Nonnull
    private LineFormula calculateLineFormula(VisualPoint p1, VisualPoint p2) {
        float a = (p1.getY() - p2.getY()) / (p1.getX() - p2.getX());
        float b = p1.getY() - a * p1.getX();
        return new LineFormula(a, b);
    }

    private void drawSelection(Canvas canvas) {
        if (!mChartConfig.isDrawSelection() || !mChartModel.hasSelection()) {
            return;
        }

        mLegendRect = null;

        long dataX = mChartModel.getSelectedX();
        float visualX = dataXToVisualX(dataX);
        if (visualX < getChartLeft() || visualX > getChartRight()) {
            return;
        }

        canvas.drawLine(visualX, getChartBottom(), visualX, getChartTop(), mGridPaint);

        Map<ChartDataSource, ValueInfo> dataSource2yInfo = new HashMap<>();
        for (ChartDataSource dataSource : mDataSources) {
            if (!mChartModel.isActive(dataSource)) {
                continue;
            }
            List<DataPoint> points = mChartModel.getCurrentRangePoints(dataSource, getDataAnchor()).getPoints();
            if (points.isEmpty()) {
                continue;
            }

            long dataY;

            DataPoint firstDataPoint = points.get(0);
            DataPoint lastDataPoint = points.get(points.size() - 1);
            if (dataX < firstDataPoint.getX()) {
                DataPoint previousDataPoint = mChartModel.getPreviousPointForActiveRange(dataSource, getDataAnchor());
                if (previousDataPoint == null) {
                    continue;
                }
                LineFormula formula = calculateLineFormula(new VisualPoint(previousDataPoint.getX(),
                                                                           previousDataPoint.getY()),
                                                           new VisualPoint(firstDataPoint.getX(),
                                                                           firstDataPoint.getY()));
                dataY = Math.round((double) formula.getY(dataX));
            } else if (dataX > lastDataPoint.getX()) {
                DataPoint nextDataPoint = mChartModel.getNextPointForActiveRange(dataSource, getDataAnchor());
                if (nextDataPoint == null) {
                    continue;
                }
                LineFormula formula = calculateLineFormula(new VisualPoint(lastDataPoint.getX(),
                                                                           lastDataPoint.getY()),
                                                           new VisualPoint(nextDataPoint.getX(),
                                                                           nextDataPoint.getY()));
                dataY = Math.round((double) formula.getY(dataX));
            } else {
                int i = Collections.binarySearch(points, new DataPoint(dataX, 0), DataPoint.COMPARATOR_BY_X);
                if (i >= 0) {
                    dataY = points.get(i).getY();
                } else {
                    i = -(i + 1);
                    DataPoint prev = points.get(i - 1);
                    DataPoint next = points.get(i);
                    LineFormula formula = calculateLineFormula(new VisualPoint(prev.getX(), prev.getY()),
                                                               new VisualPoint(next.getX(), next.getY()));
                    dataY = Math.round((double) formula.getY(dataX));
                }
            }

            VisualPoint visualPoint = dataPointToVisualPoint(new DataPoint(dataX, dataY));
            dataSource2yInfo.put(dataSource, new ValueInfo(dataY, visualPoint.getY()));
            int yShift = mChartConfig.getPlotLineWidthInPixels() / 2;
            drawSelectionPlotSign(canvas,
                                  new VisualPoint(visualPoint.getX(), visualPoint.getY() - yShift),
                                  dataSource.getColor());
        }

        drawSelectionLegend(canvas, visualX, dataSource2yInfo);
    }

    private void drawSelectionPlotSign(Canvas canvas, VisualPoint point, int color) {
        mPlotPaint.setColor(mChartConfig.getBackgroundColor());
        mPlotPaint.setStyle(Paint.Style.FILL);
        canvas.drawCircle(point.getX(), point.getY(), mChartConfig.getSelectionSignRadiusInPixels(), mPlotPaint);

        mPlotPaint.setColor(color);
        mPlotPaint.setStyle(Paint.Style.STROKE);
        canvas.drawCircle(point.getX(), point.getY(), mChartConfig.getSelectionSignRadiusInPixels(), mPlotPaint);
    }

    private void drawSelectionLegend(Canvas canvas, float x, Map<ChartDataSource, ValueInfo> dataSource2yInfo) {
        LegendDrawContext context = new LegendDrawContext(dataSource2yInfo);
        boolean shouldDraw = fillLegendInternalHorizontalData(context)
                             && fillLegendInternalVerticalData(context)
                             && locateLegend(context, x, dataSource2yInfo);
        if (shouldDraw) {
            doDrawLegend(canvas, context);
        }
    }

    private boolean fillLegendInternalHorizontalData(LegendDrawContext context) {
        if (context.dataSource2yInfo.isEmpty()) {
            return false;
        }
        context.horizontalPadding = mYAxisLabelHeight * 7 / 5;

        TextWrapper legendTitle = mXAxisConfig.getLabelTextStrategy().getLabel(mChartModel.getSelectedX(), mXAxisStep);
        int legendTitleWidth = mYAxisLabelWidthMeasurer.measureVisualSpace(legendTitle);
        if (legendTitleWidth + context.horizontalPadding * 2 > getWidth()) {
            context.tooNarrow = true;
            return true;
        }

        int legendTextWidth = measureLegendTextWidth(context, false);
        if (legendTextWidth < 0) {
            legendTextWidth = measureLegendTextWidth(context, true);
            if (legendTextWidth > 0) {
                context.minifiedValues = true;
            }
        }

        if (legendTextWidth < 0) {
            context.tooNarrow = true;
        }

        context.legendWidth = Math.max(legendTitleWidth, legendTextWidth) + context.horizontalPadding * 2;
        return true;
    }

    private int measureLegendTextWidth(LegendDrawContext context, boolean minifyValues) {
        boolean first = true;
        int legendTextWidth = 0;
        for (ChartDataSource dataSource : mDataSources) {
            if (!mChartModel.isActive(dataSource)) {
                continue;
            }
            ValueInfo valueInfo = context.dataSource2yInfo.get(dataSource);
            if (valueInfo == null) {
                continue;
            }
            long dataY = valueInfo.dataValue;
            final TextWrapper value;
            if (minifyValues) {
                value = mYAxisConfig.getLabelTextStrategy().getMinifiedLabel(dataY, mYAxisStep);
            } else {
                value = mYAxisConfig.getLabelTextStrategy().getLabel(dataY, mYAxisStep);
            }
            int valueWidth = mLegendValueWidthMeasurer.measureVisualSpace(value);
            int legendWidth = mYAxisLabelWidthMeasurer.measureVisualSpace(dataSource.getLegend());
            legendTextWidth += Math.max(valueWidth, legendWidth);
            if (first) {
                first = false;
            } else {
                legendTextWidth += context.horizontalPadding;
            }

            if (legendTextWidth + context.horizontalPadding * 2 > getWidth()) {
                return -1;
            }
        }

        return legendTextWidth;
    }

    private boolean fillLegendInternalVerticalData(LegendDrawContext context) {
        context.verticalPadding = mYAxisLabelHeight;
        context.titleYShift = context.verticalPadding + mYAxisLabelHeight;
        context.valueYShift = context.titleYShift + mLegendValueHeight * 2;
        context.legendYShift = context.valueYShift + mYAxisLabelHeight * 2 / 3 + mYAxisLabelHeight;
        context.legendHeight = context.legendYShift + context.verticalPadding;
        return context.legendHeight <= getHeight();
    }

    private boolean locateLegend(LegendDrawContext context,
                                 float selectionVisualX,
                                 Map<ChartDataSource, ValueInfo> dataSource2yInfo)
    {
        // Ideally we'd like to show legend above selection
        List<Float> selectionYs = new ArrayList<>();
        for (ValueInfo valueInfo : dataSource2yInfo.values()) {
            selectionYs.add(valueInfo.visualValue);
        }
        Collections.sort(selectionYs);

        float topLimit = 0;
        float legendTop = -1;
        for (Float selectionY : selectionYs) {
            if (context.legendHeight + context.verticalPadding * 2 <= selectionY - topLimit) {
                legendTop = selectionY - context.verticalPadding - context.legendHeight;
                break;
            } else {
                topLimit = selectionY;
            }
        }

        if (context.legendHeight + context.verticalPadding * 2 <= getChartBottom() - topLimit) {
            legendTop = topLimit + context.verticalPadding;
        }

        if (legendTop >= 0) {
            // Show legend above the selected X in a way to not hiding selection points.
            context.topOnChart = legendTop;
            if (context.tooNarrow) {
                context.leftOnChart = context.horizontalPadding;
                context.legendWidth = getWidth();
            } else {
                float desiredLeft = selectionVisualX - context.legendWidth * 5f / 28f;
                context.leftOnChart = normalizeLeft(Math.max(0, desiredLeft), context.legendWidth);
            }
            return true;
        }

        // We can't show legend above the selected X without hiding one or more selection points.
        if (context.tooNarrow) {
            context.leftOnChart = context.horizontalPadding;
            context.legendWidth = getWidth();
            context.topOnChart = context.verticalPadding;
            return true;
        }

        if (selectionVisualX + context.horizontalPadding + context.legendWidth <= getWidth()) {
            // We can show legend to the right of selected x
            context.leftOnChart = selectionVisualX + context.horizontalPadding;
            context.topOnChart = Math.max(0, selectionYs.get(0) - context.legendHeight * 5f / 28f);
            return true;
        }

        if (selectionVisualX - context.horizontalPadding - context.legendWidth >= 0) {
            // We can show legend to the left of selected x
            context.leftOnChart = selectionVisualX - context.horizontalPadding - context.legendWidth;
            context.topOnChart = Math.max(0, selectionYs.get(0) - context.legendHeight * 5f / 28f);
            return true;
        }

        // We failed finding a location where legend doesn't hid selection points, let's show it above them.
        context.leftOnChart =
                normalizeLeft(Math.max(0, selectionVisualX - context.legendWidth * 5f / 28f), context.legendWidth);
        context.topOnChart = context.verticalPadding;
        return true;
    }

    private float normalizeLeft(float left, float width) {
        float xToShiftLeft = left + width - getChartRight();
        if (xToShiftLeft <= 0) {
            return left;
        }
        return Math.max(getChartLeft(), left - xToShiftLeft);
    }

    private void doDrawLegend(Canvas canvas, LegendDrawContext context) {
        mLegendRect = new RectF(context.leftOnChart,
                                context.topOnChart,
                                context.leftOnChart + context.legendWidth,
                                context.topOnChart + context.legendHeight);

        mBackgroundPaint.setColor(mChartConfig.getLegendBackgroundColor());
        mRoundedRectangleDrawer.draw(mLegendRect,
                                     mGridPaint,
                                     mBackgroundPaint,
                                     LeonardoUtil.DEFAULT_CORNER_RADIUS,
                                     canvas);
        drawLegendTitle(canvas, context);
        drawLegendValues(canvas, context);
    }

    private void drawLegendTitle(Canvas canvas, LegendDrawContext context) {
        mYLabelPaint.setTypeface(TYPEFACE_BOLD);
        mYLabelPaint.setColor(mChartConfig.getLegendTextTitleColor());
        TextWrapper xText = mXAxisConfig.getLabelTextStrategy().getLabel(mChartModel.getSelectedX(), mXAxisStep);
        canvas.drawText(xText.getData(),
                        0,
                        xText.getLength(),
                        context.leftOnChart + context.horizontalPadding,
                        context.topOnChart + context.titleYShift,
                        mYLabelPaint);
    }

    private void drawLegendValues(Canvas canvas, LegendDrawContext context) {
        float x = context.leftOnChart + context.horizontalPadding;
        float valueY = context.topOnChart + context.valueYShift;
        float legendY = context.topOnChart + context.legendYShift;
        mYLabelPaint.setTypeface(Typeface.DEFAULT);
        for (ChartDataSource dataSource : mDataSources) {
            if (!mChartModel.isActive(dataSource)) {
                continue;
            }
            ValueInfo valueInfo = context.dataSource2yInfo.get(dataSource);
            if (valueInfo == null) {
                continue;
            }
            final TextWrapper value;
            if (context.minifiedValues) {
                value = mYAxisConfig.getLabelTextStrategy().getMinifiedLabel(valueInfo.dataValue, mYAxisStep);
            } else {
                value = mYAxisConfig.getLabelTextStrategy().getLabel(valueInfo.dataValue, mYAxisStep);
            }
            mLegendValuePaint.setColor(dataSource.getColor());
            canvas.drawText(value.getData(), 0, value.getLength(), x, valueY, mLegendValuePaint);

            mYLabelPaint.setColor(dataSource.getColor());
            canvas.drawText(dataSource.getLegend(), x, legendY, mYLabelPaint);

            x += Math.max(mLegendValueWidthMeasurer.measureVisualSpace(value),
                          mYAxisLabelWidthMeasurer.measureVisualSpace(dataSource.getLegend()));
            x += context.horizontalPadding;
        }
    }

    private void mayBeTickAnimations() {
        boolean hasActiveAnimation = tickYRescaleAnimation() | tickDataSourceFadeAnimation();
        if (hasActiveAnimation) {
            mHandler.postDelayed(mRedrawTask, LeonardoUtil.ANIMATION_TICK_FREQUENCY_MILLIS);
        }
    }

    private boolean isYRescaleAnimationInProgress() {
        return mYAnimationStartTimeMs > 0 && mYAnimationFirstDataValue != Long.MIN_VALUE;
    }

    private void startYRescaleAnimation(Range rangeFrom, long stepFrom, Range rangeTo) {
        if (!mChartConfig.isAnimationEnabled()) {
            return;
        }

        mYAnimationStartTimeMs = System.currentTimeMillis();
        mYAnimationFirstDataValue = rangeFrom.findFirstStepValue(stepFrom);
        mYAnimationAxisStep = stepFrom;
        mYAnimationInitialUnitHeight = getChartHeight() / (rangeFrom.getPointsNumber() - 1f);
        mYAnimationCurrentUnitHeight = mYAnimationInitialUnitHeight;
        mYAnimationFinalUnitHeight = getChartHeight() / (rangeTo.getPointsNumber() - 1f);
        mYAnimationOngoingGridAlpha = 255;
        mYAnimationOngoingLabelAlpha = 255;
        mYAnimationFinalLabelAlpha = 0;
        mYAnimationFinalGridAlpha = 0;

        invalidate();
    }

    private boolean tickYRescaleAnimation() {
        if (!isYRescaleAnimationInProgress()) {
            return false;
        }

        long animationEndTime = mYAnimationStartTimeMs + ANIMATION_DURATION_MILLIS;
        long now = System.currentTimeMillis();
        if (now >= animationEndTime) {
            stopYRescaleAnimation();
            return false;
        }

        float unitHeightDelta = mYAnimationFinalUnitHeight - mYAnimationInitialUnitHeight;
        float tickUnitHeightChange = (now - mYAnimationStartTimeMs) * unitHeightDelta / ANIMATION_DURATION_MILLIS;
        mYAnimationCurrentUnitHeight = mYAnimationInitialUnitHeight + tickUnitHeightChange;

        updateAnimationLabelAlpha();
        updateAnimationGridAlpha();

        return true;
    }

    private void updateAnimationLabelAlpha() {
        long ongoingLabelFadeDuration = ANIMATION_DURATION_MILLIS * 2 / 3;
        long ongoingLabelFadeStartTime = mYAnimationStartTimeMs + ANIMATION_DURATION_MILLIS - ongoingLabelFadeDuration;
        long now = System.currentTimeMillis();
        if (now <= ongoingLabelFadeStartTime) {
            mYAnimationOngoingLabelAlpha = 255;
            mYAnimationFinalLabelAlpha = 0;
            return;
        }
        mYAnimationFinalLabelAlpha = (int) ((now - ongoingLabelFadeStartTime) * 255 / ongoingLabelFadeDuration);
        mYAnimationOngoingLabelAlpha = 255 - mYAnimationFinalLabelAlpha;
    }

    private void updateAnimationGridAlpha() {
        long switchTime = mYAnimationStartTimeMs + ANIMATION_DURATION_MILLIS * 2 / 3;
        if (System.currentTimeMillis() >= switchTime) {
            mYAnimationOngoingGridAlpha = 255;
            mYAnimationFinalGridAlpha = 0;
        } else {
            mYAnimationOngoingGridAlpha = 0;
            mYAnimationFinalGridAlpha = 255;
        }
    }

    private void stopYRescaleAnimation() {
        mYAnimationStartTimeMs = -1;
        mYAnimationFirstDataValue = -1;
        mYAnimationAxisStep = -1;
        mYAnimationInitialUnitHeight = -1;
        mYAnimationCurrentUnitHeight = -1;
        mYAnimationFinalUnitHeight = -1;
        invalidate();
    }

    private void startDataSourceFadeInAnimation(ChartDataSource dataSource) {
        if (!mChartConfig.isAnimationEnabled()) {
            return;
        }
        mAnimationDataSourceInfo.put(dataSource, new DataSourceAnimationContext(0, 255));
        invalidate();
    }

    private void startDataSourceFadeOutAnimation(ChartDataSource dataSource) {
        if (!mChartConfig.isAnimationEnabled()) {
            return;
        }
        mAnimationDataSourceInfo.put(dataSource, new DataSourceAnimationContext(255, 0));
        invalidate();
    }

    private boolean tickDataSourceFadeAnimation() {
        Set<ChartDataSource> toRemove = new HashSet<>();
        long now = System.currentTimeMillis();
        for (Map.Entry<ChartDataSource, DataSourceAnimationContext> entry : mAnimationDataSourceInfo.entrySet()) {
            DataSourceAnimationContext context = entry.getValue();
            if (now >= context.startTimeMs + ANIMATION_DURATION_MILLIS) {
                toRemove.add(entry.getKey());
                break;
            }

            long elapsedTimeMs = now - context.startTimeMs;
            int totalAlphaDelta = context.finalAlpha - context.initialAlpha;
            long currentAlphaDelta = elapsedTimeMs * totalAlphaDelta / ANIMATION_DURATION_MILLIS;
            context.currentAlpha = (int) (context.initialAlpha + currentAlphaDelta);
            if ((totalAlphaDelta > 0 && context.currentAlpha >= context.finalAlpha)
                || (totalAlphaDelta < 0 && context.currentAlpha <= context.finalAlpha))
            {
                toRemove.add(entry.getKey());
            }
        }
        for (ChartDataSource dataSource : toRemove) {
            stopDataSourceFadeAnimation(dataSource);
        }
        return !mAnimationDataSourceInfo.isEmpty();
    }

    private void stopDataSourceFadeAnimation(ChartDataSource dataSource) {
        mAnimationDataSourceInfo.remove(dataSource);
    }

    private static class ValueInfo {

        public final long  dataValue;
        public final float visualValue;

        public ValueInfo(long dataValue, float visualValue) {
            this.dataValue = dataValue;
            this.visualValue = visualValue;
        }
    }

    private static class LegendDrawContext {

        final Map<ChartDataSource, ValueInfo> dataSource2yInfo;

        LegendDrawContext(Map<ChartDataSource, ValueInfo> dataSource2yInfo) {
            this.dataSource2yInfo = dataSource2yInfo;
        }

        boolean tooNarrow;
        boolean minifiedValues;

        int horizontalPadding;
        int legendWidth;

        int verticalPadding;
        int titleYShift;
        int valueYShift;
        int legendYShift;
        int legendHeight;

        float leftOnChart;
        float topOnChart;
    }

    private static class DataSourceAnimationContext {

        public final long startTimeMs = System.currentTimeMillis();

        public final int initialAlpha;
        public final int finalAlpha;

        public int currentAlpha;

        public DataSourceAnimationContext(int initialAlpha, int finalAlpha) {
            this.initialAlpha = initialAlpha;
            this.finalAlpha = finalAlpha;
            currentAlpha = initialAlpha;
        }
    }
}
