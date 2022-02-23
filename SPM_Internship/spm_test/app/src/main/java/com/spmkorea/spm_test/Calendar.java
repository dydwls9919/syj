package com.spmkorea.spm_test;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Date;

public class Calendar extends AppCompatActivity {
    DBHelper helper;
    SQLiteDatabase db;

    SimpleCursorAdapter adapter;
    Cursor cursor;
    Cursor cursor2;
    ListView listView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        String currentDateTimeString = DateFormat.getDateInstance().format(new Date());

        Button btn_search = (Button) findViewById(R.id.button_view);
        Button btn_delete = (Button) findViewById(R.id.button_delete);

        helper = new DBHelper(this);
        db = helper.getWritableDatabase();
        cursor = db.rawQuery("SELECT * FROM todolists order by _id desc", null);

        String[] from = {"date", "mp", "name", "value", "color"};
        int[] to = {R.id.calendar_date, R.id.calendar_mp, R.id.calendar_name,R.id.calendar_value, R.id.calendar_color};

        startManagingCursor(cursor);

        adapter = new SimpleCursorAdapter(this, R.layout.calendaritem_list, cursor, from, to);
        listView = (ListView) findViewById(R.id.list_cal);
        listView.setAdapter(adapter);

        TextView cal_text = (TextView) findViewById(R.id.calendar_date);
        CalendarView calendarView = (CalendarView) findViewById(R.id.calendar) ;
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {

                btn_search.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onResume();
                        String date = String.valueOf(i)+". "+String.valueOf(i1+1)+". "+String.valueOf(i2)+". ";
                        System.out.println(date);
                        System.out.println(currentDateTimeString);
                        helper = new DBHelper(Calendar.this);
                        db = helper.getWritableDatabase();

                       // cursor2 = db.rawQuery("SELECT * FROM todolists WHERE date = "+"'"+currentDateTimeString+"'"+"order by _id desc", null);
                        cursor2= db.rawQuery("SELECT * FROM todolists WHERE date = ? order by _id desc", new String[] {currentDateTimeString});

                        String[] from = {"date", "mp", "name", "value", "color"};
                        int[] to = {R.id.calendar_date, R.id.calendar_mp, R.id.calendar_name,R.id.calendar_value, R.id.calendar_color};

                        startManagingCursor(cursor);
                        if(date.equals(currentDateTimeString)) {
                            System.out.println("equal");
                        }

                        adapter = new SimpleCursorAdapter(Calendar.this, R.layout.calendaritem_list, cursor2, from, to);
                        listView = (ListView) findViewById(R.id.list_cal);
                        listView.setAdapter(adapter);

                    }
                });
            }
        });

        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CheckBox checkBox;
                for (int i = 0; i < adapter.getCount(); i++) {
                    checkBox = (CheckBox)(listView.getChildAt(i).findViewById(R.id.checkbox_delete));
                    if(checkBox.isChecked()) {
                        TextView textView = listView.getChildAt(i).findViewById(R.id.calendar_name);
                        String title = textView.getText().toString();

                        db.execSQL("DELETE FROM todolists WHERE name = '" + title + "'");
                        Toast.makeText(getApplicationContext(), "성공적으로 삭제되었음", Toast.LENGTH_SHORT).show();
                    }
                }
                onResume();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        cursor = db.rawQuery("SELECT * FROM todolists order by _id desc", null);
        String[] from = {"date", "mp", "name", "value", "color"};
        int[] to = {R.id.calendar_date, R.id.calendar_mp, R.id.calendar_name,R.id.calendar_value, R.id.calendar_color};

        adapter = new SimpleCursorAdapter(this, R.layout.calendaritem_list, cursor, from, to);
        listView.setAdapter(adapter);
    }
}