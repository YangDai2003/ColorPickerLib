package com.yangdai.colorpickerlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.ColorInt;
import androidx.annotation.ColorRes;
import androidx.annotation.FloatRange;
import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.Px;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.OnLifecycleEvent;

import com.yangdai.colorpickerlib.interfaces.ColorSelectionListener;
import com.yangdai.colorpickerlib.interfaces.ColorChangeListener;
import com.yangdai.colorpickerlib.interfaces.ColorListener;
import com.yangdai.colorpickerlib.interfaces.Dp;
import com.yangdai.colorpickerlib.preference.ColorPickerPreferenceManager;
import com.yangdai.colorpickerlib.sliders.AlphaSlider;
import com.yangdai.colorpickerlib.sliders.BrightnessSlider;


@SuppressWarnings("unused")
public class ColorPickerView extends FrameLayout implements LifecycleObserver {

    @ColorInt
    private int selectedPureColor;
    @ColorInt
    private int selectedColor;
    private Point selectedPoint;
    private ImageView palette;
    private ImageView selector;
    private Drawable paletteDrawable;
    private Drawable selectorDrawable;
    private AlphaSlider alphaSlider;
    private BrightnessSlider brightnessSlider;
    public ColorListener colorListener;
    private long debounceDuration = 0;
    private final Handler debounceHandler = new Handler(Looper.getMainLooper());

    private UpdateMode updateMode = UpdateMode.ALWAYS;

    @FloatRange(from = 0.0, to = 1.0)
    private float selector_alpha = 1.0f;

    @FloatRange(from = 0.0, to = 1.0)
    private float flag_alpha = 1.0f;

    private boolean flag_isFlipAble = true;

    @Px
    private int selectorSize = 0;

    private boolean VISIBLE_FLAG = false;

    private String preferenceName;
    private final ColorPickerPreferenceManager preferenceManager =
            ColorPickerPreferenceManager.getInstance(getContext());

    public ColorPickerView(Context context) {
        super(context);
    }

    public ColorPickerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        getAttrs(attrs);
        onCreate();
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        getAttrs(attrs);
        onCreate();
    }

    public ColorPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        getAttrs(attrs);
        onCreate();
    }

    private void getAttrs(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ColorPickerView);
        try {
            if (a.hasValue(R.styleable.ColorPickerView_palette)) {
                this.paletteDrawable = a.getDrawable(R.styleable.ColorPickerView_palette);
            }
            if (a.hasValue(R.styleable.ColorPickerView_selector)) {
                int resourceId = a.getResourceId(R.styleable.ColorPickerView_selector, -1);
                if (resourceId != -1) {
                    this.selectorDrawable = AppCompatResources.getDrawable(getContext(), resourceId);
                }
            }
            if (a.hasValue(R.styleable.ColorPickerView_selector_alpha)) {
                this.selector_alpha =
                        a.getFloat(R.styleable.ColorPickerView_selector_alpha, selector_alpha);
            }
            if (a.hasValue(R.styleable.ColorPickerView_selector_size)) {
                this.selectorSize =
                        a.getDimensionPixelSize(R.styleable.ColorPickerView_selector_size, selectorSize);
            }
            if (a.hasValue(R.styleable.ColorPickerView_flag_alpha)) {
                this.flag_alpha = a.getFloat(R.styleable.ColorPickerView_flag_alpha, flag_alpha);
            }
            if (a.hasValue(R.styleable.ColorPickerView_flag_isFlipAble)) {
                this.flag_isFlipAble =
                        a.getBoolean(R.styleable.ColorPickerView_flag_isFlipAble, flag_isFlipAble);
            }
            if (a.hasValue(R.styleable.ColorPickerView_updateMode)) {
                int actionMode = a.getInteger(R.styleable.ColorPickerView_updateMode, 0);
                if (actionMode == 0) {
                    this.updateMode = UpdateMode.ALWAYS;
                } else if (actionMode == 1) {
                    this.updateMode = UpdateMode.After;
                }
            }
            if (a.hasValue(R.styleable.ColorPickerView_debounceDuration)) {
                this.debounceDuration =
                        a.getInteger(R.styleable.ColorPickerView_debounceDuration, (int) debounceDuration);
            }
            if (a.hasValue(R.styleable.ColorPickerView_preferenceName)) {
                this.preferenceName = a.getString(R.styleable.ColorPickerView_preferenceName);
            }
            if (a.hasValue(R.styleable.ColorPickerView_initialColor)) {
                setInitialColor(a.getColor(R.styleable.ColorPickerView_initialColor, Color.WHITE));
            }
        } finally {
            a.recycle();
        }
    }

    private void onCreate() {
        setPadding(0, 0, 0, 0);
        palette = new ImageView(getContext());
        if (paletteDrawable != null) {
            palette.setImageDrawable(paletteDrawable);
        }

        LayoutParams paletteParam =
                new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        paletteParam.gravity = Gravity.CENTER;
        addView(palette, paletteParam);

        selector = new ImageView(getContext());
        if (selectorDrawable != null) {
            selector.setImageDrawable(selectorDrawable);
        } else {
            selector.setImageDrawable(ContextCompat.getDrawable(getContext(), R.drawable.dot));
        }
        LayoutParams selectorParam =
                new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (selectorSize != 0) {
            selectorParam.width = Utils.dp2Px(getContext(), selectorSize);
            selectorParam.height = Utils.dp2Px(getContext(), selectorSize);
        }
        selectorParam.gravity = Gravity.CENTER;
        addView(selector, selectorParam);
        selector.setAlpha(selector_alpha);

        getViewTreeObserver()
                .addOnGlobalLayoutListener(
                        new ViewTreeObserver.OnGlobalLayoutListener() {
                            @Override
                            public void onGlobalLayout() {
                                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                onFinishInflated();
                            }
                        });
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);

        if (palette.getDrawable() == null) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
            palette.setImageDrawable(new ColorPalette(getResources(), bitmap));
        }
    }

    private void onFinishInflated() {
        if (getParent() != null && getParent() instanceof ViewGroup) {
            ((ViewGroup) getParent()).setClipChildren(false);
        }

        if (getPreferenceName() != null) {
            preferenceManager.restoreColorPickerData(this);
            final int persisted = preferenceManager.getColor(getPreferenceName(), -1);
            if (palette.getDrawable() instanceof ColorPalette && persisted != -1) {
                post(
                        () -> {
                            try {
                                selectByHsvColor(persisted);
                            } catch (IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });
            }
        } else {
            selectCenter();
        }
    }

    /**
     * initialize the {@link ColorPickerView} by {@link Builder}.
     *
     * @param builder {@link Builder}.
     */
    protected void onCreateByBuilder(Builder builder) {
        LayoutParams params =
                new LayoutParams(
                        Utils.dp2Px(getContext(), builder.width),
                        Utils.dp2Px(getContext(), builder.height));
        setLayoutParams(params);

        this.paletteDrawable = builder.paletteDrawable;
        this.selectorDrawable = builder.selectorDrawable;
        this.selector_alpha = builder.selector_alpha;
        this.selectorSize = builder.selectorSize;
        this.debounceDuration = builder.debounceDuration;
        onCreate();

        if (builder.colorListener != null) {
            setColorListener(builder.colorListener);
        }
        if (builder.alphaSlider != null) {
            attachAlphaSlider(builder.alphaSlider);
        }
        if (builder.brightnessSlider != null) {
            attachBrightnessSlider(builder.brightnessSlider);
        }
        if (builder.updateMode != null) {
            this.updateMode = builder.updateMode;
        }
        if (builder.preferenceName != null) {
            setPreferenceName(builder.preferenceName);
        }
        if (builder.initialColor != 0) {
            setInitialColor(builder.initialColor);
        }
        if (builder.lifecycleOwner != null) {
            setLifecycleOwner(builder.lifecycleOwner);
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!this.isEnabled()) {
            return false;
        }
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
            case MotionEvent.ACTION_UP:
                selector.setPressed(true);
                return onTouchReceived(event);
            default:
                selector.setPressed(false);
                return false;
        }
    }

    /**
     * notify to the other views by the onTouchEvent.
     *
     * @param event {@link MotionEvent}.
     * @return notified or not.
     */
    @MainThread
    private boolean onTouchReceived(final MotionEvent event) {
        Point snapPoint =
                PointHelper.getColorPoint(this, new Point((int) event.getX(), (int) event.getY()));
        int pixelColor = getColorFromBitmap(snapPoint.x, snapPoint.y);

        this.selectedPureColor = pixelColor;
        this.selectedColor = pixelColor;
        this.selectedPoint = PointHelper.getColorPoint(this, new Point(snapPoint.x, snapPoint.y));
        setCoordinate(snapPoint.x, snapPoint.y);

        if (updateMode == UpdateMode.After) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                notifyColorChanged();
            }
        } else {
            notifyColorChanged();
        }
        return true;
    }

    public boolean isHuePalette() {
        return palette.getDrawable() != null && palette.getDrawable() instanceof ColorPalette;
    }

    private void notifyColorChanged() {
        this.debounceHandler.removeCallbacksAndMessages(null);
        Runnable debounceRunnable =
                () -> fireColorListener(getColor(), true);
        this.debounceHandler.postDelayed(debounceRunnable, this.debounceDuration);
    }

    /**
     * gets a pixel color on the specific coordinate from the bitmap.
     *
     * @param x coordinate x.
     * @param y coordinate y.
     * @return selected color.
     */
    protected int getColorFromBitmap(float x, float y) {
        Matrix invertMatrix = new Matrix();
        palette.getImageMatrix().invert(invertMatrix);

        float[] mappedPoints = new float[]{x, y};
        invertMatrix.mapPoints(mappedPoints);

        if (palette.getDrawable() != null
                && palette.getDrawable() instanceof BitmapDrawable
                && mappedPoints[0] >= 0
                && mappedPoints[1] >= 0
                && mappedPoints[0] < palette.getDrawable().getIntrinsicWidth()
                && mappedPoints[1] < palette.getDrawable().getIntrinsicHeight()) {

            invalidate();

            if (palette.getDrawable() instanceof ColorPalette) {
                x = x - getWidth() * 0.5f;
                y = y - getHeight() * 0.5f;
                double r = Math.sqrt(x * x + y * y);
                float radius = Math.min(getWidth(), getHeight()) * 0.5f;
                float[] hsv = {0, 0, 1};
                hsv[0] = (float) (Math.atan2(y, -x) / Math.PI * 180f) + 180;
                hsv[1] = Math.max(0f, Math.min(1f, (float) (r / radius)));
                return Color.HSVToColor(hsv);
            } else {
                Rect rect = palette.getDrawable().getBounds();
                float scaleX = mappedPoints[0] / rect.width();
                int x1 = (int) (scaleX * ((BitmapDrawable) palette.getDrawable()).getBitmap().getWidth());
                float scaleY = mappedPoints[1] / rect.height();
                int y1 = (int) (scaleY * ((BitmapDrawable) palette.getDrawable()).getBitmap().getHeight());
                return ((BitmapDrawable) palette.getDrawable()).getBitmap().getPixel(x1, y1);
            }
        }
        return 0;
    }

    public void setColorListener(ColorListener colorListener) {
        this.colorListener = colorListener;
    }

    public void fireColorListener(@ColorInt int color, final boolean fromUser) {
        if (this.colorListener != null) {
            this.selectedColor = color;
            if (getAlphaSlideBar() != null) {
                getAlphaSlideBar().notifyColor();
                this.selectedColor = getAlphaSlideBar().assembleColor();
            }
            if (getBrightnessSlider() != null) {
                getBrightnessSlider().notifyColor();
                this.selectedColor = getBrightnessSlider().assembleColor();
            }

            if (colorListener instanceof ColorChangeListener) {
                ((ColorChangeListener) colorListener).onColorChanged(selectedColor, fromUser);
            } else if (colorListener instanceof ColorSelectionListener) {
                ColorInfo envelope = new ColorInfo(selectedColor);
                ((ColorSelectionListener) colorListener).onColorSelected(envelope, fromUser);
            }

            if (VISIBLE_FLAG) {
                VISIBLE_FLAG = false;
                if (this.selector != null) {
                    this.selector.setAlpha(selector_alpha);
                }
            }
        }
    }

    /**
     * notify to sliders about a new trigger.
     */
    private void notifyToSlideBars() {
        if (alphaSlider != null) {
            alphaSlider.notifyColor();
        }
        if (brightnessSlider != null) {
            brightnessSlider.notifyColor();

            if (brightnessSlider.assembleColor() != Color.WHITE) {
                selectedColor = brightnessSlider.assembleColor();
            } else if (alphaSlider != null) {
                selectedColor = alphaSlider.assembleColor();
            }
        }
    }

    /**
     * gets the selected color.
     *
     * @return the selected color.
     */
    public @ColorInt int getColor() {
        return selectedColor;
    }

    /**
     * gets an alpha value from the selected color.
     *
     * @return alpha from the selected color.
     */
    @Override
    public @FloatRange(from = 0.0, to = 1.0) float getAlpha() {
        return Color.alpha(getColor()) / 255f;
    }

    /**
     * gets the selected pure color without alpha and brightness.
     *
     * @return the selected pure color.
     */
    public @ColorInt int getPureColor() {
        return selectedPureColor;
    }

    /**
     * sets the pure color.
     *
     * @param color the pure color.
     */
    public void setPureColor(@ColorInt int color) {
        this.selectedPureColor = color;
    }

    /**
     * gets the {@link ColorInfo} of the selected color.
     *
     * @return {@link ColorInfo}.
     */
    public ColorInfo getColorInfo() {
        return new ColorInfo(getColor());
    }


    /**
     * gets a debounce duration.
     *
     * <p>only emit a color to the listener if a particular timespan has passed without it emitting
     * another value.
     *
     * @return debounceDuration.
     */
    public long getDebounceDuration() {
        return this.debounceDuration;
    }

    /**
     * sets a debounce duration.
     *
     * <p>only emit a color to the listener if a particular timespan has passed without it emitting
     * another value.
     *
     * @param debounceDuration intervals.
     */
    public void setDebounceDuration(long debounceDuration) {
        this.debounceDuration = debounceDuration;
    }

    /**
     * gets center coordinate of the selector.
     *
     * @param x coordinate x.
     * @param y coordinate y.
     * @return the center coordinate of the selector.
     */
    private Point getCenterPoint(int x, int y) {
        return new Point(x - (selector.getWidth() / 2), y - (selector.getMeasuredHeight() / 2));
    }

    /**
     * gets a selector.
     *
     * @return selector.
     */
    public ImageView getSelector() {
        return this.selector;
    }

    /**
     * gets a selector's selected coordinate x.
     *
     * @return a selected coordinate x.
     */
    public float getSelectorX() {
        return selector.getX() - (selector.getWidth() * 0.5f);
    }

    /**
     * gets a selector's selected coordinate y.
     *
     * @return a selected coordinate y.
     */
    public float getSelectorY() {
        return selector.getY() - (selector.getMeasuredHeight() * 0.5f);
    }

    /**
     * gets a selector's selected coordinate.
     *
     * @return a selected coordinate {@link Point}.
     */
    public Point getSelectedPoint() {
        return selectedPoint;
    }

    /**
     * changes selector's selected point with notifies about changes manually.
     *
     * @param x coordinate x of the selector.
     * @param y coordinate y of the selector.
     */
    public void setSelectorPoint(int x, int y) {
        Point mappedPoint = PointHelper.getColorPoint(this, new Point(x, y));
        int color = getColorFromBitmap(mappedPoint.x, mappedPoint.y);
        selectedPureColor = color;
        selectedColor = color;
        selectedPoint = new Point(mappedPoint.x, mappedPoint.y);
        setCoordinate(mappedPoint.x, mappedPoint.y);
        fireColorListener(getColor(), false);
    }

    /**
     * moves selector's selected point with notifies about changes manually.
     *
     * @param x coordinate x of the selector.
     * @param y coordinate y of the selector.
     */
    public void moveSelectorPoint(int x, int y, @ColorInt int color) {
        selectedPureColor = color;
        selectedColor = color;
        selectedPoint = new Point(x, y);
        setCoordinate(x, y);
        fireColorListener(getColor(), false);
    }

    /**
     * changes selector's selected point without notifies.
     *
     * @param x coordinate x of the selector.
     * @param y coordinate y of the selector.
     */
    public void setCoordinate(int x, int y) {
        selector.setX(x - (selector.getWidth() * 0.5f));
        selector.setY(y - (selector.getMeasuredHeight() * 0.5f));
    }

    /**
     * select a point by a specific color. this method will not work if the default palette drawable
     * is not {@link ColorPalette}.
     *
     * @param color a starting color.
     */
    public void setInitialColor(@ColorInt final int color) {
        if (getPreferenceName() == null
                || (getPreferenceName() != null
                && preferenceManager.getColor(getPreferenceName(), -1) == -1)) {
            post(
                    () -> {
                        try {
                            selectByHsvColor(color);
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    });
        }
    }

    /**
     * select a point by a specific color resource. this method will not work if the default palette
     * drawable is not {@link ColorPalette}.
     *
     * @param colorRes a starting color resource.
     */
    public void setInitialColorRes(@ColorRes final int colorRes) {
        setInitialColor(ContextCompat.getColor(getContext(), colorRes));
    }

    /**
     * changes selector's selected point by a specific color.
     *
     * <p>It will throw an exception if the default palette drawable is not {@link ColorPalette}.
     *
     * @param color color.
     */
    public void selectByHsvColor(@ColorInt int color) throws IllegalAccessException {
        if (palette.getDrawable() instanceof ColorPalette) {
            float[] hsv = new float[3];
            Color.colorToHSV(color, hsv);

            float centerX = getWidth() * 0.5f;
            float centerY = getHeight() * 0.5f;
            float radius = hsv[1] * Math.min(centerX, centerY);
            int pointX = (int) (radius * Math.cos(Math.toRadians(hsv[0])) + centerX);
            int pointY = (int) (-radius * Math.sin(Math.toRadians(hsv[0])) + centerY);

            Point mappedPoint = PointHelper.getColorPoint(this, new Point(pointX, pointY));
            selectedPureColor = color;
            selectedColor = color;
            selectedPoint = new Point(mappedPoint.x, mappedPoint.y);
            if (getAlphaSlideBar() != null) {
                getAlphaSlideBar().setSelectorByHalfSelectorPosition(getAlpha());
            }
            if (getBrightnessSlider() != null) {
                getBrightnessSlider().setSelectorByHalfSelectorPosition(hsv[2]);
            }
            setCoordinate(mappedPoint.x, mappedPoint.y);
            fireColorListener(getColor(), false);
        } else {
            throw new IllegalAccessException(
                    "selectByHsvColor(@ColorInt int color) can be called only "
                            + "when the palette is an instance of ColorHsvPalette. Use setHsvPaletteDrawable();");
        }
    }

    /**
     * changes selector's selected point by a specific color resource.
     *
     * <p>It may not work properly if change the default palette drawable.
     *
     * @param resource a color resource.
     */
    public void selectByHsvColorRes(@ColorRes int resource) throws IllegalAccessException {
        selectByHsvColor(ContextCompat.getColor(getContext(), resource));
    }

    /**
     * The default palette drawable is {@link ColorPalette} if not be set the palette drawable
     * manually. This method can be used for changing as {@link ColorPalette} from another palette
     * drawable.
     */
    public void setHsvPaletteDrawable() {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
        setPaletteDrawable(new ColorPalette(getResources(), bitmap));
    }

    /**
     * changes palette drawable manually.
     *
     * @param drawable palette drawable.
     */
    public void setPaletteDrawable(Drawable drawable) {
        removeView(palette);
        palette = new ImageView(getContext());
        paletteDrawable = drawable;
        palette.setImageDrawable(paletteDrawable);
        addView(palette);

        removeView(selector);
        addView(selector);

        selectedPureColor = Color.WHITE;
        notifyToSlideBars();


        if (!VISIBLE_FLAG) {
            VISIBLE_FLAG = true;
            if (selector != null) {
                selector_alpha = selector.getAlpha();
                selector.setAlpha(0.0f);
            }
        }
    }

    /**
     * changes selector drawable manually.
     *
     * @param drawable selector drawable.
     */
    public void setSelectorDrawable(Drawable drawable) {
        selector.setImageDrawable(drawable);
    }

    /**
     * selects the center of the palette manually.
     */
    public void selectCenter() {
        setSelectorPoint(getWidth() / 2, getMeasuredHeight() / 2);
    }

    /**
     * sets enabling or not the ColorPickerView and slide bars.
     *
     * @param enabled true/false flag for making enable or not.
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        selector.setVisibility(enabled ? VISIBLE : INVISIBLE);

        if (getAlphaSlideBar() != null) {
            getAlphaSlideBar().setEnabled(enabled);
        }

        if (getBrightnessSlider() != null) {
            getBrightnessSlider().setEnabled(enabled);
        }

        if (enabled) {
            palette.clearColorFilter();
        } else {
            int color = Color.argb(255, 255, 255, 255);
            palette.setColorFilter(color);
        }
    }

    /**
     * gets an {@link UpdateMode}.
     *
     * @return {@link UpdateMode}.
     */
    public UpdateMode getActionMode() {
        return this.updateMode;
    }

    /**
     * sets an {@link UpdateMode}.
     *
     * @param updateMode {@link UpdateMode}.
     */
    public void setActionMode(UpdateMode updateMode) {
        this.updateMode = updateMode;
    }

    /**
     * gets an {@link AlphaSlider}.
     *
     * @return {@link AlphaSlider}.
     */
    public @Nullable AlphaSlider getAlphaSlideBar() {
        return alphaSlider;
    }

    /**
     * linking an {@link AlphaSlider} on the {@link ColorPickerView}.
     *
     * @param alphaSlider {@link AlphaSlider}.
     */
    public void attachAlphaSlider(@NonNull AlphaSlider alphaSlider) {
        this.alphaSlider = alphaSlider;
        alphaSlider.attachColorPickerView(this);
        alphaSlider.notifyColor();

        if (getPreferenceName() != null) {
            alphaSlider.setPreferenceName(getPreferenceName());
        }
    }

    /**
     * gets an {@link BrightnessSlider}.
     *
     * @return {@link BrightnessSlider}.
     */
    public @Nullable BrightnessSlider getBrightnessSlider() {
        return brightnessSlider;
    }

    /**
     * linking an {@link BrightnessSlider} on the {@link ColorPickerView}.
     *
     * @param brightnessSlider {@link BrightnessSlider}.
     */
    public void attachBrightnessSlider(@NonNull BrightnessSlider brightnessSlider) {
        this.brightnessSlider = brightnessSlider;
        brightnessSlider.attachColorPickerView(this);
        brightnessSlider.notifyColor();

        if (getPreferenceName() != null) {
            brightnessSlider.setPreferenceName(getPreferenceName());
        }
    }

    /**
     * gets the preference name.
     *
     * @return preference name.
     */
    public @Nullable String getPreferenceName() {
        return preferenceName;
    }

    /**
     * sets the preference name.
     *
     * @param preferenceName preference name.
     */
    public void setPreferenceName(@Nullable String preferenceName) {
        this.preferenceName = preferenceName;
        if (this.alphaSlider != null) {
            this.alphaSlider.setPreferenceName(preferenceName);
        }
        if (this.brightnessSlider != null) {
            this.brightnessSlider.setPreferenceName(preferenceName);
        }
    }

    /**
     * sets the {@link LifecycleOwner}.
     *
     * @param lifecycleOwner {@link LifecycleOwner}.
     */
    public void setLifecycleOwner(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().addObserver(this);
    }

    /**
     * removes this color picker observer from the the {@link LifecycleOwner}.
     *
     * @param lifecycleOwner {@link LifecycleOwner}.
     */
    public void removeLifecycleOwner(LifecycleOwner lifecycleOwner) {
        lifecycleOwner.getLifecycle().removeObserver(this);
    }

    /**
     * This method invoked by the {@link LifecycleOwner}'s life cycle.
     *
     * <p>OnDestroy would be called on the {@link LifecycleOwner}, all of the color picker data will
     * be saved automatically.
     */
    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    public void onDestroy() {
        preferenceManager.saveColorPickerData(this);
    }

    /**
     * Builder class for create {@link ColorPickerView}.
     */
    public static class Builder {
        private final Context context;
        private ColorListener colorListener;
        private int debounceDuration = 0;
        private Drawable paletteDrawable;
        private Drawable selectorDrawable;
        private AlphaSlider alphaSlider;
        private BrightnessSlider brightnessSlider;
        private UpdateMode updateMode = UpdateMode.ALWAYS;
        @ColorInt
        private int initialColor = 0;

        @FloatRange(from = 0.0, to = 1.0)
        private float selector_alpha = 1.0f;

        @Dp
        private int selectorSize = 0;
        @Dp
        private int width = LayoutParams.MATCH_PARENT;
        @Dp
        private int height = LayoutParams.MATCH_PARENT;
        private String preferenceName;
        private LifecycleOwner lifecycleOwner;

        public Builder(Context context) {
            this.context = context;
        }

        public Builder setColorListener(ColorListener colorListener) {
            this.colorListener = colorListener;
            return this;
        }

        public Builder setDebounceDuration(int debounceDuration) {
            this.debounceDuration = debounceDuration;
            return this;
        }

        public Builder setPaletteDrawable(@NonNull Drawable palette) {
            this.paletteDrawable = palette;
            return this;
        }

        public Builder setSelectorDrawable(@NonNull Drawable selector) {
            this.selectorDrawable = selector;
            return this;
        }

        public Builder setAlphaSlideBar(AlphaSlider alphaSlider) {
            this.alphaSlider = alphaSlider;
            return this;
        }

        public Builder setBrightnessSlideBar(BrightnessSlider brightnessSlider) {
            this.brightnessSlider = brightnessSlider;
            return this;
        }

        public Builder setActionMode(UpdateMode updateMode) {
            this.updateMode = updateMode;
            return this;
        }

        public Builder setSelectorAlpha(@FloatRange(from = 0.0, to = 1.0) float alpha) {
            this.selector_alpha = alpha;
            return this;
        }

        public Builder setSelectorSize(@Dp int size) {
            this.selectorSize = size;
            return this;
        }

        public Builder setWidth(@Dp int width) {
            this.width = width;
            return this;
        }

        public Builder setHeight(@Dp int height) {
            this.height = height;
            return this;
        }

        public Builder setInitialColor(@ColorInt int initialColor) {
            this.initialColor = initialColor;
            return this;
        }

        public Builder setInitialColorRes(@ColorRes int initialColorRes) {
            this.initialColor = ContextCompat.getColor(context, initialColorRes);
            return this;
        }

        public Builder setPreferenceName(@Nullable String preferenceName) {
            this.preferenceName = preferenceName;
            return this;
        }

        public Builder setLifecycleOwner(LifecycleOwner lifecycleOwner) {
            this.lifecycleOwner = lifecycleOwner;
            return this;
        }

        public ColorPickerView build() {
            ColorPickerView colorPickerView = new ColorPickerView(context);
            colorPickerView.onCreateByBuilder(this);
            return colorPickerView;
        }
    }
}
