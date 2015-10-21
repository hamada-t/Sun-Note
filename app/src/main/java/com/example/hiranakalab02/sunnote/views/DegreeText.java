package com.example.hiranakalab02.sunnote.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.format.Time;
import android.util.Log;
import android.view.View;

import com.example.hiranakalab02.sunnote.common.ObjectPosition;

import java.util.Date;

/**
 * Created by HiranakaLab02 on 2015/08/28.
 */
public class DegreeText extends View {
    private String degreeText;
    private ObjectPosition mObjectPosition = new ObjectPosition();
    private Paint paint = new Paint();
    int Azimuth = 0;
    int Roll = 0;
    float[] sensorValues = new float[3];
    float[] angleValues = new float[2];
    int[] displayValues = new int[2];
    String date;
    String[] eight_degrees = {"北", "北東", "東", "南東", "南", "南西", "西", "北西", "東"};
    String[] four_degrees = {
            "北", "", "", "", "", "", "", "", "",
            "東", "", "", "", "", "", "", "", "",
            "南", "", "", "", "", "", "", "", "",
            "西", "", "", "", "", "", "", "", ""
    };

    private Date DateData;

    public DegreeText(Context context){
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        drawText(canvas, paint);
        drawScale(canvas, paint);
        // 再描画処理
        invalidate();
    }

    private void drawText(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);

        Azimuth = (Math.round((float)Math.toDegrees(sensorValues[0]) + 360) + 90) % 360;

        Roll = (Math.round((float)Math.toDegrees(sensorValues[2])) + 90) * -1;
        if (Roll < -90 && Roll >= -270){
            Roll += 180;
            Roll *= -1;
        }

        /*
        int n = (int)Math.round(Azimuth / (180 / 8) / 2);
        //Log.v("n", String.valueOf(n));
        if (n >= 0) {
            degreeText = eight_degrees[n];
        }
        */

        if ( Azimuth >= 338 || Azimuth <= 22 ){
            degreeText = "北";
        } else if ( Azimuth >= 23 && Azimuth <= 67 ){
            degreeText = "北東";
        } else if ( Azimuth >= 68 && Azimuth <= 112 ){
            degreeText = "東";
        } else if ( Azimuth >= 113 && Azimuth <= 157 ){
            degreeText = "南東";
        } else if ( Azimuth >= 158 && Azimuth <= 202 ){
            degreeText = "南";
        } else if ( Azimuth >= 203 && Azimuth <= 247 ){
            degreeText = "南西";
        } else if ( Azimuth >= 248 && Azimuth <= 292 ){
            degreeText = "西";
        } else if ( Azimuth >= 293 && Azimuth <= 337 ){
            degreeText = "北西";
        }

        Time time = new Time("Asia/Tokyo");
        time.setToNow();
        date = time.year + "年\t" + (time.month + 1) + "月" + time.monthDay + "日\t" + time.hour + "時" + time.minute + "分";
        DateData = new Date();

        canvas.drawText(String.format("%s\t\t方位角：%4d\t\t  方位：%s\t\t 仰角：%4d", date, Azimuth, degreeText, Roll), 150, 50, paint);
    }

    /**
     * 目盛を表示するためのクラス
     * @param canvas
     * @param paint
     */
    private void drawScale(Canvas canvas, Paint paint){
        paint.setColor(Color.WHITE);
        paint.setStrokeWidth(7.0f);
        final float alpha = 10;
        final float width = 40;
        final float height = 40;
        // angleX：10度ごとの画面の横幅のピクセル数
        double angleX = displayValues[0] / 6;
        // angleY：10度ごとの画面の縦幅のピクセル数
        // 16:9 = 6:x, x = 3.375
        double angleY = displayValues[1] / 3.375;

        double angleXText = Math.floor((Math.toDegrees(sensorValues[0]) + 180) / 10) * 10 - (60 / alpha * 10);

        //Log.v("degreeIndex", String.valueOf(degreeIndex));

        // 画面が向いている方向を取得し、目盛の最初の描画位置を決める計算式
        for (float x = (float)((Math.toDegrees(sensorValues[0]) + 180) % alpha / alpha * angleX); x < displayValues[0]; x += angleX){
            int degreeIndex = (int)angleXText / 10;
            canvas.drawLine(x, displayValues[1] - 30, x, (displayValues[1] - 30 - height), paint);
            canvas.drawText(String.format("%3.0f", angleXText), x - 35, displayValues[1], paint);
            //canvas.drawText(degree[degreeIndex], x - 35, displayValues[1] - 90, paint);
            angleXText -= alpha;
            degreeIndex -= 1;
            if (angleXText < 0){
                angleXText += 360;
            }
            if (degreeIndex < 0){
                degreeIndex += 36;
            }
        }

        for (float y = (float)((Math.toDegrees(sensorValues[2]) + 180) % alpha / alpha * angleY); y < displayValues[1]; y += angleY){
            canvas.drawLine(0, y, (0 + width), y, paint);
        }
    }

    public Date getDate(){
        return DateData;
    }

    public String getDateText() {
        return date;
    }

    public String getOrientation(){
        return degreeText;
    }

    public double getAzimuth(){
        Log.v("Azimuth", String.valueOf(Azimuth));
        return (double)Azimuth;
    }

    public double getRoll(){
        Log.v("Roll", String.valueOf(Roll));
        return (double)Roll;
    }

    // センサー情報を得る
    public void setOrientationValues(float[] orientationValues) {
        this.sensorValues = orientationValues;
    }

    // カメラのアングルを得る
    public void setCameraAngle(float[] angle){
        this.angleValues = angle;
        //Log.v("angle", String.valueOf(this.angleValues[0]) + "\t" + String.valueOf(this.angleValues[1]));
    }

    public void setDisplayValues(int x, int y){
        this.displayValues[0] = x;
        this.displayValues[1] = y;
        /*
        Log.v("x", String.valueOf(this.displayValues[0]));
        Log.v("y", String.valueOf(this.displayValues[1]));
        */
    }
}
