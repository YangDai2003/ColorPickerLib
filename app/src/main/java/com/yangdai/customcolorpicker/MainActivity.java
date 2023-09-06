package com.yangdai.customcolorpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.yangdai.colorpickerlib.ColorInfo;
import com.yangdai.colorpickerlib.ColorPickerDialog;
import com.yangdai.colorpickerlib.interfaces.ColorSelectionListener;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        new ColorPickerDialog.Builder(this)
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .setPositiveButton(android.R.string.ok, (ColorSelectionListener) (colorInfo, fromUser) -> {

                })
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .show();
    }
}