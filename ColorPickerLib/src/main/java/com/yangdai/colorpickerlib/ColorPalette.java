package com.yangdai.colorpickerlib;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * ColorPalette 是一个使用（色调、饱和度、明度）颜色模型构建的默认可绘制调色板，
 * 用于交替表示 RGB 颜色模型。
 * @author 30415
 */
public class ColorPalette extends Drawable {

    private final Paint huePaint;
    private final Paint saturationPaint;

    public ColorPalette() {
        this.huePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.saturationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        int width = getBounds().width();
        int height = getBounds().height();
        float centerX = width * 0.5f;
        float centerY = height * 0.5f;
        float radius = Math.min(width, height) * 0.5f;

        Shader sweepShader =
                new SweepGradient(
                        centerX,
                        centerY,
                        new int[]{
                                Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN, Color.GREEN, Color.YELLOW, Color.RED
                        },
                        new float[]{0.000f, 0.166f, 0.333f, 0.499f, 0.666f, 0.833f, 0.999f});

        huePaint.setShader(sweepShader);

        Shader saturationShader =
                new RadialGradient(
                        centerX, centerY, radius, Color.WHITE, 0x00FFFFFF, Shader.TileMode.CLAMP);
        saturationPaint.setShader(saturationShader);

        canvas.drawCircle(centerX, centerY, radius, huePaint);
        canvas.drawCircle(centerX, centerY, radius, saturationPaint);
    }

    @Override
    public void setAlpha(int alpha) {
        huePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(@Nullable ColorFilter colorFilter) {
        huePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }
}
