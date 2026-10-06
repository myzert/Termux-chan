package com.termux.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.termux.view.TerminalView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class TermuxAppearanceSettingsActivity extends Activity {
    
    private static final int PICK_IMAGE_REQUEST = 1;
    private static final int PICK_FONT_REQUEST = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(32, 32, 32, 32);
        
        TextView title = new TextView(this);
        title.setText("Termux-chan Appearance");
        title.setTextSize(24);
        layout.addView(title);
        
        // 1. Pick Custom Wallpaper
        Button pickWallpaperBtn = new Button(this);
        pickWallpaperBtn.setText("Ubah Wallpaper (Pick Image)");
        pickWallpaperBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
        layout.addView(pickWallpaperBtn);

        // 2. Disable Wallpaper
        Button disableWallpaperBtn = new Button(this);
        disableWallpaperBtn.setText("Matikan Wallpaper (Solid Color)");
        disableWallpaperBtn.setOnClickListener(v -> {
            android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
            prefs.edit().putString("custom_wallpaper_uri", "disabled").apply();
            TerminalView.sWallpaper = null;
            Toast.makeText(this, "Wallpaper dimatikan", Toast.LENGTH_SHORT).show();
        });
        layout.addView(disableWallpaperBtn);

        // 3. Reset to Anime Wallpaper
        Button resetAnimeBtn = new Button(this);
        resetAnimeBtn.setText("Kembalikan Wallpaper Anime Default");
        resetAnimeBtn.setOnClickListener(v -> {
            try {
                android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
                prefs.edit().remove("custom_wallpaper_uri").apply();
                InputStream is = getAssets().open("anime/default.png");
                TerminalView.sWallpaper = BitmapFactory.decodeStream(is);
                is.close();
                Toast.makeText(this, "Wallpaper Anime diaktifkan", Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                Toast.makeText(this, "Gagal meload Anime", Toast.LENGTH_SHORT).show();
            }
        });
        layout.addView(resetAnimeBtn);
        
        // 4. Opacity Slider
        TextView opacityLabel = new TextView(this);
        opacityLabel.setText("Transparansi / Opacity (0-255):");
        layout.addView(opacityLabel);
        
        SeekBar opacityBar = new SeekBar(this);
        opacityBar.setMax(255);
        android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
        TerminalView.sOpacity = prefs.getInt("terminal_opacity", 180);
        opacityBar.setProgress(TerminalView.sOpacity);
        
        opacityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TerminalView.sOpacity = progress;
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {
                prefs.edit().putInt("terminal_opacity", TerminalView.sOpacity).apply();
            }
        });
        layout.addView(opacityBar);
        
        // 5. Custom Font Picker
        Button pickFontBtn = new Button(this);
        pickFontBtn.setText("Ubah Font Terminal (.ttf / .otf)");
        pickFontBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            startActivityForResult(intent, PICK_FONT_REQUEST);
        });
        layout.addView(pickFontBtn);
        
        setContentView(layout);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            if (requestCode == PICK_IMAGE_REQUEST) {
                try {
                    android.content.SharedPreferences prefs = android.preference.PreferenceManager.getDefaultSharedPreferences(this);
                    prefs.edit().putString("custom_wallpaper_uri", uri.toString()).apply();
                    
                    InputStream is = getContentResolver().openInputStream(uri);
                    TerminalView.sWallpaper = BitmapFactory.decodeStream(is);
                    is.close();
                    Toast.makeText(this, "Wallpaper berhasil diubah!", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Gagal memuat gambar", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PICK_FONT_REQUEST) {
                try {
                    InputStream is = getContentResolver().openInputStream(uri);
                    File fontFile = com.termux.shared.termux.TermuxConstants.TERMUX_FONT_FILE;
                    if (fontFile.getParentFile() != null && !fontFile.getParentFile().exists()) {
                        fontFile.getParentFile().mkdirs();
                    }
                    FileOutputStream fos = new FileOutputStream(fontFile);
                    byte[] buffer = new byte[8192];
                    int len;
                    while ((len = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, len);
                    }
                    fos.close();
                    is.close();
                    
                    Toast.makeText(this, "Font berhasil diaplikasikan! Silakan restart aplikasi.", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Gagal memuat font", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
