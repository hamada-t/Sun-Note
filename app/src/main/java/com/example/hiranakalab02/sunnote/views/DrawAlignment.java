package com.example.hiranakalab02.sunnote.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * 照準を描画するクラス
 */
public class DrawAlignment extends View {
    private Paint paint = new Paint();
    private int POS_ARX;
    private int POS_ARY;
    int[] displayValues = new int[2];

    public DrawAlignment(Context context){
        super(context);
    }

    @Override
    protected void onDraw(Canvas canvas){
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        drawAlignment(canvas, paint);
        drawAlignment2(canvas, paint);
        // 再描画処理
        invalidate();
    }

    private void drawAlignment(Canvas canvas, Paint paint){
        POS_ARX = displayValues[0] / 2;
        POS_ARY = displayValues[1] / 2;
        // 図形の内部を塗りつぶす方式
        paint.setStyle(Paint.Style.FILL);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10.0f);

        // drawCircle( 中心横, 中心縦, 半径, [Paint] )
        canvas.drawCircle(POS_ARX, POS_ARY, 70, paint);
    }

    public void drawAlignment2(Canvas canvas, Paint paint){
        paint.setStyle(Paint.Style.FILL);
        paint.setStyle(Paint.Style.STROKE);
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(3.0f);

        // drawCircle( 中心横, 中心縦, 半径, [Paint] )
        canvas.drawCircle(POS_ARX, POS_ARY, 60, paint);
        canvas.drawCircle(POS_ARX, POS_ARY, 10, paint);

        float[] pts1 = { POS_ARX - 65, POS_ARY, POS_ARX + 65, POS_ARY };
        canvas.drawLines(pts1, paint);

        float[] pts2 = { POS_ARX, POS_ARY - 65, POS_ARX, POS_ARY + 65 };
        canvas.drawLines(pts2, paint);

        for (float i = 20.0f; i <= 50.0f; i += 15.0f) {
            float[] pts3 = {POS_ARX - i, POS_ARY - 10, POS_ARX - i, POS_ARY + 10};
            canvas.drawLines(pts3, paint);

            float[] pts4 = {POS_ARX - 10, POS_ARY - i, POS_ARX + 10, POS_ARY - i};
            canvas.drawLines(pts4, paint);

            float[] pts5 = {POS_ARX + i, POS_ARY - 10, POS_ARX + i, POS_ARY + 10};
            canvas.drawLines(pts5, paint);

            float[] pts6 = {POS_ARX - 10, POS_ARY + i, POS_ARX + 10, POS_ARY + i};
            canvas.drawLines(pts6, paint);
        }

        for (float i = 27.5f; i <= 42.5f; i += 15.0f){
            float[] pts7 = {POS_ARX - i, POS_ARY - 6, POS_ARX - i, POS_ARY + 6};
            canvas.drawLines(pts7, paint);

            float[] pts8 = {POS_ARX - 6, POS_ARY - i, POS_ARX + 6, POS_ARY - i};
            canvas.drawLines(pts8, paint);

            float[] pts9 = {POS_ARX + i, POS_ARY - 6, POS_ARX + i, POS_ARY + 6};
            canvas.drawLines(pts9, paint);

            float[] pts10 = {POS_ARX - 6, POS_ARY + i, POS_ARX + 6, POS_ARY + i};
            canvas.drawLines(pts10, paint);
        }
    }

    public void setDisplayValues(int x, int y){
        this.displayValues[0] = x;
        this.displayValues[1] = y;
    }

}
