package tech.harmonysoft.android.leonardo.model.config.navigator;

import android.content.Context;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 16/3/19
 */
public interface NavigatorConfigBuilder {

    @Nonnull
    NavigatorConfigBuilder withInactiveBackgroundColor(int color);

    @Nonnull
    NavigatorConfigBuilder withActiveBorderColor(int color);

    @Nonnull
    NavigatorConfigBuilder withActiveBorderHorizontalWidthInPixels(int widthInPixels);

    @Nonnull
    NavigatorConfigBuilder withActiveBorderVerticalHeightInPixels(int heightInPixels);

    @Nonnull
    NavigatorConfigBuilder withViewMargin(int marginInPixels);

    @Nonnull
    NavigatorConfigBuilder withSelectionOutline(int outlineInPixels);

    @Nonnull
    NavigatorConfigBuilder withSelectionColor(int selectionColor);

    NavigatorConfigBuilder withContext(Context context);

    @Nonnull
    NavigatorConfig build() throws IllegalStateException;
}
