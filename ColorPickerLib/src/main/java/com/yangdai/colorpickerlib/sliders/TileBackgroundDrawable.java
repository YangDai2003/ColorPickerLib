package com.yangdai.colorpickerlib.sliders;

import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;

/**
 * 绘制透明度滑块的瓷砖样式背景
 *
 * @author 30415
 */
public class TileBackgroundDrawable extends Drawable {

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final int tileSize;
    private final int tileOddColor;
    private final int tileEvenColor;

    public TileBackgroundDrawable() {
        this(25, 0xFFFFFFFF, 0xFFCBCBCB);
    }

    public TileBackgroundDrawable(int tileSize, int tileOddColor, int tileEvenColor) {
        super();
        this.tileSize = tileSize;
        this.tileOddColor = tileOddColor;
        this.tileEvenColor = tileEvenColor;
        drawTiles();
    }

    public TileBackgroundDrawable(Builder builder) {
        super();
        this.tileSize = builder.tileSize;
        this.tileOddColor = builder.tileOddColor;
        this.tileEvenColor = builder.tileEvenColor;
        drawTiles();
    }

    private void drawTiles() {
        Bitmap bitmap = Bitmap.createBitmap(tileSize * 2, tileSize * 2, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Rect rect = new Rect(0, 0, tileSize, tileSize);

        Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bitmapPaint.setStyle(Paint.Style.FILL);

        bitmapPaint.setColor(tileOddColor);
        drawTile(canvas, rect, bitmapPaint, 0, 0);
        drawTile(canvas, rect, bitmapPaint, tileSize, tileSize);

        bitmapPaint.setColor(tileEvenColor);
        drawTile(canvas, rect, bitmapPaint, -tileSize, 0);
        drawTile(canvas, rect, bitmapPaint, tileSize, -tileSize);

        paint.setShader(
                new BitmapShader(bitmap, BitmapShader.TileMode.REPEAT, BitmapShader.TileMode.REPEAT));
    }

    private void drawTile(Canvas canvas, Rect rect, Paint bitmapPaint, int dx, int dy) {
        rect.offset(dx, dy);
        canvas.drawRect(rect, bitmapPaint);
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        canvas.drawPaint(paint);
    }

    @Override
    public void setAlpha(int alpha) {
        paint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        paint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.OPAQUE;
    }

    /**
     * Builder class for create {@link TileBackgroundDrawable}.
     */
    @SuppressWarnings("UnusedReturnValue")
    public static class Builder {
        private int tileSize = 25;
        private int tileOddColor = 0xFFFFFFFF;
        private int tileEvenColor = 0xFFCBCBCB;

        public int getTileSize() {
            return tileSize;
        }

        public Builder setTileSize(int tileSize) {
            this.tileSize = tileSize;
            return this;
        }

        public @ColorInt int getTileOddColor() {
            return tileOddColor;
        }

        public Builder setTileOddColor(@ColorInt int color) {
            this.tileOddColor = color;
            return this;
        }

        public @ColorInt int getTileEvenColor() {
            return tileEvenColor;
        }

        public Builder setTileEvenColor(@ColorInt int color) {
            this.tileEvenColor = color;
            return this;
        }

        public TileBackgroundDrawable build() {
            return new TileBackgroundDrawable(this);
        }
    }
}
