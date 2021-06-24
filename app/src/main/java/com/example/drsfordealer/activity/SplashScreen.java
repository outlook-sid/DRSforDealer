package com.example.drsfordealer.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.drsfordealer.R;

public class SplashScreen extends AppCompatActivity {

    private ImageView logoImage;
    private LinearLayout startupText_1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        this.getWindow().setStatusBarColor(ContextCompat.getColor(this,R.color.color_PureWhite));
        //getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        logoImage = findViewById(R.id.logo_image);
        startupText_1 = findViewById(R.id.start_screen_text_1);
        TextView startupText_2 = findViewById(R.id.start_screen_text_2);

        Animation startAnimation2 = AnimationUtils.loadAnimation(this, R.anim.startup_middle_animation);
        Animation startAnimation3 = AnimationUtils.loadAnimation(this, R.anim.startup_bottom_annimation);

        logoImage.setAnimation(startAnimation2);
        startupText_1.setAnimation(startAnimation2);
        startupText_2.setAnimation(startAnimation3);

        new Handler().postDelayed(() -> {
            Intent intent = new Intent(SplashScreen.this, LoginPage.class);
            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SplashScreen.this,
                    Pair.create(logoImage, "transition_Image"),
                    Pair.create(startupText_1, "transition_Text"));
            startActivity(intent, options.toBundle());
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            finishAffinity();
        },2500);

    }
}