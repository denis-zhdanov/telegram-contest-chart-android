package tech.harmonysoft.android.leonardo.model.config.axis;

import android.content.Context;
import harmonysoft.tech.android.leonardo.R;
import tech.harmonysoft.android.leonardo.model.axis.AxisLabelTextStrategy;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public interface AxisConfigBuilder {

    @Nonnull
    AxisConfigBuilder disableLabels();

    @Nonnull
    AxisConfigBuilder disableAxis();

    @Nonnull
    AxisConfigBuilder withLabelTextStrategy(AxisLabelTextStrategy strategy);

    /**
     * <p>Specifies font size to use for axis labels.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@code android:textAppearanceSmall} from its theme is used.
     * </p>
     * <p>
     *     If font size is still undefined, {@link #build()} throws an exception.
     * </p>
     *
     * @param size      text size to use
     * @return          current builder
     */
    @Nonnull
    AxisConfigBuilder withFontSizeInPixels(int size);

    /**
     * <p>Specifies color to use for drawing axis labels.</p>
     * <p>
     *     If it's not specified explicitly and {@link #withContext(Context) a context is provided},
     *     then {@link R.attr#leonardo_chart_axis_label_color} from its theme is used.
     * </p>
     *
     * @param color     axis label color to use
     * @return          current builder
     */
    @Nonnull
    AxisConfigBuilder withLabelColor(int color);

    /**
     * Applies default values to various graphic elements obtained from the given context's
     * {@link Context#getTheme() theme}.
     *
     * @param context   theme holder which defaults should be applied unless explicitly specified
     * @return          current builder
     */
    @Nonnull
    AxisConfigBuilder withContext(Context context);

    @Nonnull
    AxisConfig build() throws IllegalStateException;
}
