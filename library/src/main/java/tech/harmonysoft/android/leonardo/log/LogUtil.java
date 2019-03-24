package tech.harmonysoft.android.leonardo.log;

import android.annotation.SuppressLint;
import android.util.Log;
import harmonysoft.tech.android.leonardo.BuildConfig;

import javax.annotation.Nonnull;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Denis Zhdanov
 * @since 17/3/19
 */
public class LogUtil {

    private static final String LOG_TAG = "Leonardo";

    private static Map<Object, String> LOG_MARKERS = new HashMap<>();

    @SuppressLint("SimpleDateFormat")
    private static final SimpleDateFormat FORMATTER = new SimpleDateFormat("HH:mm:ss.SSS");

    private LogUtil() {
    }

    public static void putMarker(Object anchor, String marker) {
        if (BuildConfig.DEBUG) { // To avoid memory leak on anchor
            LOG_MARKERS.put(anchor, marker);
        }
    }

    public static void debug(Object location, String messagePattern, Object... arguments) {
        if (BuildConfig.DEBUG) {
            Log.d(LOG_TAG,
                  String.format("%s %s - %s",
                                FORMATTER.format(new Date()),
                                getMarker(location),
                                String.format(messagePattern, arguments)));
        }
    }

    @Nonnull
    public static String getMarker(Object location) {
        String result = LOG_MARKERS.get(location);
        if (result != null) {
            return result;
        }

        if (location instanceof Class<?>) {
            return ((Class) location).getSimpleName();
        }

        result = location.toString();
        if (result.contains("@")) {
            return location.getClass().getSimpleName();
        } else {
            return result;
        }
    }
}
