package com.redcoresoft.myinstagramclonejavaapp.Activitys;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.redcoresoft.myinstagramclonejavaapp.R;

public class ForSplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_for_splash_screen);

        //getSupportActionBar().hide();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(ForSplashScreen.this,MainActivity.class));
                finish();
            }
        },2500);


    }
}