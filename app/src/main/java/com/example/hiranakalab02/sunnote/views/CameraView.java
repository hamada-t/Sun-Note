package com.example.hiranakalab02.sunnote.views;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.List;

/**
 * Created by HiranakaLab02 on 2015/08/28.
 */
public class CameraView extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder surfaceHolder;
    private Camera camera;
    private Camera.Size mSize;
    private Camera.Parameters params;

    //zoom
    int currentZoomLevel = 1, maxZoomLevel = 0;

    private float prms[] = {0.0f, 0.0f};

    public CameraView(Context context) {
        super(context);

        surfaceHolder = getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera = Camera.open();

            camera.setPreviewDisplay(holder);
            // カメラのパラメータ取得
            params = camera.getParameters();
            List<Camera.Size> sizes = params.getSupportedPreviewSizes();
            Camera.Size selected = sizes.get(0);
            params.setPreviewSize(selected.width, selected.height);
            // サポートしているフォーカスモードをログに出す
            List<String>focusList = params.getSupportedFocusModes();
            for (int i = 0; i < focusList.size(); i++){
                //Log.v("CameraFocus", "Mode = " + focusList.get(i));
            }
            // フォーカスモードを設定
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_INFINITY);
            // パラメータ設定
            camera.setParameters(params);

        } catch (Exception e) {
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        camera.setPreviewCallback(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        //String strParams = params.flatten();
        //Log.v("pa", strParams);
        //画角を取得する
        /*
        prms[0] = params.getHorizontalViewAngle();
        prms[1] = params.getVerticalViewAngle();
        Log.v("prams", String.valueOf(prms[0]) + "," + String.valueOf(prms[1]));
        */
            camera.startPreview();

    }

    public float[] getParams(){
        return prms;
    }

    /**
     * Zoom in and out
     */
    public void zoomin(float m){
        if(params.isZoomSupported()){
            maxZoomLevel = params.getMaxZoom();

            float zoom = (float)currentZoomLevel;
            if( m > 1)
                zoom +=  m;
            else
                zoom = zoom *m;

            currentZoomLevel = (int)zoom;

            if(currentZoomLevel > maxZoomLevel)
                currentZoomLevel = maxZoomLevel;
            if(currentZoomLevel < 1)
                currentZoomLevel = 1;

            params.setZoom(currentZoomLevel);
            camera.setParameters(params);

        }
    }

}
