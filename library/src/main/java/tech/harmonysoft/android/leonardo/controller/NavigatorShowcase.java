package tech.harmonysoft.android.leonardo.controller;

import tech.harmonysoft.android.leonardo.model.runtime.ChartModel;

import javax.annotation.Nonnull;

/**
 * @author Denis Zhdanov
 * @since 17/3/19
 */
public interface NavigatorShowcase {

    /**
     * @return      anchor to use for {@link ChartModel getting showcase' data}
     */
    @Nonnull
    Object getDataAnchor();

    float getVisualXShift();

    void scrollHorizontally(float deltaVisualX);
}
