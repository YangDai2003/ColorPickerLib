package com.yangdai.customcolorpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.yangdai.colorpickerlib.ColorPickerDialog;
import com.yangdai.colorpickerlib.interfaces.ColorSelectionListener;

public class MainActivity extends AppCompatActivity {
    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textView = findViewById(R.id.text_view);
        textView.setOnClickListener(view -> showDialog());


    }

    private void showDialog() {
        new ColorPickerDialog.Builder(this)
                .attachAlphaSlideBar(true)
                .attachBrightnessSlideBar(true)
                .setPositiveButton(android.R.string.ok, (ColorSelectionListener) (colorInfo, fromUser) -> textView.setText(colorInfo.getHexCode()))
                .setNegativeButton(android.R.string.cancel, null)
                .setCancelable(false)
                .show();
    }
}