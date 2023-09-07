package com.yangdai.colorpickerlib.sliders;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.DimenRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.FloatRange;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;

import com.yangdai.colorpickerlib.UpdateMode;
import com.yangdai.colorpickerlib.ColorPickerView;

/**
 * BaseSlider是所有滑动条的父类抽象.
 */
@SuppressWarnings("unused")
abstract class BaseSlider extends FrameLayout {

    public ColorPickerView colorPickerView;
    protected Paint colorPaint;
    protected Paint borderPaint;
    protected float selectorPosition = 1.0f;
    protected int selectedX = 0;
    protected Drawable selectorDrawable;
    protected int borderSize = 2;
    protected int borderColor = Color.BLACK;
    protected int color = Color.WHITE;
    protected ImageView selector;
    protected String preferenceName;
    private float cornerRadius = 28f;

    public BaseSlider(Context context) {
        super(context);
        onCreate();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        selector.setVisibility(enabled ? VISIBLE : INVISIBLE);
        this.setClickable(enabled);
    }

    public BaseSlider(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
        onCreate();
    }

    public BaseSlider(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(attrs);
        onCreate();
    }

    public BaseSlider(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttrs(attrs);
        onCreate();
    }

    /**
     * 从布局获取样式集合
     */
    protected abstract void getAttrs(AttributeSet attrs);

    /**
     * 更新颜色时更新画笔颜色。
     *
     * @param colorPaint 画笔颜色。
     */
    protected abstract void updatePaint(Paint colorPaint);

    /**
     * 组合所选颜色。
     *
     * @return 组装的颜色。
     */
    public abstract @ColorInt int assembleColor();

    private void onCreate() {
        this.colorPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        this.colorPaint.setStrokeCap(Paint.Cap.ROUND);
        this.borderPaint.setStrokeCap(Paint.Cap.ROUND);
        this.borderPaint.setStyle(Paint.Style.STROKE);
        this.borderPaint.setStrokeWidth(borderSize);
        this.borderPaint.setColor(borderColor);
        this.setBackgroundColor(Color.TRANSPARENT);

        selector = new ImageView(getContext());
        if (selectorDrawable != null) {
            setSelectorDrawable(selectorDrawable);
        }

        initializeSelector();
    }

    public void setCornerRadius(float cornerRadius) {
        this.cornerRadius = cornerRadius;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float width = getWidth();
        float height = getMeasuredHeight();
        float halfBorderSize = borderSize / 2f;
        float right = width - halfBorderSize;
        float bottom = height - halfBorderSize;
        float rx = cornerRadius;
        float ry = cornerRadius;
        canvas.drawRoundRect(halfBorderSize, halfBorderSize, right, bottom, rx, ry, colorPaint);
        canvas.drawRoundRect(halfBorderSize, halfBorderSize, right, bottom, rx, ry, borderPaint);
    }

    /**
     * called by {@link ColorPickerView} whenever {@link ColorPickerView} is triggered.
     */
    public void notifyColor() {
        color = colorPickerView.getPureColor();
        updatePaint(colorPaint);
        invalidate();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isEnabled()) {
            return false;
        }

        if (colorPickerView != null) {
            switch (event.getActionMasked()) {
                case MotionEvent.ACTION_UP:
                case MotionEvent.ACTION_DOWN:
                case MotionEvent.ACTION_MOVE:
                    selector.setPressed(true);
                    if (event.getX() > getWidth() || event.getX() < 0) {
                        return false;
                    } else {
                        onTouchReceived(event);
                        return true;
                    }
                default:
                    selector.setPressed(false);
                    return false;
            }
        } else {
            return false;
        }
    }

    private void onTouchReceived(MotionEvent event) {
        float eventX = event.getX();
        float left = selector.getWidth() / 2f;
        float right = getWidth() - left;
        if (eventX > right) {
            eventX = right;
        }
        selectorPosition = (eventX - left) / (right - left);
        if (selectorPosition < 0) {
            selectorPosition = 0;
        }
        if (selectorPosition > 1.0f) {
            selectorPosition = 1.0f;
        }
        Point snapPoint = new Point((int) event.getX(), (int) event.getY());
        selectedX = (int) getBoundaryX(snapPoint.x);
        selector.setX(selectedX);
        if (colorPickerView.getUpdateMode() == UpdateMode.After) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                colorPickerView.fireColorListener(assembleColor(), true);
            }
        } else {
            colorPickerView.fireColorListener(assembleColor(), true);
        }

        int maxPos = getWidth() - selector.getWidth();
        if (selector.getX() >= maxPos) {
            selector.setX(maxPos);
        }
        if (selector.getX() <= 0) {
            selector.setX(0);
        }
    }

    public void updateSelectorX(int x) {
        float left = selector.getWidth() / 2f;
        float right = getWidth() - left;
        selectorPosition = (x - left) / (right - left);
        if (selectorPosition < 0) {
            selectorPosition = 0;
        }
        if (selectorPosition > 1.0f) {
            selectorPosition = 1.0f;
        }
        selectedX = (int) getBoundaryX(x);
        selector.setX(selectedX);
        colorPickerView.fireColorListener(assembleColor(), false);
    }

    public void setSelectorPosition(@FloatRange(from = 0.0, to = 1.0) float selectorPosition) {
        this.selectorPosition = Math.min(selectorPosition, 1.0f);
        float x = (getWidth() * selectorPosition) - getSelectorSize() - getBorderHalfSize();
        selectedX = (int) getBoundaryX(x);
        selector.setX(selectedX);
    }

    public void setSelectorByHalfSelectorPosition(
            @FloatRange(from = 0.0, to = 1.0) float selectorPosition) {
        this.selectorPosition = Math.min(selectorPosition, 1.0f);
        float x = (getWidth() * selectorPosition) - (getSelectorSize() * 0.5f) - getBorderHalfSize();
        selectedX = (int) getBoundaryX(x);
        selector.setX(selectedX);
    }

    private float getBoundaryX(float x) {
        int maxPos = getWidth() - selector.getWidth() / 2;
        if (x >= maxPos) {
            return maxPos;
        }
        if (x <= getSelectorSize() / 2f) {
            return 0;
        }
        return x - getSelectorSize() / 2f;
    }

    protected int getSelectorSize() {
        return (int) (selector.getWidth());
    }

    protected int getBorderHalfSize() {
        return (int) (borderSize * 0.5f);
    }

    private void initializeSelector() {
        getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                onInflateFinished();
                                setSelectorPosition(selectorPosition); // Add this line
                            }
                        });
    }

    /**
     * 设置选择器的可绘制对象。
     *
     * @param drawable 选择器的可绘制对象。
     */
    public void setSelectorDrawable(Drawable drawable) {
        removeView(selector);
        this.selectorDrawable = drawable;
        this.selector.setImageDrawable(drawable);
        LayoutParams thumbParams =
                new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        thumbParams.gravity = Gravity.CENTER;
        addView(selector, thumbParams);
    }

    /**
     * 设置选择器的可绘制资源。
     *
     * @param resource 选择器的可绘制资源。
     */
    public void setSelectorDrawableRes(@DrawableRes int resource) {
        Drawable drawable = ResourcesCompat.getDrawable(getContext().getResources(), resource, null);
        setSelectorDrawable(drawable);
    }

    /**
     * 设置滑块边框的颜色。
     *
     * @param color 滑块边框的颜色。
     */
    public void setBorderColor(@ColorInt int color) {
        this.borderColor = color;
        this.borderPaint.setColor(color);
        invalidate();
    }

    /**
     * 设置滑块边框的颜色资源。
     *
     * @param resource 滑块边框的颜色资源。
     */
    public void setBorderColorRes(@ColorRes int resource) {
        int color = ContextCompat.getColor(getContext(), resource);
        setBorderColor(color);
    }

    /**
     * 设置滑块边框的大小。
     *
     * @param borderSize 滑块边框的大小。
     */
    public void setBorderSize(int borderSize) {
        this.borderSize = borderSize;
        this.borderPaint.setStrokeWidth(borderSize);
        invalidate();
    }

    /**
     * 使用尺寸资源设置滑块边框的大小。
     *
     * @param resource 滑块边框的大小尺寸资源。
     */
    public void setBorderSizeRes(@DimenRes int resource) {
        int borderSize = (int) getContext().getResources().getDimension(resource);
        setBorderSize(borderSize);
    }

    /**
     * 当布局生成完成时调用。
     */
    public abstract void onInflateFinished();

    /**
     * 获取组合的颜色。
     *
     * @return 颜色。
     */
    public int getColor() {
        return color;
    }

    /**
     * 将 {@link ColorPickerView} 附加到滑块上。
     *
     * @param colorPickerView {@link ColorPickerView}。
     */
    public void attachColorPickerView(ColorPickerView colorPickerView) {
        this.colorPickerView = colorPickerView;
    }

    /**
     * 获取选择器的位置比例。
     *
     * @return 选择器的位置比例。
     */
    protected float getSelectorPosition() {
        return this.selectorPosition;
    }

    /**
     * 获取选择的 x 坐标。
     *
     * @return 选择的 x 坐标。
     */
    public int getSelectedX() {
        return this.selectedX;
    }

    /**
     * 获取偏好设置名称。
     *
     * @return 偏好设置名称。
     */
    public String getPreferenceName() {
        return preferenceName;
    }

    /**
     * 设置偏好设置名称。
     *
     * @param preferenceName 偏好设置名称。
     */
    public void setPreferenceName(String preferenceName) {
        this.preferenceName = preferenceName;
    }
}
