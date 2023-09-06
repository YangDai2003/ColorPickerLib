package com.yangdai.colorpickerlib;

import android.content.Context;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.yangdai.colorpickerlib.databinding.DialogColorpickerviewBinding;
import com.yangdai.colorpickerlib.interfaces.ColorSelectionListener;
import com.yangdai.colorpickerlib.interfaces.ColorChangeListener;
import com.yangdai.colorpickerlib.interfaces.ColorListener;
import com.yangdai.colorpickerlib.preference.ColorPickerPreferenceManager;
import com.yangdai.colorpickerlib.sliders.AlphaSlider;
import com.yangdai.colorpickerlib.sliders.BrightnessSlider;


@SuppressWarnings("unused")
public class ColorPickerDialog extends AlertDialog {

    private ColorPickerView colorPickerView;

    public ColorPickerDialog(Context context) {
        super(context);
    }

    /**
     * {@link ColorPickerDialog} 的构造器.
     */
    public static class Builder extends AlertDialog.Builder {
        private DialogColorpickerviewBinding dialogBinding;
        private ColorPickerView colorPickerView;
        private boolean shouldAttachAlphaSlideBar = true;
        private boolean shouldAttachBrightnessSlideBar = true;

        public Builder(Context context) {
            super(context, R.style.CustomDialog);
            onCreate();
        }

        public Builder(Context context, int themeResId) {
            super(context, themeResId);
            onCreate();
        }

        private void onCreate() {
            LayoutInflater layoutInflater = LayoutInflater.from(getContext());
            this.dialogBinding =
                    DialogColorpickerviewBinding.inflate(layoutInflater, null, false);

            this.colorPickerView = dialogBinding.colorPickerView;
            this.colorPickerView.attachAlphaSlider(dialogBinding.alphaSlider);
            this.colorPickerView.attachBrightnessSlider(dialogBinding.brightnessSlider);
            this.colorPickerView.setColorListener(
                    (ColorSelectionListener) (envelope, fromUser) -> {
                        // no stubs
                    });
            super.setView(dialogBinding.getRoot());
        }

        /**
         * gets {@link ColorPickerView} on {@link Builder}.
         *
         * @return {@link ColorPickerView}.
         */
        public ColorPickerView getColorPickerView() {
            return colorPickerView;
        }

        /**
         * sets {@link ColorPickerView} manually.
         *
         * @param colorPickerView {@link ColorPickerView}.
         * @return {@link Builder}.
         */
        public Builder setColorPickerView(ColorPickerView colorPickerView) {
            this.dialogBinding.colorPickerViewFrame.removeAllViews();
            this.dialogBinding.colorPickerViewFrame.addView(colorPickerView);
            return this;
        }

        /**
         * if true, attaches a {@link AlphaSlider} on the {@link ColorPickerDialog}.
         *
         * @param value true or false.
         * @return {@link Builder}.
         */
        public Builder attachAlphaSlideBar(boolean value) {
            this.shouldAttachAlphaSlideBar = value;
            return this;
        }

        /**
         * if true, attaches a {@link BrightnessSlider} on the {@link ColorPickerDialog}.
         *
         * @param value true or false.
         * @return {@link Builder}.
         */
        public Builder attachBrightnessSlideBar(boolean value) {
            this.shouldAttachBrightnessSlideBar = value;
            return this;
        }

        /**
         * sets the preference name.
         *
         * @param preferenceName preference name.
         * @return {@link Builder}.
         */
        public Builder setPreferenceName(String preferenceName) {
            if (getColorPickerView() != null) {
                getColorPickerView().setPreferenceName(preferenceName);
            }
            return this;
        }

        /**
         * sets positive button with {@link ColorListener} on the {@link ColorPickerDialog}.
         *
         * @param textId        string resource integer id.
         * @param colorListener {@link ColorChangeListener}.
         * @return {@link Builder}.
         */
        public Builder setPositiveButton(int textId, final ColorListener colorListener) {
            super.setPositiveButton(textId, getOnClickListener(colorListener));
            return this;
        }

        /**
         * sets positive button with {@link ColorListener} on the {@link ColorPickerDialog}.
         *
         * @param text          string text value.
         * @param colorListener {@link ColorChangeListener}.
         * @return {@link Builder}.
         */
        public Builder setPositiveButton(
                CharSequence text, final ColorListener colorListener) {
            super.setPositiveButton(text, getOnClickListener(colorListener));
            return this;
        }

        @Override
        public Builder setNegativeButton(int textId, OnClickListener listener) {
            super.setNegativeButton(textId, listener);
            return this;
        }

        @Override
        public Builder setNegativeButton(CharSequence text, OnClickListener listener) {
            super.setNegativeButton(text, listener);
            return this;
        }

        private OnClickListener getOnClickListener(final ColorListener colorListener) {
            return (dialogInterface, i) -> {
                if (colorListener instanceof ColorChangeListener) {
                    ((ColorChangeListener) colorListener).onColorChanged(getColorPickerView().getColor(), true);
                } else if (colorListener instanceof ColorSelectionListener) {
                    ((ColorSelectionListener) colorListener)
                            .onColorSelected(getColorPickerView().getColorEnvelope(), true);
                }
                if (getColorPickerView() != null) {
                    ColorPickerPreferenceManager.getInstance(getContext())
                            .saveColorPickerData(getColorPickerView());
                }
            };
        }

        /**
         * shows a created {@link ColorPickerDialog}.
         *
         * @return {@link AlertDialog}.
         */
        @Override
        @NonNull
        public AlertDialog create() {
            if (getColorPickerView() != null) {
                this.dialogBinding.colorPickerViewFrame.removeAllViews();
                this.dialogBinding.colorPickerViewFrame.addView(getColorPickerView());

                attachSliders();
            }

            super.setView(dialogBinding.getRoot());
            return super.create();
        }

        private void attachSliders() {
            AlphaSlider alphaSlider = getColorPickerView().getAlphaSlideBar();
            if (shouldAttachAlphaSlideBar && alphaSlider != null) {
                this.dialogBinding.alphaSliderFrame.removeAllViews();
                this.dialogBinding.alphaSliderFrame.addView(alphaSlider);
                this.getColorPickerView().attachAlphaSlider(alphaSlider);
            } else if (!shouldAttachAlphaSlideBar) {
                this.dialogBinding.alphaSliderFrame.removeAllViews();
            }

            BrightnessSlider brightnessSlider = getColorPickerView().getBrightnessSlider();
            if (shouldAttachBrightnessSlideBar && brightnessSlider != null) {
                this.dialogBinding.brightnessSliderFrame.removeAllViews();
                this.dialogBinding.brightnessSliderFrame.addView(brightnessSlider);
                this.getColorPickerView().attachBrightnessSlider(brightnessSlider);
            } else if (!shouldAttachBrightnessSlideBar) {
                this.dialogBinding.brightnessSliderFrame.removeAllViews();
            }
        }

        @Override
        public Builder setTitle(int titleId) {
            super.setTitle(titleId);
            return this;
        }

        @Override
        public Builder setTitle(CharSequence title) {
            super.setTitle(title);
            return this;
        }

        @Override
        public Builder setCustomTitle(View customTitleView) {
            super.setCustomTitle(customTitleView);
            return this;
        }

        @Override
        public Builder setMessage(int messageId) {
            super.setMessage(getContext().getString(messageId));
            return this;
        }

        @Override
        public Builder setMessage(CharSequence message) {
            super.setMessage(message);
            return this;
        }

        @Override
        public Builder setIcon(int iconId) {
            super.setIcon(iconId);
            return this;
        }

        @Override
        public Builder setIcon(Drawable icon) {
            super.setIcon(icon);
            return this;
        }

        @Override
        public Builder setIconAttribute(int attrId) {
            super.setIconAttribute(attrId);
            return this;
        }

        @Override
        public Builder setCancelable(boolean cancelable) {
            super.setCancelable(cancelable);
            return this;
        }

        @Override
        public Builder setOnCancelListener(OnCancelListener onCancelListener) {
            super.setOnCancelListener(onCancelListener);
            return this;
        }

        @Override
        public Builder setOnDismissListener(OnDismissListener onDismissListener) {
            super.setOnDismissListener(onDismissListener);
            return this;
        }

        @Override
        public Builder setOnKeyListener(OnKeyListener onKeyListener) {
            super.setOnKeyListener(onKeyListener);
            return this;
        }

        @Override
        public Builder setPositiveButton(int textId, OnClickListener listener) {
            super.setPositiveButton(textId, listener);
            return this;
        }

        @Override
        public Builder setPositiveButton(CharSequence text, OnClickListener listener) {
            super.setPositiveButton(text, listener);
            return this;
        }

        @Override
        public Builder setNeutralButton(int textId, OnClickListener listener) {
            super.setNeutralButton(textId, listener);
            return this;
        }

        @Override
        public Builder setNeutralButton(CharSequence text, OnClickListener listener) {
            super.setNeutralButton(text, listener);
            return this;
        }

        @Override
        public Builder setItems(int itemsId, OnClickListener listener) {
            super.setItems(itemsId, listener);
            return this;
        }

        @Override
        public Builder setItems(CharSequence[] items, OnClickListener listener) {
            super.setItems(items, listener);
            return this;
        }

        @Override
        public Builder setAdapter(ListAdapter adapter, OnClickListener listener) {
            super.setAdapter(adapter, listener);
            return this;
        }

        @Override
        public Builder setCursor(Cursor cursor, OnClickListener listener, String labelColumn) {
            super.setCursor(cursor, listener, labelColumn);
            return this;
        }

        @Override
        public Builder setMultiChoiceItems(
                int itemsId, boolean[] checkedItems, OnMultiChoiceClickListener listener) {
            super.setMultiChoiceItems(itemsId, checkedItems, listener);
            return this;
        }

        @Override
        public Builder setMultiChoiceItems(
                CharSequence[] items, boolean[] checkedItems, OnMultiChoiceClickListener listener) {
            super.setMultiChoiceItems(items, checkedItems, listener);
            return this;
        }

        @Override
        public Builder setMultiChoiceItems(
                Cursor cursor,
                String isCheckedColumn,
                String labelColumn,
                OnMultiChoiceClickListener listener) {
            super.setMultiChoiceItems(cursor, isCheckedColumn, labelColumn, listener);
            return this;
        }

        @Override
        public Builder setSingleChoiceItems(int itemsId, int checkedItem, OnClickListener listener) {
            super.setSingleChoiceItems(itemsId, checkedItem, listener);
            return this;
        }

        @Override
        public Builder setSingleChoiceItems(
                Cursor cursor, int checkedItem, String labelColumn, OnClickListener listener) {
            super.setSingleChoiceItems(cursor, checkedItem, labelColumn, listener);
            return this;
        }

    }
}
