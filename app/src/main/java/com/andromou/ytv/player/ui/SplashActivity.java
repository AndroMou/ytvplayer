package com.andromou.ytv.player.ui;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.andromou.ytv.player.R;
import com.andromou.ytv.player.database.SavedLinksSQLite;


public class SplashActivity extends AppCompatActivity {
    private com.andromou.ytv.player.database.SavedLinksSQLite SavedLinksSQLite;
    Intent intent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_splash);

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            intent = new Intent(getApplicationContext(), SavedLinksActivity.class);
            startActivity(intent);
            finish();
        }, 1500);
    }
}