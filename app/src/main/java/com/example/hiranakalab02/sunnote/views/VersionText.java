package com.example.hiranakalab02.sunnote.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;

/**
 * Created by HiranakaLab02 on 2015/08/28.
 */
public class VersionText extends View {
    Paint paint = new Paint();

    public VersionText(Context context){
        super(context);
    }

    @Override
    protected void onDraw (Canvas canvas){
        super.onDraw(canvas);
        paint.setAntiAlias(true);
        drawText(canvas, paint);
    }

    private void drawText(Canvas canvas, Paint paint) {
        paint.setColor(Color.WHITE);
        paint.setTextSize(40);

        String version = "ver.0.0.7";
        canvas.drawText(String.format("%s", version), 20, 1100, paint);

    }
}
