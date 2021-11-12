package com.example.enthusiasticblobuploads;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.util.Pair;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private View img_logo, img_bottom, logo_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Utils.blackIconStatusBar(MainActivity.this, R.color.light_backgroud);
        img_logo = findViewById(R.id.imgLogo);
        img_bottom = findViewById(R.id.imgBottom);
        logo_text = findViewById(R.id.txtLogo);

        Boolean isFirstTime;

        SharedPreferences app_preferences = PreferenceManager
                .getDefaultSharedPreferences(MainActivity.this);

        SharedPreferences.Editor editor = app_preferences.edit();

        isFirstTime = app_preferences.getBoolean("isFirstTime", true);

        if (isFirstTime) {

//implement your first time logic
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(MainActivity.this, BlobUploadActivity.class);
                    ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,
                            Pair.create(img_logo, "logo"),
                            Pair.create(img_bottom, "img_tree"),
                            Pair.create(logo_text, "logo_txt"));
                    startActivity(intent, options.toBundle());
                    finish();
                }
            }, 3000);

            editor.putBoolean("isFirstTime", false);
            editor.commit();

        } else {
//app open directly
            Intent intent = new Intent(MainActivity.this, BlobUploadActivity.class);
            startActivity(intent);

        }

    }
}