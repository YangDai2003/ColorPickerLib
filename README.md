# ColorPickerLib

ColorPickerLib 实现了多种颜色选择功能, 包括自定义颜色选择对话框(ColorPickerDialog)、自定义颜色选择视图(ColorPickerView)。

颜色选择的对象可以使用标准颜色调色板(ColorPalette)，也可以传入自定义Bitmap或Drawable。

ColorPickerView可以高度自定义样式，比如准心的大小、资源，颜色选择器的回调接口，颜色获取的更新频率等。

ColorPickerDialog继承了AlertDialog并添加了ColorPickerView，实现了具有颜色选择功能的对话窗。

------------

ColorPickerLib implements a variety of color selection functions, including a custom color selection dialog box (ColorPickerDialog) and a custom color selection view (ColorPickerView).

The color selection can use the standard color palette (ColorPalette), or pass in a custom Bitmap or Drawable.

ColorPickerView can highly customize the style, such as the size of the crosshair, resources, the callback interface of the color picker, the update frequency of color acquisition, etc.

ColorPickerDialog extends AlertDialog and adds ColorPickerView to implement a dialog window with color selection function.

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

ColorPickerDialog可以用传统方式创建，也可以用Builder进行链式创建。

ColorPickerDialog can be created in the traditional way, or it can be created in a chain using Builder.

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