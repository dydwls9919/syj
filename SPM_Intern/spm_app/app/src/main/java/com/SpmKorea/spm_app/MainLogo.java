package com.SpmKorea.spm_app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class MainLogo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        new Handler().postDelayed(new Runnable() {// 2 초 후에 실행
            @Override
            public void run() {
                // start after 2 seconds
                Intent intent = new Intent(MainLogo.this, Putinfo.class);
                startActivity(intent);
            }
        }, 2000);




    }
}