package com.example.projectwecare;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity
{
    TextView we, health;
    private static int Splash_timeout=8000;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_main);
        we = findViewById (R.id.textView1);
        health = findViewById (R.id.textView2);
        new Handler ( ).postDelayed (new Runnable () {
            @Override
            public void run() {
                Intent splashintent = new Intent (MainActivity.this, Home_Activity.class);
                startActivity (splashintent);
                finish();
            }
        },Splash_timeout);
        Animation myanimation1 = AnimationUtils.loadAnimation (MainActivity.this, R.anim.animation1);
        we.startAnimation (myanimation1);
        Animation myanimation2 = AnimationUtils.loadAnimation (MainActivity.this, R.anim.animation2);
        health.startAnimation (myanimation2);

    }
}