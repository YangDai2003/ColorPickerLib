package com.yangdai.colorpickerlib.sliders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;

import androidx.annotation.ColorInt;
import androidx.appcompat.content.res.AppCompatResources;

import com.yangdai.colorpickerlib.R;
import com.yangdai.colorpickerlib.preference.ColorPickerPreferenceManager;


/**
 * 透明度调节滑块
 *
 * @author 30415
 */
@SuppressWarnings("unused")
public class AlphaSlider extends BaseSlider {

    private Bitmap backgroundBitmap;
    private final TileBackgroundDrawable drawable = new TileBackgroundDrawable();

    public AlphaSlider(Context context) {
        super(context);
    }

    public AlphaSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AlphaSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AlphaSlider(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void getAttrs(AttributeSet attrs) {
        @SuppressLint("CustomViewStyleable")
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.AlphaSlideBar);
        try {
            if (a.hasValue(R.styleable.AlphaSlideBar_selector_AlphaSlideBar)) {
                int resourceId = a.getResourceId(R.styleable.AlphaSlideBar_selector_AlphaSlideBar, -1);
                if (resourceId != -1) {
                    selectorDrawable = AppCompatResources.getDrawable(getContext(), resourceId);
                }
            }
            if (a.hasValue(R.styleable.AlphaSlideBar_borderColor_AlphaSlideBar)) {
                borderColor = a.getColor(R.styleable.AlphaSlideBar_borderColor_AlphaSlideBar, borderColor);
            }
            if (a.hasValue(R.styleable.AlphaSlideBar_borderSize_AlphaSlideBar)) {
                borderSize = a.getInt(R.styleable.AlphaSlideBar_borderSize_AlphaSlideBar, borderSize);
            }
        } finally {
            a.recycle();
        }
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        if (width > 0 && height > 0) {
            backgroundBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            Canvas backgroundCanvas = new Canvas(backgroundBitmap);
            drawable.setBounds(0, 0, backgroundCanvas.getWidth(), backgroundCanvas.getHeight());
            drawable.draw(backgroundCanvas);

            Path path = new Path();
            float cornerRadius = 28f;
            path.addRoundRect(new RectF(0, 0, width, height), cornerRadius, cornerRadius, Path.Direction.CW);
            backgroundCanvas.clipPath(path);
        }
    }

    @Override
    public void updatePaint(Paint colorPaint) {
        float[] hsv = new float[3];
        Color.colorToHSV(getColor(), hsv);
        int startColor = Color.HSVToColor(0, hsv);
        int endColor = Color.HSVToColor(255, hsv);
        Shader shader =
                new LinearGradient(
                        0, 0, getWidth(), getMeasuredHeight(), startColor, endColor, Shader.TileMode.CLAMP);
        colorPaint.setShader(shader);
    }

    @Override
    public void onInflateFinished() {
        int defaultPosition = getWidth() - selector.getWidth();
        if (getPreferenceName() != null) {
            updateSelectorX(
                    ColorPickerPreferenceManager.getInstance(getContext())
                            .getAlphaSliderPosition(getPreferenceName(), defaultPosition)
                            + getSelectorSize() / 2);
        } else {
            selector.setX(defaultPosition);
        }
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        // 绘制圆角背景
        Path roundedRectPath = new Path();
        float cornerRadius = 28f;
        roundedRectPath.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), cornerRadius, cornerRadius, Path.Direction.CW);

        // 暂存canvas状态
        int saveCount = canvas.save();

        canvas.clipPath(roundedRectPath);
        canvas.drawBitmap(backgroundBitmap, 0, 0, null);

        // 恢复状态
        canvas.restoreToCount(saveCount);

        super.onDraw(canvas);
    }

    @Override
    public @ColorInt int assembleColor() {
        float[] hsv = new float[3];
        Color.colorToHSV(getColor(), hsv);
        int alpha = (int) (selectorPosition * 255);
        return Color.HSVToColor(alpha, hsv);
    }
}
