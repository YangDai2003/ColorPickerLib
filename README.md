# ColorPickerLib

ColorPickerLib 实现了多种颜色选择功能, 包括自定义颜色选择对话框(ColorPickerDialog)、自定义颜色选择视图(ColorPickerView)。
可以使用标准颜色调色板(ColorPalette)选择颜色，也可以传入自定义Bitmap或Drawable。
可以高度自定义样式，比如准心的大小、资源，颜色选择器的回调接口，颜色获取的更新频率等。

[![jitpack](https://jitpack.io/v/YangDai2003/ColorPickerLib.svg)](https://jitpack.io/#YangDai2003/ColorPickerLib)

## How to import?

### Step 1. Add the JitPack repository to your build file

Gradle

Add it in your root build.gradle at the end of repositories:

```code
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Maven

```code
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

### Step 2. Add the dependency

Gradle

```code
dependencies {
    implementation 'com.github.YangDai2003:ColorPickerLib:latest_version'
}
```

Maven

```code
<dependency>
    <groupId>com.github.YangDai2003</groupId>
    <artifactId>ColorPickerLib</artifactId>
    <version>latest_version</version>
</dependency>
```

## How to use?

JAVA

```code
new ColorPickerDialog.Builder(this)
    .setIcon(android.R.drawable.sym_def_app_icon)
    .setTitle("选择颜色")
    .setPositiveButton("确定", (ColorListener) (colorInfo, fromUser) -> {
        textView.setText(colorInfo.getHexCode());
        textView.setTextColor(colorInfo.getColor());
    })
    .show();

or
    
ColorPickerDialog colorPickerDialog = new ColorPickerDialog(this);
colorPickerDialog.show();

ColorPickerView colorPickerView = new ColorPickerView(this);
colorPickerView.setImageResource(R.drawable.ic_launcher_foreground);
colorPickerView.setOnColorSelectedListener(new ColorListener() {
    @Override
    public void onColorSelected(ColorInfo colorInfo, boolean fromUser) {

    }
});
```