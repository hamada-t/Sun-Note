package com.example.hiranakalab02.sunnote.realm;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by HiranakaLab02 on 2015/09/20.
 */
public class SunNote extends RealmObject {
    @PrimaryKey
    private int id;

    private Date date;
    private float pointX;
    private float pointY;
    private String orientation;
    private int azimuth;
    private int roll;
    private String dateText;
    private boolean mode;

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id = id;
    }

    public Date getDate(){
        return date;
    }

    public void setDate(Date date){
        this.date = date;
    }

    public float getPointX(){
        return pointX;
    }

    public void setPointX(float pointX){
        this.pointX = pointX;
    }

    public float getPointY(){
        return pointY;
    }

    public void setPointY(float pointY){
        this.pointY = pointY;
    }

    public String getOrientation(){
        return orientation;
    }

    public void setOrientation(String orientation){
        this.orientation = orientation;
    }

    public int getAzimuth(){
        return azimuth;
    }

    public void setAzimuth(int azimuth){
        this.azimuth = azimuth;
    }

    public int getRoll(){
        return roll;
    }

    public void setRoll(int roll){
        this.roll = roll;
    }

    public String getDateText(){
        return dateText;
    }

    public void setDateText(String dateText){
        this.dateText = dateText;
    }

    public boolean getMode() {
        return mode;
    }

    public void setMode(boolean mode) {
        this.mode = mode;
    }

}


