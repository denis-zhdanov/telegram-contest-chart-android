package tech.harmonysoft.android.leonardo.model.config.axis.impl;

import tech.harmonysoft.android.leonardo.model.axis.AxisLabelTextStrategy;
import tech.harmonysoft.android.leonardo.model.text.TextWrapper;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 12/3/19
 */
public class DefaultAxisLabelTextStrategy implements AxisLabelTextStrategy {

    public static final DefaultAxisLabelTextStrategy INSTANCE = new DefaultAxisLabelTextStrategy();

    private final TextWrapper mText = new TextWrapper();

    @Nonnull
    @Override
    public TextWrapper getLabel(long value, long step) {
        mText.reset();
        int divisionsNumber = 0;
        long tmp = value;
        while (tmp >= 1000 && tmp % 1000 == 0) {
            tmp /= 1000;
            divisionsNumber++;
        }
        if (divisionsNumber == 0) {
            if (value > 1000 && value < 1000000 && value % 100 == 0) {
                mText.append(value / 1000);
                mText.append('.');
                mText.append((value / 100) % 10);
                mText.append('K');
                return mText;
            }
            mText.append(value);
            return mText;
        } else if (divisionsNumber == 1) {
            if (value > 1000000 && tmp % 100 == 0) {
                mText.append(tmp / 1000);
                mText.append('.');
                mText.append((tmp / 100) % 10);
                mText.append('M');
                return mText;
            }
            mText.append(tmp);
            mText.append("K");
            return mText;
        } else {
            mText.append(tmp);
            mText.append("M");
            return mText;
        }
    }

    @Nonnull
    @Override
    public TextWrapper getMinifiedLabel(long value, long step) {
        mText.reset();
        if (value < 1000) {
            mText.append(value);
            return mText;
        }

        if (value < 1000000) {
            if (value % 1000 != 0) {
                mText.append('~');
            }
            mText.append(Math.round(value / 1000d));
            mText.append('K');
            return mText;
        }

        if (value % 1000000 != 0) {
            mText.append('~');
        }
        mText.append(Math.round(value / 1000000d));
        mText.append('M');
        return mText;
    }
}
