package com.example.hiranakalab02.sunnote.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.hiranakalab02.sunnote.common.MyFilter;
import com.example.hiranakalab02.sunnote.common.ObjectPosition;
import com.example.hiranakalab02.sunnote.gles.Figure;
import com.example.hiranakalab02.sunnote.gles.GLManager;
import com.example.hiranakalab02.sunnote.gles.Object3D;
import com.example.hiranakalab02.sunnote.mqo.MQOFormatImporter;
import com.example.hiranakalab02.sunnote.realm.SunNote;
import com.example.hiranakalab02.sunnote.views.CameraView;
import com.example.hiranakalab02.sunnote.views.DegreeText;
import com.example.hiranakalab02.sunnote.views.DrawAlignment;
import com.example.hiranakalab02.sunnote.views.VersionText;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;
import javax.microedition.khronos.opengles.GL11;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.exceptions.RealmException;

/**
 * Created by HiranakaLab02 on 2015/08/28.
 */
public class MainActivity extends Activity implements SensorEventListener {
    private CameraView mCameraView;
    private GLSurfaceView mGLSurfaceView;
    private MQRender mMQRender;
    private DegreeText mDegreeText;
    private DrawAlignment mDrawAlignment;
    private VersionText mVersionText;
    private MyFilter mMyFilter;
    private GestureDetector mGestureDetector;
    private ObjectPosition mObjectPosition;
    private SQLiteDatabase db;
    private AlertDialog.Builder alertDlg;
    private Realm mRealm;
    private SunNote mSunNote;
    private Sensor mSensor;
    private SensorManager mSensorManager = null;
    private FrameLayout mFrameLayout;

    // 画面サイズ
    private int displayX;
    private int displayY;

    private boolean mIsMagSensor;
    private boolean mIsAccSensor;

    private static final int MATRIX_SIZE = 16;
    /** 回転行列 */
    float[]  inR = new float[MATRIX_SIZE];
    float[] outR = new float[MATRIX_SIZE];
    float[]    I = new float[MATRIX_SIZE];

    /** センサーの値 */
    float[] magneticValues      = new float[3];
    float[] accelerometerValues = new float[3];
    float[] filteredValues      = new float[3];
    float[] params      = new float[2];
    float[] orientationValues   = { 0.0f, 0.0f, 0.0f };

    private static int n;
    static ArrayList<Float> sunRollArray = new ArrayList<>(n);
    static ArrayList<Float> sunAzimuthArray = new ArrayList<>(n);
    static ArrayList<Float> moonRollArray = new ArrayList<>(n);
    static ArrayList<Float> moonAzimuthArray = new ArrayList<>(n);

    // 記録されて初めて描画するようにするためのフラグ
    boolean drawFrag = false;

    public static final String KEY_PREF_GENERAL = "mode_switch";
    boolean modeSwitchPreference;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // フルスクリーン設定
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // タイトルバーの非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        // 各種センサーの用意
        mSensorManager = (SensorManager)getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);

        // 画面サイズの取得
        Display display = ((WindowManager)this.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        displayX = display.getWidth();
        displayY = display.getHeight();

        // Realmのインスタンスを取得
        mRealm = Realm.getInstance(this);

        // 確認ダイアログの生成
        alertDlg = new AlertDialog.Builder(this);

        mCameraView = new CameraView(this);
        mMQRender = new MQRender();
        params = mCameraView.getParams();

        mDegreeText = new DegreeText(this);
        mDegreeText.setCameraAngle(params);
        mDegreeText.setDisplayValues(displayX, displayY);

        mDrawAlignment = new DrawAlignment(this);
        mDrawAlignment.setDisplayValues(displayX, displayY);

        mVersionText = new VersionText(this);
        mObjectPosition = new ObjectPosition();
        mMyFilter = new MyFilter();

        // GestureDetectorクラスのインスタンス生成
        mGestureDetector = new GestureDetector(this, onGestureListener);
        //mMyOpenHelper = new MyOpenHelper(this);
        mGLSurfaceView = new GLSurfaceView(this);

        mGLSurfaceView.setEGLConfigChooser(8, 8, 8, 8, 16, 0);
        mGLSurfaceView.setRenderer(mMQRender);
        mGLSurfaceView.getHolder().setFormat(PixelFormat.TRANSLUCENT);

        mFrameLayout = new FrameLayout(this);
        mFrameLayout.setBackgroundColor(Color.BLACK);
        mFrameLayout.setAlpha(0.5f);

        setContentView(mGLSurfaceView);
        //setContentView(R.layout.activity_main);
        //addContentView(mGLSurfaceView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addContentView(mCameraView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addContentView(mFrameLayout, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addContentView(mDegreeText, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addContentView(mVersionText, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addContentView(mDrawAlignment, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 設定を読み込む
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        modeSwitchPreference = sharedPreferences.getBoolean(KEY_PREF_GENERAL, false);
        Log.v("preference", String.valueOf(modeSwitchPreference));

        mGLSurfaceView.onResume();

        // センサの取得
        List<Sensor> sensors = mSensorManager.getSensorList(Sensor.TYPE_ALL);

        // センサマネージャへリスナーを登録(implements SensorEventListenerにより、thisで登録する)
        for (Sensor sensor : sensors) {
            if( sensor.getType() == Sensor.TYPE_MAGNETIC_FIELD){
                mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
                mIsMagSensor = true;
            }
            if( sensor.getType() == Sensor.TYPE_ACCELEROMETER){
                mSensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_UI);
                mIsAccSensor = true;
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        mSensorManager.unregisterListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGLSurfaceView.onPause();

        //センサーマネージャのリスナ登録破棄
        if (mIsMagSensor || mIsAccSensor) {
            mSensorManager.unregisterListener(this);
            mIsMagSensor = false;
            mIsAccSensor = false;
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    // センサー値の反映
    @Override
    public void onSensorChanged(SensorEvent event) {

        switch (event.sensor.getType()) {
            case Sensor.TYPE_MAGNETIC_FIELD:
                magneticValues = event.values.clone();
                //Log.v("magneticValues",String.valueOf(magneticValues[0]));
                break;

            case Sensor.TYPE_ACCELEROMETER:
                accelerometerValues = event.values.clone();
                //Log.v("eventValue", String.valueOf(accelerometerValues[0]) + "," + String.valueOf(accelerometerValues[1]) + "," + String.valueOf(accelerometerValues[2]));
                break;
        }

        if (magneticValues != null && accelerometerValues != null) {
            // 回転行列を得る
            SensorManager.getRotationMatrix(inR, I, accelerometerValues, magneticValues);

            /*
            // デバイスの座標系を変換
            SensorManager.remapCoordinateSystem(inR, SensorManager.AXIS_Y, SensorManager.AXIS_MINUS_X, outR) ;

            //姿勢を得る
            SensorManager.getOrientation(outR, orientationValues);
            */

            //姿勢を得る
            SensorManager.getOrientation(inR,orientationValues);

            try {
                filteredValues = mMyFilter.HighPassFilter(orientationValues);
            } catch (Exception e){
                Log.v("error", String.valueOf(e));
            }

            mMQRender.setState(filteredValues[0], filteredValues[2]);
            mDegreeText.setOrientationValues(filteredValues);
        }
    }

    public boolean onTouchEvent(MotionEvent event) {
        // タッチイベントをGestureDetector#onTouchEventメソッドに
        mGestureDetector.onTouchEvent(event);
        return false;
    }

    /**
     * シングルタップ
     *　　　　onDown -> (onShowPress) -> onSingleTapUp -> onSingleTapConfirmed
     *　　　　onDown -> onShowPress -> onSingleTapUp
     * ロングタップ
     *　　　　onDown -> onShowPress -> onLongPress
     * ダブルタップ
     *　　　　onDown -> (onShowPress) -> onSingleTapUp -> onDoubleTap -> onDoubleTapEvent -> onDown -> onDoubleTapEvent * n
     *　　　　onDown -> onSingleTapUp -> onDoubleTap -> onDoubleTapEvent -> onDown -> onDoubleTapEvent * m -> onShowPress -> onDoubleTabEvent * n
     * スクロール
     *　　　　onDown -> (onShowPress) -> onScroll * n
     * フリック
     *　　　　onDown -> (onShowPress) -> onScroll * n -> onFling
     */
    // 複雑なタッチイベントを取得
    private final GestureDetector.SimpleOnGestureListener onGestureListener = new GestureDetector.SimpleOnGestureListener() {

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.v("Gesture", "onDoubleTap");
            // 端末のバックボタンでダイアログが閉じないように設定
            alertDlg.setCancelable(false);
            alertDlg.setTitle("確認");
            alertDlg.setMessage("データベースを削除しますか？");
            alertDlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // OK ボタンクリック処理
                    try {
                        mRealm.beginTransaction();
                        RealmQuery<SunNote> query = mRealm.where(SunNote.class);
                        RealmResults<SunNote> results = query.findAll();
                        // 全て削除する
                        results.clear();
                        mRealm.commitTransaction();
                        Toast.makeText(MainActivity.this, "削除しました", Toast.LENGTH_SHORT).show();
                    } catch (RealmException error) {
                        Log.v("error", String.valueOf(error));
                        Toast.makeText(MainActivity.this, "削除に失敗しました", Toast.LENGTH_SHORT).show();
                    }
                }
            });
            alertDlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Cancel ボタンクリック処理
                        }
                    }
            );

            // ダイアログを表示
            alertDlg.create().show();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.v("Gesture", "onDoubleTapEvent");
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onDown(MotionEvent e) {
            Log.v("Gesture", "onDown");
            return super.onDown(e);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            Log.v("Gesture", "onFling");
            Log.v("velocity", String.valueOf(velocityX + ":" + velocityY));

            if (velocityX > 0 && velocityY > 0) {
                // 右下フリック時の処理
            } else if (velocityX > 0 && velocityY < 0) {
                // 右上フリック時の処理
            } else if (velocityX < 0 && velocityY > 0) {
                // 左下フリック時の処理
            } else if (velocityX < 0 && velocityY < 0) {
                // 左上フリック時の処理
            }

            return super.onFling(e1, e2, velocityX, velocityY);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            Log.v("Gesture", "onLongPress");
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            Log.v("Gesture", "onScroll");
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public void onShowPress(MotionEvent e) {
            Log.v("Gesture", "onShowPress");
            super.onShowPress(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.v("Gesture", "onSingleTapConfirmed");

            // 端末のバックボタンでダイアログが閉じないように設定
            alertDlg.setCancelable(false);
            alertDlg.setTitle("確認");
            alertDlg.setMessage("記録してもよろしいですか？");
            alertDlg.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // OK ボタンクリック処理
                    try {
                        // 書き込み処理
                        // 書き込み処理は必ずトランザクションの中で行う
                        mRealm.beginTransaction();
                        mSunNote = mRealm.createObject(SunNote.class);
                        // 現在 Realm に AutoIncrement 機能がないので、最大値 ＋１ で簡易的に表現
                        mSunNote.setId((int)mRealm.where(SunNote.class).maximumInt("id") + 1);
                        mSunNote.setDate(mDegreeText.getDate());
                        mSunNote.setPointX(displayX / 2);
                        mSunNote.setPointY(displayY / 2);
                        mSunNote.setOrientation(mDegreeText.getOrientation());
                        mSunNote.setAzimuth((int) mDegreeText.getAzimuth());
                        mSunNote.setRoll((int) mDegreeText.getRoll());
                        mSunNote.setDateText(mDegreeText.getDateText());
                        mSunNote.setMode(modeSwitchPreference);
                        // commitTransaction を実行して保存
                        mRealm.commitTransaction();

                        // 第3引数は、表示期間（LENGTH_SHORT、または、LENGTH_LONG）
                        Toast.makeText(MainActivity.this, "記録しました。", Toast.LENGTH_SHORT).show();

                    } catch (RealmException error) {
                        Log.v("error", String.valueOf(error));
                        Toast.makeText(MainActivity.this, "記録に失敗しました。", Toast.LENGTH_SHORT).show();
                    }

                    if (modeSwitchPreference == true) {
                        try {
                            drawFrag = true;

                            sunAzimuthArray.add((float) mDegreeText.getAzimuth());
                            sunRollArray.add((float) mDegreeText.getRoll());
                            n = sunAzimuthArray.size();
                            Log.v("n", String.valueOf(n));
                            //Log.v("azimuth,roll", String.valueOf(mDegreeText.getAzimuth()) + "," + String.valueOf(mDegreeText.getRoll()));
                        } catch (Exception e) {
                            Log.v("e", String.valueOf(e));
                        }

                    } else if (modeSwitchPreference == false) {
                        try {
                            drawFrag = true;

                            moonAzimuthArray.add((float) mDegreeText.getAzimuth());
                            moonRollArray.add((float) mDegreeText.getRoll());
                            n = moonAzimuthArray.size();

                            Log.v("n", String.valueOf(n));
                            //Log.v("azimuth,roll", String.valueOf(mDegreeText.getAzimuth()) + "," + String.valueOf(mDegreeText.getRoll()));
                        } catch (Exception e) {
                            Log.v("e", String.valueOf(e));
                        }
                    }
                }
            });
            alertDlg.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            // Cancel ボタンクリック処理
                        }
                    }
            );

            // ダイアログを表示
            alertDlg.create().show();

            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            Log.v("Gesture", "onSingleTapUp");
            return super.onSingleTapUp(e);
        }
    };

    /**
     * MQOファイルを描画するためのクラス
     */
    public class MQRender implements GLSurfaceView.Renderer {
        private Object3D sun = new Object3D();
        private Object3D moon = new Object3D();
        // オブジェクトの遠さ（円の半径）
        private float radius = 1000;

        private GLManager glManager = null;
        private ObjectPosition mObjectPosition = new ObjectPosition();
        private float aspect = 0.0f;

        private float camThetaXZ = 0.0f;
        private float camThetaY = 0.0f;
        final float CAMERA_R = 10;
        private float positionValues[] = new float[3];

        @Override
        public void onSurfaceCreated(GL10 gl, EGLConfig config) {
            mObjectPosition.setRadiusValue(radius);
         /*
         * By default, OpenGL enables features that improve quality
         * but reduce performance. One might want to tweak that
         * especially on software renderer.
         */
            gl.glDisable(GL10.GL_DITHER);

        /*
         * Some one-time OpenGL initialization can be made here
         * probably based on features of this particular context
         */
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

            gl.glClearColor(0, 0, 0, 0);
            gl.glEnable(GL10.GL_CULL_FACE);
            gl.glShadeModel(GL10.GL_SMOOTH);
            gl.glEnable(GL10.GL_DEPTH_TEST);
            // ディザ処理を無効化し、なめらかな表示を行います
            gl.glDisable(GL10.GL_DITHER);
            // 透視射影の程度を処理速度を重視したものを指定します
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);

        }

        @Override
        public void onSurfaceChanged(GL10 gl, int width, int height) {

            // 描画領域が変更されたときに呼び出されます

            // 描画を領域行う領域を指定します
            // ここでは画面全体を指定しています
            gl.glViewport(0, 0, width, height);

            float ratio = (float) width / height;

            // 射影行列を選択する状態にします
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();
            // 射影方法を遠近法を使用する透視射影として描画領域を指定します
            gl.glFrustumf(-ratio, ratio, -1.0f, 1.0f, 1f, 10000f);
            // ディザ処理を無効化し、なめらかな表示を行います
            gl.glDisable(GL10.GL_DITHER);
            // 透視射影の程度を処理速度を重視したものを指定します
            gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_FASTEST);
            // ポリゴンの背面を描画しなようにします
            gl.glEnable(GL10.GL_CULL_FACE);
            // 面の描画をなめらかにするようにします
            gl.glShadeModel(GL10.GL_SMOOTH);
            // デプスバッファを有効化します
            gl.glEnable(GL10.GL_DEPTH_TEST);
            // テクスチャを有効化します
            gl.glEnable(GL10.GL_TEXTURE_2D);

            aspect = (float) width / (float) height;

            //! MQO読み込み
            try {
                glManager = new GLManager((GL11) gl, width, height);
                glManager.setContext(MainActivity.this);

                if (modeSwitchPreference == true) {
                    //! 球本体
                    {
                        InputStream is = getAssets().open("sun.mqo");
                        MQOFormatImporter imp = new MQOFormatImporter(is);

                        sun.setGlManager(glManager);
                        sun.setFigure(new Figure(glManager, imp.getConvertObject()));

                        is.close();
                    }
                } else if (modeSwitchPreference == false) {
                    //! 球本体
                    {
                        InputStream is = getAssets().open("moon.mqo");
                        MQOFormatImporter imp = new MQOFormatImporter(is);

                        moon.setGlManager(glManager);
                        moon.setFigure(new Figure(glManager, imp.getConvertObject()));

                        is.close();
                    }
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onDrawFrame(GL10 gl) {
            // 描画処理を行う前にバッファの消去を行う
            // GL_COLOR_BUFFER_BITではカラーバッファを
            // GL10.GL_DEPTH_BUFFER_BITでは陰面消去に使われるデプスバッファを指定しています
            gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);

            //! カメラ転送
            //! 呼び出さなかった場合の動作を考える
            gl.glMatrixMode(GL10.GL_PROJECTION);
            gl.glLoadIdentity();

            // 端末の向きから視点を計算する
            float centerY = CAMERA_R * (float) Math.sin(this.camThetaY);
            float centerX = CAMERA_R * (float) Math.cos(this.camThetaY) * (float) Math.cos(-this.camThetaXZ);
            float centerZ = CAMERA_R * (float) Math.cos(this.camThetaY) * (float) Math.sin(-this.camThetaXZ);

            // カメラの位置と、視点の中心を指定する
            GLU.gluPerspective(gl, 45.0f, aspect, 1.0f, 1000.0f);
            GLU.gluLookAt(gl, 0, 0, 0, centerX, centerY, centerZ, 0f, 1f, 0f);

            // 頂点配列を有効化します
            gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);
            // カラー配列を有効化します
            gl.glEnableClientState(GL10.GL_COLOR_ARRAY);

            // 演算対象をモデルビューにする
            gl.glMatrixMode(GL10.GL_MODELVIEW);
            // 演算行列を単位行列にする
            gl.glLoadIdentity();

            // 記録されて初めてオブジェクトを表示rするようにする
            if (drawFrag == true && modeSwitchPreference == true) {
                    for (int i = 0; i < n; i++) {
                        double m = (double)sunAzimuthArray.get(i);
                        double n = (double)sunRollArray.get(i);
                        mObjectPosition.setAzimuth(m);
                        mObjectPosition.setRoll(n);
                        onDrawObject();
               }
            } else if (drawFrag == true && modeSwitchPreference == false) {
                    for (int i = 0; i < n; i++) {
                        double m = (double)moonAzimuthArray.get(i);
                        double n = (double)moonRollArray.get(i);
                        mObjectPosition.setAzimuth(m);
                        mObjectPosition.setRoll(n);
                        onDrawObject();
               }
            }
        }

        public void onDrawObject(){
            //! 球描画
            if (modeSwitchPreference == true) {
                positionValues = ObjectPosition.getPositionValues();
                sun.setPosition(positionValues[0], positionValues[1], positionValues[2]);
                sun.draw();
            } else if (modeSwitchPreference == false) {
                positionValues = ObjectPosition.getPositionValues();
                moon.setPosition(positionValues[0], positionValues[1], positionValues[2]);
                moon.draw();
            }
        }

        // センサー情報をセット
        public void setState(float thetaXZ, float thetaY) {
            // カメラの角度を取得
            this.camThetaXZ = -thetaXZ;
            this.camThetaY = (float) (-Math.PI / 2 - thetaY);
        }
    }
}
