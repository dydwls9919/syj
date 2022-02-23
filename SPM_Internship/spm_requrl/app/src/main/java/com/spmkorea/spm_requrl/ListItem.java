package com.spmkorea.spm_requrl;

import android.widget.ImageView;

public class ListItem {
    private String DateStr ;
    private String MPStr ;
    private String NameStr ;
    private String ValueStr ;
    private String ColorStr ;
    private ImageView ColorImage;

    public void setDate(String Date) {
        DateStr = Date ;
    }
    public void setMP(String MP) {
        MPStr = MP ;
    }
    public void setValue(String Value) {
        ValueStr = Value ;
    }
    public void setColor(String Color) {
        ColorStr = Color ;
    }
    public void setName(String Name) {
        NameStr = Name ;
    }
    public void setImage(ImageView image) {
        ColorImage = image ;
    }

    public String getDate() {
        return this.DateStr ;
    }
    public String getMP() {
        return this.MPStr ;
    }
    public String getName() {
        return this.NameStr ;
    }
    public String getValue() {
        return this.ValueStr ;
    }
    public String getColor() {
        return this.ColorStr ;
    }
    public ImageView getImage() {
        return this.ColorImage ;
    }
}
