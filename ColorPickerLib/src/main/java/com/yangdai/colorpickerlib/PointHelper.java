package com.yangdai.colorpickerlib;

import android.graphics.Color;
import android.graphics.Point;

class PointHelper {
    private PointHelper() {
    }

    /**
     * 获取颜色点的坐标。
     *
     * @param colorPickerView 颜色选择器视图。
     * @param point           坐标点。
     * @return 颜色点的坐标。
     */
    protected static Point getColorPoint(ColorPickerView colorPickerView, Point point) {
        Point center = new Point(colorPickerView.getWidth() / 2, colorPickerView.getMeasuredHeight() / 2);
        if (colorPickerView.isHuePalette()) {
            return getHuePoint(colorPickerView, point);
        }
        return approximatedPoint(colorPickerView, point, center);
    }

    /**
     * 根据起始点和结束点逼近获取颜色点的坐标。
     *
     * @param colorPickerView 颜色选择器视图。
     * @param start           起始点。
     * @param end             结束点。
     * @return 颜色点的坐标。
     */
    private static Point approximatedPoint(ColorPickerView colorPickerView, Point start, Point end) {
        if (getDistance(start, end) <= 3) {
            return end;
        }
        Point center = getCenterPoint(start, end);
        int color = colorPickerView.getColorFromBitmap(center.x, center.y);
        if (color == Color.TRANSPARENT) {
            return approximatedPoint(colorPickerView, center, end);
        } else {
            return approximatedPoint(colorPickerView, start, center);
        }
    }

    /**
     * 根据色相选择器获取颜色点的坐标。
     *
     * @param colorPickerView 颜色选择器视图。
     * @param point           坐标点。
     * @return 颜色点的坐标。
     */
    private static Point getHuePoint(ColorPickerView colorPickerView, Point point) {
        float centerX = colorPickerView.getWidth() * 0.5f;
        float centerY = colorPickerView.getHeight() * 0.5f;
        float x = point.x - centerX;
        float y = point.y - centerY;
        float radius = Math.min(centerX, centerY);
        double r = Math.sqrt(x * x + y * y);
        if (r > radius) {
            x *= radius / r;
            y *= radius / r;
        }
        return new Point((int) (x + centerX), (int) (y + centerY));
    }

    /**
     * 获取起始点和结束点的中心点坐标。
     *
     * @param start 起始点。
     * @param end   结束点。
     * @return 中心点坐标。
     */
    private static Point getCenterPoint(Point start, Point end) {
        return new Point((end.x + start.x) / 2, (end.y + start.y) / 2);
    }

    /**
     * 计算起始点和结束点之间的距离。
     *
     * @param start 起始点。
     * @param end   结束点。
     * @return 距离。
     */
    private static int getDistance(Point start, Point end) {
        return (int) Math.hypot(end.x - start.x, end.y - start.y);
    }
}
