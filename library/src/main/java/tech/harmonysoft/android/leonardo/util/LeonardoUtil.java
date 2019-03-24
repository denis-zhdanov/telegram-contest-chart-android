package tech.harmonysoft.android.leonardo.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.TypedValue;
import harmonysoft.tech.android.leonardo.R;
import tech.harmonysoft.android.leonardo.controller.ChartDataManager;
import tech.harmonysoft.android.leonardo.model.config.chart.ChartConfig;
import tech.harmonysoft.android.leonardo.model.runtime.ChartModel;
import tech.harmonysoft.android.leonardo.model.runtime.impl.ChartModelImpl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @author Denis Zhdanov
 * @since 12/3/19
 */
public class LeonardoUtil {

    public static final float DEFAULT_CORNER_RADIUS                   = 20;
    public static final int   ACTION_START_AUTO_EXPAND_AREA_IN_PIXELS = 15;
    public static final long  ANIMATION_DURATION_MILLIS               = 300;
    public static final long  ANIMATION_TICK_FREQUENCY_MILLIS         = 20;

    private LeonardoUtil() {
    }

    public static int getColor(String resourceDescription,
                               int attributeId,
                               @Nullable Integer value,
                               @Nullable Context context)
    {
        if (value != null) {
            return value;
        }
        if (context != null) {
            return getColor(context, attributeId);
        }
        throw new IllegalStateException(resourceDescription + " is undefined");
    }

    public static int getColor(Context context, int attributeId) {
        // Look up a custom value in app theme first
        TypedValue appTypedValue = new TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(attributeId, appTypedValue, true);
        if (resolved
            && appTypedValue.type >= TypedValue.TYPE_FIRST_COLOR_INT
            && appTypedValue.type <= TypedValue.TYPE_LAST_COLOR_INT)
        {
            return appTypedValue.data;
        }

        // Fallback to library defaults
        TypedArray typedArray = context.obtainStyledAttributes(R.style.Leonardo_Light, new int[]{attributeId});
        try {
            return typedArray.getColor(0, Color.WHITE);
        } finally {
            typedArray.recycle();
        }
    }

    public static int getDimensionSizeInPixels(String resourceDescription,
                                               int attributeId,
                                               @Nullable Integer value,
                                               @Nullable Context context)
    {
        if (value != null) {
            return value;
        }
        if (context != null) {
            return getDimensionSizeInPixels(context, attributeId);
        }
        throw new IllegalStateException(resourceDescription + " is undefined");
    }

    public static int getDimensionSizeInPixels(Context context, int attributeId) {
        // Look up a custom value in app theme first
        TypedValue appTypedValue = new TypedValue();
        boolean resolved = context.getTheme().resolveAttribute(attributeId, appTypedValue, true);
        if (resolved) {
            return (int) appTypedValue.getDimension(context.getResources().getDisplayMetrics());
        }

        // Fallback to library defaults
        TypedArray typedArray = context.obtainStyledAttributes(R.style.Leonardo_Light, new int[]{attributeId});
        try {
            return typedArray.getDimensionPixelSize(0, -1);
        } finally {
            typedArray.recycle();
        }
    }

    @Nonnull
    public static ChartModel spinUpMvc(int bufferPagesNumber) {
        ChartModelImpl result = new ChartModelImpl(bufferPagesNumber);
        new ChartDataManager(result);
        return result;
    }
}
