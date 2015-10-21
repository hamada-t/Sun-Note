package com.example.hiranakalab02.sunnote.common;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by HiranakaLab02 on 2015/09/15.
 */
public class MyFilter {
    private float Values[] = { 0.0f, 0.0f, 0.0f };

    private float[] lowPassValues = new float[3];
    private float[] mediumValues = new float[3];
    private float[] highPassValues = new float[3];
    private float[] sum = new float[3];

    private final float alpha = 0.9f;
    private final int m = 10;
    private final int n = 11;
    private final int medium = 5;

    private ArrayList<Float> x = new ArrayList<>(n);
    private ArrayList<Float> y = new ArrayList<>(n);
    private ArrayList<Float> z = new ArrayList<>(n);
    private ArrayList<Float> x1 = new ArrayList<>(n);
    private ArrayList<Float> y1 = new ArrayList<>(n);
    private ArrayList<Float> z1 = new ArrayList<>(n);

    //ローパスフィルター
    private void LowPassFilter(){
        // αは、T / (T + DT）として算出される
        // tは、ローパスフィルタの時定数
        // DTはイベント配信速度。
        /*lowPassValues[0] = alpha * lowPassValues[0] + (1.0f - alpha) * Values[0];
        lowPassValues[1] = alpha * lowPassValues[1] + (1.0f - alpha) * Values[1];
        lowPassValues[2] = alpha * lowPassValues[2] + (1.0f - alpha) * Values[2];*/

        lowPassValues = Values;
    }

    // ハイパスフィルター
    // スパイクノイズ等をなくし、なめらかにするためのフィルター
    public float[] HighPassFilter(float[] orientationValues){
        /*
        Log.v("OrientationValues",
                String.valueOf(orientationValues[0]) + ", " + //Z軸方向,azimuth
                        String.valueOf(orientationValues[1]) + ", " + //X軸方向,pitch
                        String.valueOf(orientationValues[2])          //Y軸方向,roll
        );
        */

        // 配列に値を追加
        //正しい平均になるように３６０足す
        x.add((orientationValues[0] + 360));
        y.add((orientationValues[1] + 360));
        z.add((orientationValues[2] + 360));
        //Log.v("", x.toString());

        // n と 配列の要素の数が同じ場合
        if (x.size() == n) {
            // 配列をクローン
            ArrayList<Float> list = (ArrayList<Float>) x.clone();
            // 配列を降順もしくは昇順に並べ替え
            Collections.sort(list);
            // 配列を並べ替えた際に中央にある値を取得
            mediumValues[0] = list.get(medium);

            list = (ArrayList<Float>) y.clone();
            Collections.sort(list);
            mediumValues[1] = list.get(medium);

            list = (ArrayList<Float>) z.clone();
            Collections.sort(list);
            mediumValues[2] = list.get(medium);

            //一番最初の行を削除
            x.remove(0);
            y.remove(0);
            z.remove(0);

            x1.add(mediumValues[0]);
            y1.add(mediumValues[1]);
            z1.add(mediumValues[2]);

            // sum に値をどんどん足していく
            sum[0] += mediumValues[0];
            sum[1] += mediumValues[1];
            sum[2] += mediumValues[2];

            // アレイリストの要素の数が m より大きくなった場合
            if (x1.size() > m) {
                // sum から配列の一番最初の値を引きつつ、配列の一番最初の値を削除
                sum[0] -= x1.remove(0);
                sum[1] -= y1.remove(0);
                sum[2] -= z1.remove(0);
            }

            // sum を m で割って平均値を出す
            highPassValues[0] = sum[0] / m - 360;
            highPassValues[1] = sum[1] / m - 360;
            highPassValues[2] = sum[2] / m - 360;
        }
        // highPassValues を返す
        return highPassValues;
    }
}
