package com.yangdai.colorpickerlib.interfaces;

import com.yangdai.colorpickerlib.ColorInfo;

/**
 * 颜色选择监听器接口，用于监听颜色选择事件。
 *
 * @author 30415
 */
public interface ColorSelectionListener extends ColorListener {

    /**
     * 当颜色被选择时调用。
     *
     * @param colorInfo 选择的颜色包装对象
     * @param fromUser  是否来自用户操作
     */
    void onColorSelected(ColorInfo colorInfo, boolean fromUser);
}