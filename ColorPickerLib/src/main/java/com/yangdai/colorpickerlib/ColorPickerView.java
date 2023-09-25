package com.yangdai.colorpickerlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;

/**
 * @author 30415
 */
public class ColorPickerView extends FrameLayout {

    private ImageView imageView;
    private View crosshairView;
    private ColorSelectionListener onColorSelectedListener;
    private ColorInfo colorInfo;
    private UpdateMode updateMode;

    public ColorPickerView(@NonNull Context context) {
        this(context, null);
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ColorPickerView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorPickerView);
        int updateModeValue = a.getInt(R.styleable.ColorPickerView_updateMode, 0);
        updateMode = UpdateMode.values()[updateModeValue];
        a.recycle();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void init() {
        imageView = new ImageView(getContext());
        imageView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(imageView);

        crosshairView = new View(getContext());
        int crosshairSize = 100;
        LayoutParams crosshairParams = new LayoutParams(crosshairSize, crosshairSize);
        crosshairParams.gravity = Gravity.CENTER;
        crosshairView.setLayoutParams(crosshairParams);
        crosshairView.setBackgroundResource(R.drawable.crosshair);
        addView(crosshairView);

        imageView.setOnTouchListener((v, event) -> {
            int action = event.getAction();
            if (action == MotionEvent.ACTION_DOWN) {
                // 当按下时，根据updateMode判断是否更新颜色
                if (updateMode == UpdateMode.ALWAYS) {
                    updateColor(event);
                }
            } else if (action == MotionEvent.ACTION_MOVE) {
                // 当移动时，根据updateMode判断是否更新颜色
                if (updateMode == UpdateMode.ALWAYS) {
                    updateColor(event);
                }
            } else if (action == MotionEvent.ACTION_UP) {
                // 当松手时，只有updateMode为AFTER时才更新颜色
                if (updateMode == UpdateMode.AFTER) {
                    updateColor(event);
                }
            }
            return true;
        });
    }

    private void updateColor(MotionEvent event) {
        int[] imageViewLocation = new int[2];
        imageView.getLocationOnScreen(imageViewLocation);

        // 获取触摸事件的位置相对于ImageView
        float touchX = event.getRawX() - imageViewLocation[0];
        float touchY = event.getRawY() - imageViewLocation[1];

        // 获取十字准心图片的宽度和高度
        int crosshairWidth = crosshairView.getWidth();
        int crosshairHeight = crosshairView.getHeight();

        // 计算十字准心图片的左上角位置
        float crosshairLeft = touchX - (float) crosshairWidth / 2;
        float crosshairTop = touchY - (float) crosshairHeight / 2;

        // 设置十字准心图片的位置
        crosshairView.setX(crosshairLeft);
        crosshairView.setY(crosshairTop);


        // 获取 touched pixel
        imageView.invalidate();
        Bitmap bitmap = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
        imageView.draw(new Canvas(bitmap));

        try {
            int pixel = bitmap.getPixel((int) touchX, (int) touchY);

            // 获取 RGB 值
            int a = Color.alpha(pixel);
            int r = Color.red(pixel);
            int g = Color.green(pixel);
            int b = Color.blue(pixel);

            int selectedColor = Color.argb(a, r, g, b);
            colorInfo = new ColorInfo(selectedColor);
            if (onColorSelectedListener != null) {
                onColorSelectedListener.onColorSelected(colorInfo, true);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void setUpdateMode(UpdateMode updateMode){
        this.updateMode = updateMode;
    }

    public void setImageBitmap(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }

    public void setImageResource(int resourceId) {
        imageView.setImageResource(resourceId);
    }

    public void setImageDrawable(Drawable drawable) {
        imageView.setImageDrawable(drawable);
    }

    public void setImageFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(filePath);
            imageView.setImageBitmap(bitmap);
        }
    }

    public void setImageUri(Uri imageUri) {
        imageView.setImageURI(imageUri);
    }

    public void setOnColorSelectedListener(ColorSelectionListener listener) {
        this.onColorSelectedListener = listener;
    }

    public void setCrosshairSize(int size) {
        ViewGroup.LayoutParams layoutParams = crosshairView.getLayoutParams();
        layoutParams.width = size;
        layoutParams.height = size;
        crosshairView.setLayoutParams(layoutParams);
    }

    public void setCrosshairBackgroundResource(int resourceId) {
        crosshairView.setBackgroundResource(resourceId);
    }

    public void setCrosshairVisibility(int visibility) {
        crosshairView.setVisibility(visibility);
    }

    public ColorInfo getSelectedColor() {
        return colorInfo;
    }
}
