package com.SpmKorea.spm_app;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Showinfo extends AppCompatActivity {
    Handler handler = new Handler();
    TextView texturl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showinfo);


        texturl = (TextView) findViewById(R.id.urltext);

        Intent receive_intent = getIntent();

        String temp1 = receive_intent.getExtras().getString("info1");   // receive data
        String temp2 = receive_intent.getExtras().getString("info2");
        String temp3 = receive_intent.getExtras().getString("info3");
        String temp4 = receive_intent.getExtras().getString("info4");
        String temp5 = receive_intent.getExtras().getString("info5");

        EditText edit_url = (EditText) findViewById(R.id.final_URL);
        edit_url.setText("https://"+temp1+"/"+temp2+"/"+temp3+"/"+temp4+"/"+temp5);

        Button btn_url = (Button) findViewById(R.id.enter);
        btn_url.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String urlStr = edit_url.getText().toString();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        request(urlStr);
                    }
                }).start();
            }
        });
    }

    public void request(String urlStr) {
        StringBuilder output = new StringBuilder();
        try {
            URL url = new URL(urlStr);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            if(connection != null) {
                connection.setConnectTimeout(10000);
                connection.setRequestMethod("GET");
                connection.setDoInput(true);

                int resCode = connection.getResponseCode();
                if(resCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    String line = null;
                    while (true) {
                        line = reader.readLine();
                        if (line == null) {
                            break;
                        }
                        output.append(line + "\n");
                    }
                    reader.close();
                    connection.disconnect();
                } else {
                    println("현재 요청 상태 : "+ resCode);
                }
            }
        } catch (IOException e) {
            println("예외 밠생함: " + e.toString());
        }
        println("응답 -> " +output.toString());
    }

    public void println(final String data) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                texturl.append(data+"\n");
            }
        });
    }
}