package com.termux.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.termux.view.TerminalView;
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
        title.setText("Termux chan Appearance");
        title.setTextSize(24);
        layout.addView(title);
        
        Button pickWallpaperBtn = new Button(this);
        pickWallpaperBtn.setText("Pick Custom Wallpaper");
        pickWallpaperBtn.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
        layout.addView(pickWallpaperBtn);
        
        TextView opacityLabel = new TextView(this);
        opacityLabel.setText("Background Opacity (0-255):");
        layout.addView(opacityLabel);
        
        SeekBar opacityBar = new SeekBar(this);
        opacityBar.setMax(255);
        opacityBar.setProgress(TerminalView.sOpacity);
        opacityBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                TerminalView.sOpacity = progress;
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        layout.addView(opacityBar);
        
        Button pickFontBtn = new Button(this);
        pickFontBtn.setText("Pick Custom Font (.ttf)");
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
                    InputStream is = getContentResolver().openInputStream(uri);
                    TerminalView.sWallpaper = BitmapFactory.decodeStream(is);
                    is.close();
                    Toast.makeText(this, "Wallpaper applied", Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
                }
            } else if (requestCode == PICK_FONT_REQUEST) {
                Toast.makeText(this, "Font selection requires saving to file, stubbed for now", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
