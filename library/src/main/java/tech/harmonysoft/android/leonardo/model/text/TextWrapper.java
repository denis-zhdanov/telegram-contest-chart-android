package tech.harmonysoft.android.leonardo.model.text;

import javax.annotation.Nonnull;
import java.util.Date;

/**
 * Is introduced just for performance optimization - profiling shows that creating many {@link Date} objects
 * during active range resize hits the performance. They are created when choosing X axis step.
 *
 * @author Denis Zhdanov
 * @since 21/3/19
 */
public class TextWrapper {

    private static final char[] DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    private char[] mData = new char[128];

    private int mLength;

    public int getLength() {
        return mLength;
    }

    @Nonnull
    public char[] getData() {
        return mData;
    }

    public void append(char c) {
        mData[mLength++] = c;
    }

    public void append(char[] data) {
        System.arraycopy(data, 0, mData, mLength, data.length);
        mLength += data.length;
    }

    public void append(String s) {
        append(s, 0, s.length());
    }

    public void append(String s, int start, int length) {
        for (int i = start, max = start + length; i < max; i++) {
            mData[mLength++] = s.charAt(i);
        }
    }

    public void append(final long l) {
        long value = l;
        if (l < 0) {
            append('-');
            value = -value;
        }
        long divider = 10;
        while (divider > 0 && divider <= value) {
            divider *= 10;
        }
        divider /= 10;

        while (divider > 0) {
            append(DIGITS[(int) (value / divider)]);
            value %= divider;
            divider /= 10;
        }
    }

    public void reset() {
        mLength = 0;
    }

    @Nonnull
    @Override
    public String toString() {
        return new String(mData, 0, mLength);
    }
}
