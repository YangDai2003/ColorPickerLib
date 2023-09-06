package com.yangdai.colorpickerlib.preference;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Point;

import com.yangdai.colorpickerlib.ColorPickerView;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class ColorPickerPreferenceManager {

    protected static final String COLOR = "_COLOR";
    protected static final String SELECTOR_X = "_SELECTOR_X";
    protected static final String SELECTOR_Y = "_SELECTOR_Y";
    protected static final String SLIDER_ALPHA = "_SLIDER_ALPHA";
    protected static final String SLIDER_BRIGHTNESS = "_SLIDER_BRIGHTNESS";
    private static ColorPickerPreferenceManager colorPickerPreferenceManager;
    private final SharedPreferences sharedPreferences;

    private ColorPickerPreferenceManager(Context context) {
        sharedPreferences =
                context.getSharedPreferences(context.getPackageName(), Context.MODE_PRIVATE);
    }

    /**
     * 获取 {@link ColorPickerPreferenceManager} 的实例。
     *
     * @param context 上下文。
     * @return {@link ColorPickerPreferenceManager}。
     */
    public static ColorPickerPreferenceManager getInstance(Context context) {
        if (colorPickerPreferenceManager == null) {
            colorPickerPreferenceManager = new ColorPickerPreferenceManager(context);
        }
        return colorPickerPreferenceManager;
    }

    /**
     * 保存颜色到偏好设置。
     *
     * @param name  偏好设置名称。
     * @param color 偏好设置颜色。
     * @return {@link ColorPickerPreferenceManager}。
     */
    public ColorPickerPreferenceManager setColor(String name, int color) {
        sharedPreferences.edit().putInt(getColorName(name), color).apply();
        return colorPickerPreferenceManager;
    }

    /**
     * 从偏好设置中获取保存的颜色。
     *
     * @param name         偏好设置名称。
     * @param defaultColor 默认偏好设置颜色。
     * @return 保存的颜色。
     */
    public int getColor(String name, int defaultColor) {
        return sharedPreferences.getInt(getColorName(name), defaultColor);
    }

    /**
     * 从偏好设置中清除保存的颜色。
     *
     * @param name 偏好设置名称。
     * @return {@link ColorPickerPreferenceManager}。
     */
    public ColorPickerPreferenceManager clearSavedColor(String name) {
        sharedPreferences.edit().remove(getColorName(name)).apply();
        return colorPickerPreferenceManager;
    }

    /**
     * 保存选择器位置到偏好设置。
     *
     * @param name     偏好设置名称。
     * @param position 选择器的位置。
     * @return {@link ColorPickerPreferenceManager}。
     */
    public ColorPickerPreferenceManager setSelectorPosition(String name, Point position) {
        sharedPreferences.edit().putInt(getSelectorXName(name), position.x).apply();
        sharedPreferences.edit().putInt(getSelectorYName(name), position.y).apply();
        return colorPickerPreferenceManager;
    }

    /**
     * 从偏好设置中获取保存的选择器位置。
     *
     * @param name         偏好设置名称。
     * @param defaultPoint 选择器的默认位置。
     * @return 保存的选择器位置。
     */
    public Point getSelectorPosition(String name, Point defaultPoint) {
        return new Point(
                sharedPreferences.getInt(getSelectorXName(name), defaultPoint.x),
                sharedPreferences.getInt(getSelectorYName(name), defaultPoint.y));
    }

    /**
     * 从偏好设置中清除保存的选择器位置。
     *
     * @param name 偏好设置名称。
     * @return {@link ColorPickerPreferenceManager}。
     */
    public ColorPickerPreferenceManager clearSavedSelectorPosition(String name) {
        sharedPreferences.edit().remove(getSelectorXName(name)).apply();
        sharedPreferences.edit().remove(getSelectorYName(name)).apply();
        return colorPickerPreferenceManager;
    }

    public ColorPickerPreferenceManager setAlphaSliderPosition(String name, int position) {
        sharedPreferences.edit().putInt(getAlphaSliderName(name), position).apply();
        return colorPickerPreferenceManager;
    }

    /**
     * 获取透明度滑块的位置。
     *
     * @param name            偏好设置名称。
     * @param defaultPosition 透明度滑块的默认位置。
     * @return {@link ColorPickerPreferenceManager}。
     */
    public int getAlphaSliderPosition(String name, int defaultPosition) {
        return sharedPreferences.getInt(getAlphaSliderName(name), defaultPosition);
    }

    /**
     * 从偏好设置中清除保存的透明度滑块位置。
     *
     * @param name 偏好设置名称。
     * @return {@link ColorPickerPreferenceManager}。
     */
    public ColorPickerPreferenceManager clearSavedAlphaSliderPosition(String name) {
        sharedPreferences.edit().remove(getAlphaSliderName(name)).apply();
        return colorPickerPreferenceManager;
    }


    public ColorPickerPreferenceManager setBrightnessSliderPosition(String name, int position) {
        sharedPreferences.edit().putInt(getBrightnessSliderName(name), position).apply();
        return colorPickerPreferenceManager;
    }

    /**
     * 获取亮度滑块的位置。
     *
     * @param name            偏好设置名称。
     * @param defaultPosition 亮度滑块的默认位置。
     * @return {@link ColorPickerPreferenceManager}。
     */
    public int getBrightnessSliderPosition(String name, int defaultPosition) {
        return sharedPreferences.getInt(getBrightnessSliderName(name), defaultPosition);
    }


    /**
     * 清除偏好设置中保存的亮度滑块位置。
     *
     * @param name 偏好设置名称。
     * @return {@link ColorPickerPreferenceManager}。
     */
    public ColorPickerPreferenceManager clearSavedBrightnessSliderPosition(String name) {
        sharedPreferences.edit().remove(getBrightnessSliderName(name)).apply();
        return colorPickerPreferenceManager;
    }


    public void saveColorPickerData(ColorPickerView colorPickerView) {
        if (colorPickerView != null && colorPickerView.getPreferenceName() != null) {
            String name = colorPickerView.getPreferenceName();
            setColor(name, colorPickerView.getColor());
            setSelectorPosition(name, colorPickerView.getSelectedPoint());

            if (colorPickerView.getAlphaSlideBar() != null) {
                setAlphaSliderPosition(name, colorPickerView.getAlphaSlideBar().getSelectedX());
            }
            if (colorPickerView.getBrightnessSlider() != null) {
                setBrightnessSliderPosition(name, colorPickerView.getBrightnessSlider().getSelectedX());
            }
        }
    }

    /**
     * 从偏好设置中恢复所有数据。
     *
     * @param colorPickerView {@link ColorPickerView}。
     */
    public void restoreColorPickerData(ColorPickerView colorPickerView) {
        if (colorPickerView != null && colorPickerView.getPreferenceName() != null) {
            String name = colorPickerView.getPreferenceName();
            colorPickerView.setPureColor(getColor(name, -1));
            Point defaultPoint =
                    new Point(colorPickerView.getWidth() / 2, colorPickerView.getMeasuredHeight() / 2);
            colorPickerView.moveSelectorPoint(
                    getSelectorPosition(name, defaultPoint).x,
                    getSelectorPosition(name, defaultPoint).y,
                    getColor(name, -1));
        }
    }

    /**
     * 清除所有保存的偏好设置数据。
     *
     * @return {@link ColorPickerPreferenceManager}。
     */
    public ColorPickerPreferenceManager clearSavedAllData() {
        sharedPreferences.edit().clear().apply();
        return colorPickerPreferenceManager;
    }

    protected String getColorName(String name) {
        return name + COLOR;
    }

    protected String getSelectorXName(String name) {
        return name + SELECTOR_X;
    }

    protected String getSelectorYName(String name) {
        return name + SELECTOR_Y;
    }

    protected String getAlphaSliderName(String name) {
        return name + SLIDER_ALPHA;
    }

    protected String getBrightnessSliderName(String name) {
        return name + SLIDER_BRIGHTNESS;
    }
}