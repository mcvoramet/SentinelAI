# Android App Icon Package

This package contains all the necessary icon files for your Android application.

## Contents

### Launcher Icons (Standard)
Located in `res/mipmap-*/`:
- `ic_launcher.png` - Standard square launcher icon
- `ic_launcher_round.png` - Round launcher icon

| Density | Size | Folder |
|---------|------|--------|
| mdpi | 48×48 | mipmap-mdpi |
| hdpi | 72×72 | mipmap-hdpi |
| xhdpi | 96×96 | mipmap-xhdpi |
| xxhdpi | 144×144 | mipmap-xxhdpi |
| xxxhdpi | 192×192 | mipmap-xxxhdpi |

### Adaptive Icons (Android 8.0+)
Located in `res/mipmap-*/`:
- `ic_launcher_foreground.png` - Foreground layer with your icon

Located in `res/mipmap-anydpi-v26/`:
- `ic_launcher.xml` - Adaptive icon definition
- `ic_launcher_round.xml` - Round adaptive icon definition

Located in `res/values/`:
- `ic_launcher_background.xml` - Background color definition (#0D1B2A - dark navy blue)

### Play Store Icon
- `play_store_icon_512x512.png` - Transparent background (512×512)
- `play_store_icon_with_bg_512x512.png` - With dark blue gradient background (512×512)

## Installation Instructions

### Step 1: Copy Resource Files

Copy the entire `res/` folder contents to your Android project's `app/src/main/res/` directory:

```
your-project/
└── app/
    └── src/
        └── main/
            └── res/
                ├── mipmap-mdpi/
                │   ├── ic_launcher.png
                │   ├── ic_launcher_round.png
                │   └── ic_launcher_foreground.png
                ├── mipmap-hdpi/
                │   └── ...
                ├── mipmap-xhdpi/
                │   └── ...
                ├── mipmap-xxhdpi/
                │   └── ...
                ├── mipmap-xxxhdpi/
                │   └── ...
                ├── mipmap-anydpi-v26/
                │   ├── ic_launcher.xml
                │   └── ic_launcher_round.xml
                └── values/
                    └── ic_launcher_background.xml
```

### Step 2: Update AndroidManifest.xml

Ensure your `AndroidManifest.xml` references the icons correctly:

```xml
<application
    android:icon="@mipmap/ic_launcher"
    android:roundIcon="@mipmap/ic_launcher_round"
    ... >
```

### Step 3: Customize Background Color (Optional)

To change the adaptive icon background color, edit `res/values/ic_launcher_background.xml`:

```xml
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <color name="ic_launcher_background">#YOUR_COLOR_HERE</color>
</resources>
```

### Step 4: Play Store Submission

Use `play_store_icon_512x512.png` or `play_store_icon_with_bg_512x512.png` when uploading your app to the Google Play Store. The Play Store requires a 512×512 PNG icon.

## Notes

- **Adaptive Icons**: Android 8.0 (API 26) and above use adaptive icons, which allow the system to display icons in various shapes (circle, squircle, rounded square, etc.)
- **Safe Zone**: The foreground layer is designed with a 72dp safe zone centered in a 108dp canvas to ensure the icon looks good in all mask shapes
- **Backward Compatibility**: The standard `ic_launcher.png` files are included for devices running Android 7.1 and below

## Troubleshooting

If your icon appears cropped or incorrectly positioned:
1. Verify all files are in the correct folders
2. Clean and rebuild your project
3. Uninstall the app from your device/emulator before reinstalling

For any issues with the icon appearance, you may need to adjust the foreground positioning in the generation script.
