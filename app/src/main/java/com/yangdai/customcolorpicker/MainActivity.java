package com.yangdai.customcolorpicker;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.yangdai.colorpickerlib.ColorPickerDialog;
import com.yangdai.colorpickerlib.ColorListener;

/**
 * @author 30415
 */
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
                .setIcon(android.R.drawable.sym_def_app_icon)
                .setTitle("选择颜色")
                .setPositiveButton("确定", (ColorListener) (colorInfo, fromUser) -> {
                    textView.setText(colorInfo.getHexCode());
                    textView.setTextColor(colorInfo.getColor());
                })
                .show();
//        ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this);
//        colorPickerDialog.show();

//        ColorPickerView colorPickerView = new ColorPickerView(this);
//        colorPickerView.setImageResource(R.drawable.ic_launcher_foreground);
//        colorPickerView.setOnColorSelectedListener(new ColorListener() {
//            @Override
//            public void onColorSelected(ColorInfo colorInfo, boolean fromUser) {
//
//            }
//        });
    }
}