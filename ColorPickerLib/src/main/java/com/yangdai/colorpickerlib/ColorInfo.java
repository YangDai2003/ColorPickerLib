package com.yangdai.colorpickerlib;

import androidx.annotation.ColorInt;

/**
 * ColorEnvelope 是颜色的包装类，用于提供颜色的不同形式。
 */
@SuppressWarnings("unused")
public class ColorInfo {

    @ColorInt
    private final int color;
    private final String hexCode;
    private final int[] argb;

    public ColorInfo(@ColorInt int color) {
        this.color = color;
        this.hexCode = Utils.getHexCode(color);
        this.argb = Utils.getColorARGB(color);
    }

    /**
     * 获取颜色值。
     *
     * @return 颜色值。
     */
    public @ColorInt int getColor() {
        return color;
    }

    /**
     * 获取十六进制代码。
     *
     * @return 十六进制代码。
     */
    public String getHexCode() {
        return hexCode;
    }

    /**
     * 获取ARGB颜色。
     *
     * @return ARGB整数数组。
     */
    public int[] getArgb() {
        return argb;
    }
}