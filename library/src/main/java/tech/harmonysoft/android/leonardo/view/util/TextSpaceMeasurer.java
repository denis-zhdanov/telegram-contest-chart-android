package tech.harmonysoft.android.leonardo.view.util;

import tech.harmonysoft.android.leonardo.model.text.TextWrapper;

/**
 * @author Denis Zhdanov
 * @since 13/3/19
 */
public interface TextSpaceMeasurer {

    /**
     * @param text      target text
     * @return          visual space occupied by the given text. It might be either horizontal or vertical space
     */
    int measureVisualSpace(String text);

    int measureVisualSpace(TextWrapper text);
}
