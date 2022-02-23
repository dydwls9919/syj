package com.spmkorea.spm_test;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.util.Linkify;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.text.DateFormat;
import java.util.Date;
import java.util.List;


public class Putinfo extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;

    SharedPreferences sharedPreferences;    // ip, dp 종료 후에도 저장

    String[] symptom_name = new String[100];   // name 저장
    double[] symptom_value = new double[100];  // 수치 저장
    String[] string_value = new String[100];  // 수치 저장(소수점 2째자리까지)
    String[] symptom_unit = new String[100];
    String[] symptom_color = new String[100];

    String[] value_name_mp = new String[100];   // measuring point 저장
    String[] value_name_ass = new String[100];   // assignment 저장

    private long backKeyPressedTime = 0;    // 뒤로가기 시 시행 time
    private Toast toast;
    private Toast to;


    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        sharedPreferences = getSharedPreferences("pref", MODE_PRIVATE);

        Handler mHandler = new Handler(Looper.getMainLooper());

        Button btn_input = (Button) findViewById(R.id.put_button);
        ImageButton btn_mp = (ImageButton) findViewById(R.id.button_mp);
        ImageButton btn_ass = (ImageButton) findViewById(R.id.button_ass);
        ImageButton btn_rv = (ImageButton) findViewById(R.id.button_rv);

        AutoCompleteTextView edt_info1 = (AutoCompleteTextView) findViewById(R.id.info1);   // IP
        AutoCompleteTextView edt_info2 = (AutoCompleteTextView) findViewById(R.id.info2);   // DB
        EditText edt_info3 = (EditText) findViewById(R.id.info3);   //M.P
        EditText edt_info4 = (EditText) findViewById(R.id.info4);   // Assign
        EditText edt_info5 = (EditText) findViewById(R.id.info5);   //R.V


        edt_info1.setText(sharedPreferences.getString("ip",""));
        edt_info2.setText(sharedPreferences.getString("db",""));

        Button history_btn = (Button) findViewById(R.id.button_history);
//--------------------------------------------------------------------------------------

        btn_mp.setOnClickListener(new View.OnClickListener() {
            CharSequence[] oItems = {"","","","","","","","","","",""}; // 초기값
            @Override
            public void onClick(View view) {

                String edtinf1 = edt_info1.getText().toString();
                String edtinf2 = edt_info2.getText().toString();
                String edtinf3 = edt_info3.getText().toString();
                String edtinf4 = edt_info4.getText().toString();

                String urladdress = "http://"+edtinf1+":7890/api/v1/databases/"+edtinf2+"/measuringpoints/"+edtinf3+"/assignments/"+edtinf4+"/results/latest?Expand=symptoms";
                String urladdressMP = "http://"+edtinf1+":7890/api/v1/databases/"+edtinf2+"/measuringpoints";
                String urladdressAss = "http://"+edtinf1+":7890/api/v1/databases/"+edtinf2+"/measuringpoints/"+edtinf3+"/assignments";
                new Thread(){
                @Override
                public void run() {
                    try {
                        // request url
                        URL urlMP = new URL(urladdressMP);

                        Authenticator.setDefault(new Authenticator(){   // auto login
                            @Override
                            protected PasswordAuthentication getPasswordAuthentication() {
                                System.err.println("Feeding username and password for " + getRequestingScheme());
                                return (new PasswordAuthentication("system", "".toCharArray()));    // put ID, PW(api auto login)
                            }});
                        InputStream is_mp = urlMP.openStream();  // open MeasuringPoint Url
                        InputStreamReader isr_mp = new InputStreamReader(is_mp);
                        BufferedReader reader_mp = new BufferedReader(isr_mp);

                        /* MeasuringPoint URL Reader */
                        StringBuffer buffer_mp = new StringBuffer();
                        String line_mp = reader_mp.readLine();    // 한 줄씩 read
                        while (line_mp != null) {
                            buffer_mp.append(line_mp + "\n");
                            line_mp = reader_mp.readLine();
                        }
                        String jsonData_mp = buffer_mp.toString();
                        JSONObject jsonObject_mp = new JSONObject(jsonData_mp);
                        String spm_value_mp = jsonObject_mp.getString("values");
                        JSONArray jsonArray_mp = new JSONArray(spm_value_mp);   // MeasuringPoint List Api

                            runOnUiThread(new Runnable() {
                            @Override

                            public void run() {
                                try {
                                    for (int i = 0; i < jsonArray_mp.length(); i++) {
                                        //  각 JSONObject 형태로 객체를 생성한다.
                                        JSONObject temp_mp = jsonArray_mp.getJSONObject(i);
                                        value_name_mp[i] = temp_mp.optString("Number");
                                        oItems[i] = value_name_mp[i];
                                        System.out.println(value_name_mp[i]);
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    }
                            }
                        });
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            // 사용하고자 하는 코드
                            AlertDialog.Builder oDialog = new AlertDialog.Builder(Putinfo.this,
                                    android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);
                                    oDialog.setTitle("MeasuringPoint")
                                    .setItems(oItems, new DialogInterface.OnClickListener()
                                    {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which)
                                        {
                                            edt_info3.setText(oItems[which]);
                                        }
                                    })
                                    .setCancelable(true)
                                    .show();
                        }
                    }, 0);

                }
                }.start();
                for(int i=0;i<oItems.length;i++) {
                    if(oItems[i] == null || oItems[i].toString().isEmpty()) {
                        oItems[i] = "";
                    }
                }
            //    출처: https://mixup.tistory.com/36 [투믹스 작업장]
            }
        });

        btn_ass.setOnClickListener(new View.OnClickListener() {
            CharSequence[] oItems = new CharSequence[100];
            @Override
            public void onClick(View view) {

                String edtinf1 = edt_info1.getText().toString();
                String edtinf2 = edt_info2.getText().toString();
                String edtinf3 = edt_info3.getText().toString();
                String edtinf4 = edt_info4.getText().toString();
                String edtinf5 = edt_info5.getText().toString();

                String urladdress = "http://"+edtinf1+":7890/api/v1/databases/"+edtinf2+"/measuringpoints/"+edtinf3+"/assignments/"+edtinf4+"/results/latest?Expand=symptoms";
                String urladdressMP = "http://"+edtinf1+":7890/api/v1/databases/"+edtinf2+"/measuringpoints";
                String urladdressAss = "http://"+edtinf1+":7890/api/v1/databases/"+edtinf2+"/measuringpoints/"+edtinf3+"/assignments";
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            // request url
                            URL urlMP = new URL(urladdressMP);
                            URL urlAss = new URL(urladdressAss);

                            Authenticator.setDefault(new Authenticator(){   // auto login
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    System.err.println("Feeding username and password for " + getRequestingScheme());
                                    return (new PasswordAuthentication("system", "".toCharArray()));
                                }});

                            InputStream is_mp = urlMP.openStream();  // open MeasuringPoint Url
                            InputStreamReader isr_mp = new InputStreamReader(is_mp);
                            BufferedReader reader_mp = new BufferedReader(isr_mp);

                            InputStream is_ass = urlAss.openStream();  // open Assignment Url
                            InputStreamReader isr_ass = new InputStreamReader(is_ass);
                            BufferedReader reader_ass = new BufferedReader(isr_ass);

                            /* MeasuringPoint URL Reader */
                            StringBuffer buffer_mp = new StringBuffer();
                            String line_mp = reader_mp.readLine();    // 한 줄씩 read
                            while (line_mp != null) {
                                buffer_mp.append(line_mp + "\n");
                                line_mp = reader_mp.readLine();
                            }
                            String jsonData_mp = buffer_mp.toString();
                            JSONObject jsonObject_mp = new JSONObject(jsonData_mp);
                            String spm_value_mp = jsonObject_mp.getString("values");
                            //System.out.println(spm_value_mp);
                            JSONArray jsonArray_mp = new JSONArray(spm_value_mp);   // MeasuringPoint List Api

                            /* Assignment URL Reader*/

                            StringBuffer buffer_ass = new StringBuffer();
                            String line_ass = reader_ass.readLine();    // 한 줄씩 read
                            while (line_ass != null) {
                                buffer_ass.append(line_ass + "\n");
                                line_ass = reader_ass.readLine();
                            }
                            String jsonData_ass = buffer_ass.toString();
                            JSONObject jsonObject_ass = new JSONObject(jsonData_ass);
                            String spm_value_ass = jsonObject_ass.getString("values");
                            JSONArray jsonArray_ass = new JSONArray(spm_value_ass); // Assignment List Api

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        for (int i = 0; i < jsonArray_mp.length(); i++) {
                                            //  각 JSONObject 형태로 객체를 생성한다.
                                            JSONObject temp_mp = jsonArray_mp.getJSONObject(i);
                                            value_name_mp[i] = temp_mp.optString("Number");
                                            System.out.println(value_name_mp[i]);
                                        }
                                        for (int i = 0; i < jsonArray_ass.length(); i++) {
                                            //  각 JSONObject 형태로 객체를 생성한다.
                                            JSONObject temp_ass = jsonArray_ass.getJSONObject(i);
                                            value_name_ass[i] = temp_ass.optString("TechName");
                                            System.out.println(value_name_ass[i]);
                                            oItems[i] = value_name_ass[i];
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                        }
                                }
                            });
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 사용하고자 하는 코드
                                AlertDialog.Builder oDialog = new AlertDialog.Builder(Putinfo.this,
                                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                                oDialog.setTitle("Assignment")
                                        .setItems(oItems, new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                edt_info4.setText(oItems[which]);
                                            }
                                        })
                                        .setCancelable(true)
                                        .show();
                            }
                        }, 0);

                    }
                }.start();
                for(int i=0;i<oItems.length;i++) {
                    if(oItems[i] == null || oItems[i].toString().isEmpty()) {
                        oItems[i] = "";
                    }
                }
                //    출처: https://mixup.tistory.com/36 [투믹스 작업장]
            }
        });

        btn_rv.setOnClickListener(new View.OnClickListener() {
            CharSequence[] oItems = new CharSequence[100];
            @Override
            public void onClick(View view) {

                String edtinf1 = edt_info1.getText().toString();
                String edtinf2 = edt_info2.getText().toString();
                String edtinf3 = edt_info3.getText().toString();
                String edtinf4 = edt_info4.getText().toString();
                String edtinf5 = edt_info5.getText().toString();

                String urladdress = "http://"+edtinf1+":7890/api/v1/databases/"+edtinf2+"/measuringpoints/"+edtinf3+"/assignments/"+edtinf4+"/results/latest?Expand=symptoms";

                String urladdressMP = "http://"+edtinf1+":7890/api/v1/databases/"+edtinf2+"/measuringpoints";

                String urladdressAss = "http://"+edtinf1+":7890/api/v1/databases/"+edtinf2+"/measuringpoints/"+edtinf3+"/assignments";

                System.out.println(urladdressMP);
                new Thread(){
                    @Override
                    public void run() {
                        try {
                            // request url

                            URL url = new URL(urladdress);
                            URL urlMP = new URL(urladdressMP);
                            URL urlAss = new URL(urladdressAss);

                            Authenticator.setDefault(new Authenticator(){   // auto login
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    System.err.println("Feeding username and password for " + getRequestingScheme());
                                    return (new PasswordAuthentication("system", "".toCharArray()));
                                }});

                            InputStream is = url.openStream();  // open Full Url
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader reader = new BufferedReader(isr);

                            InputStream is_mp = urlMP.openStream();  // open MeasuringPoint Url
                            InputStreamReader isr_mp = new InputStreamReader(is_mp);
                            BufferedReader reader_mp = new BufferedReader(isr_mp);

                            InputStream is_ass = urlAss.openStream();  // open Assignment Url
                            InputStreamReader isr_ass = new InputStreamReader(is_ass);
                            BufferedReader reader_ass = new BufferedReader(isr_ass);

                            /*Full URL Reader */
                            StringBuffer buffer = new StringBuffer();
                            String line = reader.readLine();    // 한 줄씩 read

                            while (line != null) {
                                buffer.append(line + "\n");
                                line = reader.readLine();
                            }

                            String jsonData = buffer.toString();
                            JSONObject jsonObject = new JSONObject(jsonData);
                            String spm_Symptoms = jsonObject.getString("Symptoms");
                            //System.out.println(spm_Symptoms);
                            JSONArray jsonArray = new JSONArray(spm_Symptoms);  // Full RequestUrl

                            /* MeasuringPoint URL Reader */
                            StringBuffer buffer_mp = new StringBuffer();
                            String line_mp = reader_mp.readLine();    // 한 줄씩 read
                            while (line_mp != null) {
                                buffer_mp.append(line_mp + "\n");
                                line_mp = reader_mp.readLine();
                            }

                            String jsonData_mp = buffer_mp.toString();
                            JSONObject jsonObject_mp = new JSONObject(jsonData_mp);
                            String spm_value_mp = jsonObject_mp.getString("values");
                            JSONArray jsonArray_mp = new JSONArray(spm_value_mp);   // MeasuringPoint List Api

                            /* Assignment URL Reader*/

                            StringBuffer buffer_ass = new StringBuffer();
                            String line_ass = reader_ass.readLine();    // 한 줄씩 read
                            while (line_ass != null) {
                                buffer_ass.append(line_ass + "\n");
                                line_ass = reader_ass.readLine();
                            }

                            String jsonData_ass = buffer_ass.toString();
                            JSONObject jsonObject_ass = new JSONObject(jsonData_ass);
                            String spm_value_ass = jsonObject_ass.getString("values");
                            JSONArray jsonArray_ass = new JSONArray(spm_value_ass); // Assignment List Api

                            runOnUiThread(new Runnable() {
                                @Override

                                public void run() {
                                    try {
                                        // jsonArray의 length만큼 for문 반복
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        //  각 JSONObject 형태로 객체를 생성한다.
                                        JSONObject temp = jsonArray.getJSONObject(i);
                                        symptom_name[i] = temp.optString("Name");   // 찾는 req_value의 Name값이 있는지 확인
                                        symptom_value[i] = temp.optDouble("Value");  // 찾는 req_value 값의 수치
                                        symptom_unit[i] = temp.optString("UnitName"); // 찾는 req_value 값의 단위
                                        string_value[i] = String.format("%.2f", symptom_value[i]); // 2째자리 까지 표현
                                        oItems[i] = symptom_name[i];
                                    }
                                    for (int i = 0; i < jsonArray_mp.length(); i++) {
                                        //  각 JSONObject 형태로 객체를 생성한다.
                                        JSONObject temp_mp = jsonArray_mp.getJSONObject(i);
                                        value_name_mp[i] = temp_mp.optString("Number");
                                        System.out.println(value_name_mp[i]);
                                    }
                                    for (int i = 0; i < jsonArray_ass.length(); i++) {
                                        //  각 JSONObject 형태로 객체를 생성한다.
                                        JSONObject temp_ass = jsonArray_ass.getJSONObject(i);
                                        value_name_ass[i] = temp_ass.optString("TechName");
                                        System.out.println(value_name_ass[i]);
                                    }

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // 사용하고자 하는 코드
                                AlertDialog.Builder oDialog = new AlertDialog.Builder(Putinfo.this,
                                        android.R.style.Theme_DeviceDefault_Light_Dialog_Alert);

                                oDialog.setTitle("Request Value")
                                        .setItems(oItems, new DialogInterface.OnClickListener()
                                        {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which)
                                            {
                                                edt_info5.setText(oItems[which]);
                                            }
                                        })
                                        .setCancelable(true)
                                        .show();
                            }
                        }, 0);

                    }
                }.start();
                for(int i=0;i<oItems.length;i++) {
                    if(oItems[i] == null || oItems[i].toString().isEmpty()) {
                        oItems[i] = "";
                    }
                }

                System.out.println(oItems);
                //    출처: https://mixup.tistory.com/36 [투믹스 작업장]
            }
        });
        //--------------------------------------
        ListView listview ;
        ListAdapter adapter;
        adapter = new ListAdapter();

        listview = (ListView) findViewById(R.id.listView1);
        listview.setAdapter(adapter);

        btn_input.setOnClickListener(new View.OnClickListener() {
            String currentDateTimeString = DateFormat.getDateInstance().format(new Date());
            int cnt = 1;
            @RequiresApi(api = Build.VERSION_CODES.GINGERBREAD)
            @SuppressLint("Range")
            @Override
            public void onClick(View view) {
                ImageView val_image = (ImageView) findViewById(R.id.value_image);   // value image(red,yellow,green)
                String edtinf1 = edt_info1.getText().toString();
                String edtinf2 = edt_info2.getText().toString();
                String edtinf3 = edt_info3.getText().toString();
                String edtinf4 = edt_info4.getText().toString();
                String edtinf5 = edt_info5.getText().toString();

                if(edtinf1.isEmpty()) {
                    Toast.makeText(Putinfo.this, "Put IP", Toast.LENGTH_SHORT).show();
                }
                if(edtinf2.isEmpty()) {
                    Toast.makeText(Putinfo.this, "Put DB", Toast.LENGTH_SHORT).show();
                }
                if(edtinf3.isEmpty()) {
                    Toast.makeText(Putinfo.this, "Put MeasuringPoints", Toast.LENGTH_SHORT).show();
                }
                if(edtinf4.isEmpty()) {
                    Toast.makeText(Putinfo.this, "Put Assignment", Toast.LENGTH_SHORT).show();
                }
                if(edtinf5.isEmpty()) {
                    Toast.makeText(Putinfo.this, "Put Request Value", Toast.LENGTH_SHORT).show();   // warning Toast
                }

                // request url
                String urladdress = "http://"+edtinf1+":7890/api/v1/databases/"+edtinf2+"/measuringpoints/"+edtinf3+"/assignments/"+edtinf4+"/results/latest?Expand=symptoms";
                String urladdressMP = "http://"+edtinf1+":7890/api/v1/databases/"+edtinf2+"/measuringpoints";
                String urladdressAss = "http://"+edtinf1+":7890/api/v1/databases/spmkorea/measuringpoints/"+edtinf3+"/assignments";

                new Thread(){
                    @Override
                    public void run() {
                        try {
                            URL url = new URL(urladdress);
                            URL urlMP = new URL(urladdressMP);
                            URL urlAss = new URL(urladdressAss);

                            Authenticator.setDefault(new Authenticator(){   // auto login
                                @Override
                                protected PasswordAuthentication getPasswordAuthentication() {
                                    System.err.println("Feeding username and password for " + getRequestingScheme());
                                    return (new PasswordAuthentication("system", "".toCharArray()));
                                }});

                            InputStream is = url.openStream();  // open Full Url
                            InputStreamReader isr = new InputStreamReader(is);
                            BufferedReader reader = new BufferedReader(isr);

                            InputStream is_mp = urlMP.openStream();  // open MeasuringPoint Url
                            InputStreamReader isr_mp = new InputStreamReader(is_mp);
                            BufferedReader reader_mp = new BufferedReader(isr_mp);

                            InputStream is_ass = urlAss.openStream();  // open Assignment Url
                            InputStreamReader isr_ass = new InputStreamReader(is_ass);
                            BufferedReader reader_ass = new BufferedReader(isr_ass);

                            /*Full URL Reader */
                            StringBuffer buffer = new StringBuffer();
                            String line = reader.readLine();    // 한 줄씩 read

                            while (line != null) {
                                buffer.append(line + "\n");
                                line = reader.readLine();
                            }
                            String jsonData = buffer.toString();
                            JSONObject jsonObject = new JSONObject(jsonData);
                            String spm_Symptoms = jsonObject.getString("Symptoms");
                            //System.out.println(spm_Symptoms);
                            JSONArray jsonArray = new JSONArray(spm_Symptoms);  // Full RequestUrl

                            /* MeasuringPoint URL Reader */
                            StringBuffer buffer_mp = new StringBuffer();
                            String line_mp = reader_mp.readLine();    // 한 줄씩 read

                            while (line_mp != null) {
                                buffer_mp.append(line_mp + "\n");
                                line_mp = reader_mp.readLine();
                            }

                            String jsonData_mp = buffer_mp.toString();
                            JSONObject jsonObject_mp = new JSONObject(jsonData_mp);
                            String spm_value_mp = jsonObject_mp.getString("values");
                            //System.out.println(spm_value_mp);
                            JSONArray jsonArray_mp = new JSONArray(spm_value_mp);   // MeasuringPoint List Api

                            /* Assignment URL Reader*/
                            StringBuffer buffer_ass = new StringBuffer();
                            String line_ass = reader_ass.readLine();    // 한 줄씩 read

                            while (line_ass != null) {
                                buffer_ass.append(line_ass + "\n");
                                line_ass = reader_ass.readLine();
                            }

                            String jsonData_ass = buffer_ass.toString();
                            JSONObject jsonObject_ass = new JSONObject(jsonData_ass);
                            String spm_value_ass = jsonObject_ass.getString("values");
                            //System.out.println(spm_value_ass);
                            JSONArray jsonArray_ass = new JSONArray(spm_value_ass); // Assignment List Api

                            TextView textView = (TextView) findViewById(R.id.req_name);
                            TextView text_count = (TextView) findViewById(R.id.req_value);

                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        // jsonArray의 length만큼 for문 반복
                                        for (int i = 0; i < jsonArray.length(); i++) {
                                            //  각 JSONObject 형태로 객체를 생성한다.
                                            JSONObject temp = jsonArray.getJSONObject(i);
                                            symptom_name[i] = temp.optString("Name");   // 찾는 req_value의 Name값이 있는지 확인
                                            symptom_value[i] = temp.optDouble("Value");  // 찾는 req_value 값의 수치
                                            symptom_unit[i] = temp.optString("UnitName"); // 찾는 req_value 값의 단위
                                            string_value[i] = String.format("%.2f", symptom_value[i]); // 2째자리 까지 표현

                                            if(symptom_name[i].equals(edtinf5)) {
                                                textView.setText(symptom_name[i]);   // 찾는 값이 있으면 setText
                                                text_count.setText(string_value[i] +symptom_unit[i]);  // 찾는 값의 수치값 setText
                                                cnt = 1;    // cnt =1 일때 토스트 발생X, 0일때 토스트 발생

                                                if(!(symptom_name[i].equals("HDm"))&&!(symptom_name[i].equals("HDc"))&&!(symptom_name[i].equals("Vel, Rms"))&&!(symptom_name[i].equals("HDrp"))){
                                                    val_image.setColorFilter(Color.parseColor("#FFFFFF"));  // HDm, HDc, Rms, HDrp 아닐 시 색 x
                                                    symptom_color[i] = " ";
                                                }

                                                if(symptom_name[i].equals("HDm")) {
                                                    if(symptom_value[i] >= 36) {
                                                        val_image.setColorFilter(Color.parseColor("#FF0000")); // HDm 36이상 red
                                                        symptom_color[i] = "Red";
                                                    }

                                                    else if(symptom_value[i] > 20 && symptom_value[i] < 36) {
                                                        val_image.setColorFilter(Color.parseColor("#FFFF00")); // HDm 20> 36<이상 yellow
                                                        symptom_color[i] = "Yellow";
                                                    }

                                                    else if(symptom_value[i] <=20) {
                                                        val_image.setColorFilter(Color.parseColor("#008000")); // HDm 20이하 green
                                                        symptom_color[i] = "Green";
                                                    }
                                                }

                                                if(symptom_name[i].equals("HDc")) {
                                                    if(symptom_value[i] >= 20) {
                                                        val_image.setColorFilter(Color.parseColor("#FFFF00")); // HDc 20이상 yellow
                                                        symptom_color[i] = "Yellow";
                                                    }

                                                    else if(symptom_value[i] <=19) {
                                                        val_image.setColorFilter(Color.parseColor("#008000")); // HDc 19이하 green
                                                        symptom_color[i] = "Green";
                                                    }
                                                }

                                                if(symptom_name[i].equals("Vel, Rms")) {
                                                    if(symptom_value[i] >= 8) {
                                                        val_image.setColorFilter(Color.parseColor("#FF0000")); // Vel, Rms 36이상 red
                                                        symptom_color[i] = "Red";
                                                    }
                                                    else if(symptom_value[i] > 3 && symptom_value[i] < 8) {
                                                        val_image.setColorFilter(Color.parseColor("#FFFF00")); // Vel, Rms 3> 8<이상 yellow
                                                        symptom_color[i] = "Yellow";
                                                    }
                                                    else if(symptom_value[i] <=3) {
                                                        val_image.setColorFilter(Color.parseColor("#008000")); // Vel, Rms 3이하 green
                                                        symptom_color[i] = "Green";
                                                    }
                                                }

                                                if(symptom_name[i].equals("HDrp")) {
                                                    if(symptom_value[i] > 30) {
                                                        val_image.setColorFilter(Color.parseColor("#FF0000")); // HDrp 30초과 red
                                                        symptom_color[i] = "Red";
                                                    }
                                                    else if(symptom_value[i] >= 20 && symptom_value[i] <= 30) {
                                                        val_image.setColorFilter(Color.parseColor("#FFFF00")); // HDrp 20> 30<이상 yellow
                                                        symptom_color[i] = "Yellow";
                                                    }
                                                    else if(symptom_value[i] <20) {
                                                        val_image.setColorFilter(Color.parseColor("#008000")); // HDrp 20이하 green
                                                        symptom_color[i] = "Green";
                                                    }
                                                }
                                                adapter.addItem(currentDateTimeString, edtinf3, symptom_name[i], string_value[i]+symptom_unit[i], symptom_color[i],val_image);
                                                adapter.notifyDataSetChanged(); // 변경되었음을 어답터에 알려준다//
                                                helper = new DBHelper(Putinfo.this);
                                                db = helper.getWritableDatabase();
                                                db.execSQL("INSERT INTO todolists VALUES (null, '" + currentDateTimeString + "', '" + edtinf3 + "', '" + symptom_name[i] + "','" + string_value[i] + "','" + symptom_color[i] + "');");
                                                break;
                                            }
                                            else{
                                                cnt = 0;    // 찾는 값이 없을 때 cnt = 0
                                            }
                                        }
                                        for (int i = 0; i < jsonArray_mp.length(); i++) {
                                            //  각 JSONObject 형태로 객체를 생성한다.
                                            JSONObject temp_mp = jsonArray_mp.getJSONObject(i);
                                            value_name_mp[i] = temp_mp.optString("Number");
                                        }
                                        for (int i = 0; i < jsonArray_ass.length(); i++) {
                                            //  각 JSONObject 형태로 객체를 생성한다.
                                            JSONObject temp_ass = jsonArray_ass.getJSONObject(i);
                                            value_name_ass[i] = temp_ass.optString("TechName");
                                        }

                                        if(cnt == 0) {
                                            Toast.makeText(Putinfo.this, "데어터가 없습니다", Toast.LENGTH_SHORT).show();// 토스트발생
                                            textView.setText("NAME");
                                            text_count.setText("VALUE");
                                            val_image.setColorFilter(Color.parseColor("#FFFFFF"));
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                        } catch (MalformedURLException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("ip", edtinf1);
                editor.putString("db", edtinf2);
                editor.commit();
            }

        });

        history_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Putinfo.this, Calendar.class);
                startActivity(intent);
            }
        });


        TextView txtweb = (TextView) findViewById(R.id.text_web);   // spm web주소
        Linkify.addLinks(txtweb, Linkify.WEB_URLS); // 클릭시 spe web 연결결
    }

    public void onBackPressed() {
        //super.onBackPressed();
        // 기존 뒤로 가기 버튼의 기능을 막기 위해 주석 처리 또는 삭제
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간에 2.5초를 더해 현재 시간과 비교 후
        // 마지막으로 뒤로 가기 버튼을 눌렀던 시간이 2.5초가 지났으면 Toast 출력
        // 2500 milliseconds = 2.5 seconds
        if (System.currentTimeMillis() > backKeyPressedTime + 2500) {
            backKeyPressedTime = System.currentTimeMillis();
            toast = Toast.makeText(Putinfo.this, "뒤로 가기 버튼을 한 번 더 누르시면 종료됩니다.", Toast.LENGTH_LONG);
            toast.show();
            return;
        }
        if (System.currentTimeMillis() <= backKeyPressedTime + 2500) {
            finish();
            toast.cancel();
        }
    }
}
