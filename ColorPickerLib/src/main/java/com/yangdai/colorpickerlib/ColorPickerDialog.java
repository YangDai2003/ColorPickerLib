package com.yangdai.colorpickerlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/**
 * 这是一个用于颜色选择的自定义Dialog，默认使用ColorPalette
 *
 * @author 30415
 */
@SuppressWarnings("unused")
public class ColorPickerDialog extends AlertDialog {
    private ColorPickerView colorPickerView;
    private LinearLayout rootView;

    public ColorPickerDialog(@NonNull Context context) {
        this(context, R.style.CustomDialog);
    }

    public ColorPickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        @SuppressLint("InflateParams")
        View dialogView = LayoutInflater.from(context).inflate(R.layout.colorpicker_dialog, null);
        setView(dialogView);
        colorPickerView = dialogView.findViewById(R.id.colorPickerView);
        rootView = dialogView.findViewById(R.id.rootView);
        colorPickerView.setImageDrawable(new ColorPalette());
    }

    public void addView(View view) {
        rootView.addView(view);
    }

    public void addView(View view, int index) {
        rootView.addView(view, index);
    }

    public void removeView(View view) {
        rootView.removeView(view);
    }

    public void removeViewAt(int index) {
        rootView.removeViewAt(index);
    }

    public void setImageDrawable(Drawable drawable) {
        colorPickerView.setImageDrawable(drawable);
    }

    public void setImageBitmap(Bitmap bitmap) {
        colorPickerView.setImageBitmap(bitmap);
    }

    public void setImageResource(int resourceId) {
        colorPickerView.setImageResource(resourceId);
    }

    public void setImageUri(Uri uri) {
        colorPickerView.setImageUri(uri);
    }

    public void setImageFile(String path) {
        colorPickerView.setImageFile(path);
    }

    public void setCrosshairBackgroundResource(int resourceId) {
        colorPickerView.setCrosshairResource(resourceId);
    }

    public void setCrosshairSize(int size) {
        colorPickerView.setCrosshairSize(size);
    }

    public void setOnColorSelectedListener(ColorListener listener) {
        colorPickerView.setOnColorSelectedListener(listener);
    }

    public void setPositiveButton(CharSequence text, OnClickListener listener) {
        setButton(BUTTON_POSITIVE, text, listener);
    }

    public void setPositiveButton(int textId, OnClickListener listener) {
        setButton(BUTTON_POSITIVE, getContext().getString(textId), listener);
    }

    public void setPositiveButton(CharSequence text, ColorListener listener) {
        setButton(BUTTON_POSITIVE, text, (dialog, which) -> {
            ColorInfo colorInfo = colorPickerView.getSelectedColor();
            if (colorInfo == null) {
                return;
            }
            listener.onColorSelected(colorInfo, true);
        });
    }

    public void setPositiveButton(int textId, ColorListener listener) {
        setPositiveButton(getContext().getString(textId), listener);
    }

    public void setNegativeButton(CharSequence text, OnClickListener listener) {
        setButton(BUTTON_NEGATIVE, text, listener);
    }

    public void setNegativeButton(int textId, OnClickListener listener) {
        setButton(BUTTON_NEGATIVE, getContext().getString(textId), listener);
    }

    public void setNeutralButton(CharSequence text, OnClickListener listener) {
        setButton(BUTTON_NEUTRAL, text, listener);
    }

    public void setNeutralButton(int textId, OnClickListener listener) {
        setButton(BUTTON_NEUTRAL, getContext().getString(textId), listener);
    }

    public static class Builder extends AlertDialog.Builder {
        private final ColorPickerDialog colorPickerDialog;

        public Builder(@NonNull Context context) {
            this(context, R.style.CustomDialog);
        }

        public Builder(@NonNull Context context, int themeResId) {
            super(context, themeResId);
            colorPickerDialog = new ColorPickerDialog(context, themeResId);
        }

        public Builder addView(View view) {
            colorPickerDialog.addView(view);
            return this;
        }

        public Builder addView(View view, int index) {
            colorPickerDialog.addView(view, index);
            return this;
        }

        public Builder removeView(View view) {
            colorPickerDialog.removeView(view);
            return this;
        }

        public Builder removeViewAt(int index) {
            colorPickerDialog.removeViewAt(index);
            return this;
        }

        public Builder setImageDrawable(Drawable drawable) {
            colorPickerDialog.setImageDrawable(drawable);
            return this;
        }

        public Builder setImageBitmap(Bitmap bitmap) {
            colorPickerDialog.setImageBitmap(bitmap);
            return this;
        }

        public Builder setImageResource(int resourceId) {
            colorPickerDialog.setImageResource(resourceId);
            return this;
        }

        public Builder setImageUri(Uri uri) {
            colorPickerDialog.setImageUri(uri);
            return this;
        }

        public Builder setImageFile(String file) {
            colorPickerDialog.setImageFile(file);
            return this;
        }

        public Builder setCrosshairBackgroundResource(int resourceId) {
            colorPickerDialog.setCrosshairBackgroundResource(resourceId);
            return this;
        }

        public Builder setCrosshairSize(int size) {
            colorPickerDialog.setCrosshairSize(size);
            return this;
        }

        public Builder setOnColorSelectedListener(ColorListener listener) {
            colorPickerDialog.setOnColorSelectedListener(listener);
            return this;
        }

        public Builder setPositiveButton(CharSequence text, ColorListener listener) {
            colorPickerDialog.setPositiveButton(text, listener);
            return this;
        }

        public Builder setPositiveButton(int textId, ColorListener listener) {
            colorPickerDialog.setPositiveButton(textId, listener);
            return this;
        }

        @Override
        public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            colorPickerDialog.setPositiveButton(text, listener);
            return this;
        }

        @Override
        public Builder setPositiveButton(int textId, OnClickListener listener) {
            colorPickerDialog.setPositiveButton(textId, listener);
            return this;
        }

        @Override
        public Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            colorPickerDialog.setNegativeButton(text, listener);
            return this;
        }

        @Override
        public Builder setNegativeButton(int textId, OnClickListener listener) {
            colorPickerDialog.setNegativeButton(textId, listener);
            return this;
        }

        @Override
        public Builder setNeutralButton(CharSequence text, OnClickListener listener) {
            colorPickerDialog.setNeutralButton(text, listener);
            return this;
        }

        @Override
        public Builder setNeutralButton(int textId, OnClickListener listener) {
            colorPickerDialog.setNeutralButton(textId, listener);
            return this;
        }

        @Override
        public Builder setTitle(int titleId) {
            colorPickerDialog.setTitle(titleId);
            return this;
        }

        @Override
        public Builder setTitle(@Nullable CharSequence title) {
            colorPickerDialog.setTitle(title);
            return this;
        }

        @Override
        public Builder setIcon(int iconId) {
            colorPickerDialog.setIcon(iconId);
            return this;
        }

        @Override
        public Builder setIcon(@Nullable Drawable icon) {
            colorPickerDialog.setIcon(icon);
            return this;
        }

        @Override
        public Builder setCancelable(boolean cancelable) {
            colorPickerDialog.setCancelable(cancelable);
            return this;
        }

        @Override
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            colorPickerDialog.setOnCancelListener(onCancelListener);
            return this;
        }

        @NonNull
        @Override
        public ColorPickerDialog create() {
            return colorPickerDialog;
        }
    }
}
