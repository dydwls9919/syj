package com.SpmKorea.spm_app;

import android.content.Intent;
import android.os.Bundle;
import android.text.util.Linkify;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Putinfo extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        Button btn_input = (Button) findViewById(R.id.put_button);

        btn_input.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditText edt_info1 = (EditText) findViewById(R.id.info1);
                EditText edt_info2 = (EditText) findViewById(R.id.info2);
                EditText edt_info3 = (EditText) findViewById(R.id.info3);
                EditText edt_info4 = (EditText) findViewById(R.id.info4);
                EditText edt_info5 = (EditText) findViewById(R.id.info5);

                String edtinf1 = edt_info1.getText().toString();
                String edtinf2 = edt_info2.getText().toString();
                String edtinf3 = edt_info3.getText().toString();
                String edtinf4 = edt_info4.getText().toString();
                String edtinf5 = edt_info5.getText().toString();

                Intent intent = new Intent(Putinfo.this, Showinfo.class); // send data to find
                intent.putExtra("info1", edtinf1);
                intent.putExtra("info2", edtinf2);
                intent.putExtra("info3", edtinf3);
                intent.putExtra("info4", edtinf4);
                intent.putExtra("info5", edtinf5);
                startActivity(intent);
            }
        });

        TextView txtweb = (TextView) findViewById(R.id.text_web);
        Linkify.addLinks(txtweb, Linkify.WEB_URLS);
    }
}
