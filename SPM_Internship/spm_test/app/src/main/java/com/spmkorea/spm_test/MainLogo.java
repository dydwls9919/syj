package com.spmkorea.spm_test;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

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
                startActivity(intent);  // 2초뒤 인텐트 전환
                finish();
            }
        }, 2000);
    }
}