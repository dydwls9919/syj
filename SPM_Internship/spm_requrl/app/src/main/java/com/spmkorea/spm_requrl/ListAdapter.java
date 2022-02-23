package com.spmkorea.spm_requrl;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class ListAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<ListItem> listViewItemList = new ArrayList<ListItem>() ;

    // ListViewAdapter의 생성자
    public ListAdapter() {
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();


        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.item_list, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        TextView NameTextView = (TextView) convertView.findViewById(R.id.list_name) ;
        TextView ValueTextView = (TextView) convertView.findViewById(R.id.list_value) ;
        TextView ColorTextView = (TextView) convertView.findViewById(R.id.list_color) ;
        ImageView ColorImageView = (ImageView) convertView.findViewById(R.id.list_image) ;
        TextView DateTextView = (TextView) convertView.findViewById(R.id.list_date);
        TextView MPTextView = (TextView) convertView.findViewById(R.id.list_mp);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        ListItem listViewItem = listViewItemList.get(position);

        // 아이템 내 각 위젯에 데이터 반영
        DateTextView.setText("Date: " +listViewItem.getDate());
        MPTextView.setText("MeasuringPoint: " +listViewItem.getMP());
        NameTextView.setText(listViewItem.getName());
        ValueTextView.setText(listViewItem.getValue());
        ColorTextView.setText(listViewItem.getColor());

        if(ColorTextView.getText().equals("Yellow")) {
            ColorTextView.setText("");
            ColorImageView.setColorFilter(Color.parseColor("#FFFF00"));
        }
        if(ColorTextView.getText().equals("Red")) {
            ColorTextView.setText("");
            ColorImageView.setColorFilter(Color.parseColor("#FF0000"));
        }
        if(ColorTextView.getText().equals("Green")) {
            ColorTextView.setText("");
            ColorImageView.setColorFilter(Color.parseColor("#008000"));
        }
        if(ColorTextView.getText().equals(" ")) {
            ColorImageView.setColorFilter(Color.parseColor("#FFFFFF"));
        }
        return convertView;
    }

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {

        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(String Date, String MP, String Name, String Value, String Color, ImageView image) {
        ListItem item = new ListItem();

        item.setDate(Date);
        item.setMP(MP);
        item.setName(Name);
        item.setValue(Value);
        item.setColor(Color);
        item.setImage(image);

        listViewItemList.add(0,item);
    }
}

