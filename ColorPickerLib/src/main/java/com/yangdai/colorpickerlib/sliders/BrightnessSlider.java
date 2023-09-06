package com.yangdai.colorpickerlib.sliders;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.appcompat.content.res.AppCompatResources;

import com.yangdai.colorpickerlib.R;
import com.yangdai.colorpickerlib.preference.ColorPickerPreferenceManager;

/**
 * 明亮度调节滑块
 *
 * @author 30415
 */
@SuppressWarnings("unused")
public class BrightnessSlider extends BaseSlider {

    public BrightnessSlider(Context context) {
        super(context);
    }

    public BrightnessSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BrightnessSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public BrightnessSlider(
            Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void getAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.BrightnessSlideBar);
        try {
            if (a.hasValue(R.styleable.BrightnessSlideBar_selector_BrightnessSlider)) {
                int resourceId =
                        a.getResourceId(R.styleable.BrightnessSlideBar_selector_BrightnessSlider, -1);
                if (resourceId != -1) {
                    selectorDrawable = AppCompatResources.getDrawable(getContext(), resourceId);
                }
            }
            if (a.hasValue(R.styleable.BrightnessSlideBar_borderColor_BrightnessSlider)) {
                borderColor =
                        a.getColor(R.styleable.BrightnessSlideBar_borderColor_BrightnessSlider, borderColor);
            }
            if (a.hasValue(R.styleable.BrightnessSlideBar_borderSize_BrightnessSlider)) {
                borderSize =
                        a.getInt(R.styleable.BrightnessSlideBar_borderSize_BrightnessSlider, borderSize);
            }
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void updatePaint(Paint colorPaint) {
        float[] hsv = new float[3];
        Color.colorToHSV(getColor(), hsv);
        hsv[2] = 0;
        int startColor = Color.HSVToColor(hsv);
        hsv[2] = 1;
        int endColor = Color.HSVToColor(hsv);
        Shader shader =
                new LinearGradient(
                        0, 0, getWidth(), getHeight(), startColor, endColor, Shader.TileMode.CLAMP);
        colorPaint.setShader(shader);
    }

    @Override
    public void onInflateFinished() {
        selector.post(
                () -> {
                    int defaultPosition = getWidth() - selector.getWidth();

                    if (getPreferenceName() != null) {
                        updateSelectorX(
                                ColorPickerPreferenceManager.getInstance(getContext())
                                        .getBrightnessSliderPosition(getPreferenceName(), defaultPosition)
                                        + getSelectorSize() / 2);
                    } else {
                        selector.setX(defaultPosition);
                    }
                });
    }

    @Override
    public @ColorInt int assembleColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(getColor(), hsv);
        hsv[2] = selectorPosition;
        if (colorPickerView != null && colorPickerView.getAlphaSlideBar() != null) {
            int alpha = (int) (colorPickerView.getAlphaSlideBar().getSelectorPosition() * 255);
            return Color.HSVToColor(alpha, hsv);
        }
        return Color.HSVToColor(hsv);
    }
}
