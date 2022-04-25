package com.example.spotapicheck;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.github.appintro.AppIntro;
import com.github.appintro.AppIntroFragment;
import com.github.appintro.AppIntroPageTransformerType;

import android.content.Intent;
import android.os.Bundle;

public class IntroActivity extends AppIntro {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addSlide(AppIntroFragment.createInstance("A World of Music!", "We all love grooving, don't we?", R.drawable.slide_1, R.color.black, R.color.spotify_green));
        addSlide(AppIntroFragment.createInstance("Connecting Everyone to Ease!", "Building APIs everyone can use", R.drawable.slide_2, R.color.spotify_green));

        showStatusBar(false);
        setTransformer(AppIntroPageTransformerType.Fade.INSTANCE);

        setScrollDurationFactor(2);

        setWizardMode(true);
        getSupportActionBar().hide();
    }

    @Override
    protected void onSkipPressed(@Nullable Fragment currentFragment) {
        super.onSkipPressed(currentFragment);

        getSharedPreferences("Intro", 0).edit().putBoolean("FirstTime", false).commit();
        Intent intent = new Intent(IntroActivity.this, SplashActivity.class);
        startActivity(intent);

        finish();
    }

    @Override
    protected void onDonePressed(@Nullable Fragment currentFragment) {
        super.onDonePressed(currentFragment);

        getSharedPreferences("Intro", 0).edit().putBoolean("FirstTime", false).commit();
        Intent intent = new Intent(IntroActivity.this, SplashActivity.class);
        startActivity(intent);

        finish();
    }
}