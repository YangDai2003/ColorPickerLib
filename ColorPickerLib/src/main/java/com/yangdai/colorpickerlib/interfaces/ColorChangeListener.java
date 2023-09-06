package com.yangdai.colorpickerlib.interfaces;

import androidx.annotation.ColorInt;

/**
 * @author 30415
 */
public interface ColorChangeListener extends ColorListener {

    void onColorChanged(@ColorInt int color, boolean fromUser);
}
