package com.yangdai.colorpickerlib;

import android.graphics.Color;

import androidx.annotation.ColorInt;

import java.util.Locale;

/**
 * 一个用于改变颜色，尺寸形式的工具类。
 */
class Utils {
    /**
     * 将颜色转换为十六进制代码。
     *
     * @param color 颜色值。
     * @return 十六进制代码。
     */
    public static String getHexCode(@ColorInt int color) {
        int a = Color.alpha(color);
        int r = Color.red(color);
        int g = Color.green(color);
        int b = Color.blue(color);
        return String.format(Locale.getDefault(), "%02X%02X%02X%02X", a, r, g, b);
    }

    /**
     * 将颜色转换为ARGB整数数组。
     *
     * @param color 颜色值。
     * @return ARGB整数数组。
     * @noinspection AlibabaLowerCamelCaseVariableNaming
     */
    public static int[] getColorARGB(@ColorInt int color) {
        int[] argb = new int[4];
        argb[0] = Color.alpha(color);
        argb[1] = Color.red(color);
        argb[2] = Color.green(color);
        argb[3] = Color.blue(color);
        return argb;
    }
}