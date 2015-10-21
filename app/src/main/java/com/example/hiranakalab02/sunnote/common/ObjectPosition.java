package com.example.hiranakalab02.sunnote.common;

public class ObjectPosition {

    // 円の半径
    private static float radius;
    // 記録時の方位角
    private static double azimuth;
    // 記録時の高度
    private static double roll;
    // 再生時の座標
    private static float[] positionValues = new float[3];
    private static float X;
    private static float Y;
    private static float Z;

    private static double radianAzimuth;
    private static double radianRoll;

    // オブジェクトの距離を得る
    public void setRadiusValue(float radius){
        this.radius = radius;
    }

    // 記録した際の方位角を得る
    public void setAzimuth(double azimuth){
        // 東の90度がOpenGLでは(x,y,z)＝(r,0,0)(r≠0) なので、東が0度になるように計算
        this.azimuth = (azimuth + 270) % 360;
    }

    // 記録した際の高度を得る
    public void setRoll(double roll){
        this.roll = roll;
    }

    // 記録した方向からオブジェクトの２次元空間上の座標を求める
    public static float[] getPositionValues(){
        // 表示する座標を求める
        // 極座標変換 X=R*cosΘ*cosΦ, Z=R*cosΘ*sinΦ, Y=R*sinΘ (Θ：縦の回転角、Φ：横の回転角、0<=θ<=π, 0<=Φ<=2π)
        // 弧度法をラジアンに変換してから計算しなければならない。
        X = (float)(radius * Math.cos(Math.toRadians(roll)) * Math.cos(Math.toRadians(azimuth)));
        Y = (float)(radius * Math.sin(Math.toRadians(roll)));
        Z = (float)(radius * Math.cos(Math.toRadians(roll)) * Math.sin(Math.toRadians(azimuth)));

        positionValues[0] = X;
        positionValues[1] = Y;
        positionValues[2] = Z;

        return positionValues;
    }
}
