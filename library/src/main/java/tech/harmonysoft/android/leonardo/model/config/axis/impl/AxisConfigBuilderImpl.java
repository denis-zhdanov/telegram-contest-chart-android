package tech.harmonysoft.android.leonardo.model.config.axis.impl;

import android.content.Context;
import android.graphics.Color;
import harmonysoft.tech.android.leonardo.R;
import tech.harmonysoft.android.leonardo.model.axis.AxisLabelTextStrategy;
import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfig;
import tech.harmonysoft.android.leonardo.model.config.axis.AxisConfigBuilder;
import tech.harmonysoft.android.leonardo.util.LeonardoUtil;

import javax.annotation.Nonnull;

import static tech.harmonysoft.android.leonardo.util.LeonardoUtil.getDimensionSizeInPixels;

/**
 * @author Denis Zhdanov
 * @since 10/3/19
 */
public class AxisConfigBuilderImpl implements AxisConfigBuilder {

    private AxisLabelTextStrategy mAxisLabelTextStrategy = DefaultAxisLabelTextStrategy.INSTANCE;

    private Integer mFontSizeInPixels;
    private Integer mLabelColor;
    private Context mContext;
    private boolean mLabelsDisabled;
    private boolean mAxisDisabled;

    @Nonnull
    @Override
    public AxisConfigBuilder disableLabels() {
        mLabelsDisabled = true;
        return this;
    }

    @Nonnull
    @Override
    public AxisConfigBuilder disableAxis() {
        mAxisDisabled = true;
        return this;
    }

    @Nonnull
    @Override
    public AxisConfigBuilder withLabelTextStrategy(AxisLabelTextStrategy strategy) {
        mAxisLabelTextStrategy = strategy;
        return this;
    }

    @Nonnull
    @Override
    public AxisConfigBuilder withFontSizeInPixels(int size) {
        if (mFontSizeInPixels <= 0) {
            throw new IllegalArgumentException("Axis label font size is expected to be positive but got " + size);
        }
        mFontSizeInPixels = size;
        return this;
    }

    @Nonnull
    @Override
    public AxisConfigBuilder withLabelColor(int color) {
        mLabelColor = color;
        return this;
    }

    @Nonnull
    @Override
    public AxisConfigBuilder withContext(Context context) {
        mContext = context;
        return this;
    }

    @Nonnull
    @Override
    public AxisConfig build() {
        boolean labelsDisabled = mLabelsDisabled;

        Integer fontSize = mFontSizeInPixels;
        if (fontSize == null) {
            Context context = mContext;
            if (context != null) {
                fontSize = getDimensionSizeInPixels(context, R.attr.leonardo_chart_axis_label_text_size);
            }
            if (labelsDisabled) {
                fontSize = 12; // Dummy value
            }
        }
        if (fontSize == null) {
            throw new IllegalStateException("Font size is undefined");
        }

        Integer labelColor = mLabelColor;
        if (labelColor == null) {
            Context context = mContext;
            if (context != null) {
                labelColor = LeonardoUtil.getColor(context, R.attr.leonardo_chart_axis_label_color);
            }
            if (labelsDisabled) {
                labelColor = Color.GRAY; // Dummy value
            }
        }
        if (labelColor == null) {
            throw new IllegalStateException("Axis label color");
        }

        return new AxisConfigImpl(mAxisLabelTextStrategy, fontSize, labelColor, !labelsDisabled, !mAxisDisabled);
    }
}
