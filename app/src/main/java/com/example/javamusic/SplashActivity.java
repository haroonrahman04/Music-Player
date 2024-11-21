package com.example.javamusic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        new Handler().postDelayed(new Runnable(){
            @Override
            public void run() {


                SharedPreferences sharedPreferences = getSharedPreferences("UserPrefs", Context.MODE_PRIVATE);
                boolean isLoggedIn = sharedPreferences.getBoolean("isLoggedIn", false);

                Intent mainIntent;
                if(isLoggedIn) {
                    mainIntent = new Intent(SplashActivity.this, MainActivity.class);
                }else {
                    mainIntent=new Intent(SplashActivity.this,LoginPage.class);
                }
                startActivity(mainIntent);
                finish();
//                SplashActivity.this.startActivity(mainIntent);
//                SplashActivity.this.finish();
            }
        }, 3000);
    }
}
