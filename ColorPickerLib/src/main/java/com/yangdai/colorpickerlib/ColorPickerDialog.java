package com.yangdai.colorpickerlib;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

/**
 * @author 30415
 */
public class ColorPickerDialog extends AlertDialog {
    private ColorPickerView colorPickerView;

    public ColorPickerDialog(@NonNull Context context) {
        this(context, R.style.CustomDialog);
    }

    public ColorPickerDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    private void init(Context context) {
        @SuppressLint("InflateParams")
        View dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_colorpickerview, null);
        colorPickerView = dialogView.findViewById(R.id.colorPickerView);
        colorPickerView.setImageDrawable(new ColorPalette());
        setView(dialogView);
    }

    public void setImageDrawable(Drawable drawable) {
        colorPickerView.setImageDrawable(drawable);
    }

    public void setOnColorSelectedListener(ColorSelectionListener listener) {
        colorPickerView.setOnColorSelectedListener(listener);
    }

    public void setPositiveButton(CharSequence text, OnClickListener listener) {
        setButton(BUTTON_POSITIVE, text, listener);
    }

    public void setPositiveButton(int textId, OnClickListener listener) {
        setButton(BUTTON_POSITIVE, getContext().getString(textId), listener);
    }

    public void setPositiveButton(CharSequence text, ColorSelectionListener listener) {
        setButton(BUTTON_POSITIVE, text, (dialog, which) -> {
            ColorInfo colorInfo = colorPickerView.getSelectedColor();
            if (colorInfo == null) {
                return;
            }
            listener.onColorSelected(colorInfo, true);
        });
    }

    public void setPositiveButton(int textId, ColorSelectionListener listener) {
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

        public Builder setImageDrawable(Drawable drawable) {
            colorPickerDialog.setImageDrawable(drawable);
            return this;
        }

        public Builder setOnColorSelectedListener(ColorSelectionListener listener) {
            colorPickerDialog.setOnColorSelectedListener(listener);
            return this;
        }

        public Builder setPositiveButton(CharSequence text, ColorSelectionListener listener) {
            colorPickerDialog.setPositiveButton(text, listener);
            return this;
        }
        public Builder setPositiveButton(int textId, ColorSelectionListener listener) {
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
