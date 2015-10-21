package com.example.hiranakalab02.sunnote.activities;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TimePicker;

import com.example.hiranakalab02.sunnote.R;
import com.example.hiranakalab02.sunnote.activities.setting.SettingsActivity;
import com.example.hiranakalab02.sunnote.views.DatePick;
import com.example.hiranakalab02.sunnote.views.TimePick;

/**
 * Created by HiranakaLab02 on 2015/10/03.
 */
public class SelectActivity extends FragmentActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    private static int mode;
    private static int year = 0;
    private static int month = 0;
    private static int day = 0;
    private static int hour = 0;
    private static int minuit = 0;
    private boolean timePickFrag;
    public static final String KEY_PREF_GENERAL = "mode_switch";

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        // フルスクリーン設定
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        // タイトルバーの非表示
        requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_select);
        PreferenceManager.setDefaultValues(this, R.xml.pref_general, false);

        Button recordButton = (Button)findViewById(R.id.record_button);
        recordButton.setOnClickListener(new View.OnClickListener() {
            // ボタンがクリックされた時のハンドラ
            @Override
            public void onClick(View v) {
                // クリックされた時の処理を記述
                timePickFrag = false;
                Intent intent = new Intent(SelectActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });

        final Button onePlayButton = (Button)findViewById(R.id.one_play_button);
        onePlayButton.setOnClickListener(new View.OnClickListener() {
            // ボタンがクリックされた時のハンドラ
            @Override
            public void onClick(View v) {
                // クリックされた時の処理を記述
                mode = 0;
                timePickFrag = false;
                Intent intent = new Intent(SelectActivity.this, PlayListView.class);
                startActivity(intent);
            }
        });

        final Button timePlayButton = (Button)findViewById(R.id.time_play_button);
        timePlayButton.setOnClickListener(new View.OnClickListener() {
            // ボタンがクリックされた時のハンドラ
            @Override
            public void onClick(View v) {
                // クリックされた時の処理を記述
                mode = 1;
                timePickFrag = true;
                showTimePickerDialog(timePlayButton);
            }
        });

        final Button datePlayButton = (Button)findViewById(R.id.date_play_button);
        datePlayButton.setOnClickListener(new View.OnClickListener() {
            // ボタンがクリックされた時のハンドラ
            @Override
            public void onClick(View v) {
                // クリックされた時の処理を記述
                mode = 2;
                timePickFrag = false;
                showDatePickerDialog(datePlayButton);
            }
        });

        Button multiSelectPlayButton = (Button)findViewById(R.id.multi_select_play_button);
        multiSelectPlayButton.setOnClickListener(new View.OnClickListener() {
            // ボタンがクリックされた時のハンドラ
            @Override
            public void onClick(View v) {
                // クリックされた時の処理を記述
                mode = 3;
                timePickFrag = false;
            }
        });

        Button settingButton = (Button)findViewById(R.id.setting_button);
        settingButton.setOnClickListener(new View.OnClickListener() {
            // ボタンがクリックされた時のハンドラ
            @Override
            public void onClick(View v) {
                // クリックされた時の処理を記述
                Intent intent = new Intent(SelectActivity.this, SettingsActivity.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onStart(){
        super.onStart();
    }

    @Override
    public void onResume(){
        super.onResume();
        // 設定の読み込み
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        boolean switchPreference = sharedPreferences.getBoolean(KEY_PREF_GENERAL, false);
        Log.v("preference", String.valueOf(switchPreference));
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    @Override
    public void onPause(){
        super.onPause();
    }

    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
        this.year = year;
        this.month = monthOfYear + 1;
        this.day = dayOfMonth;
        if (this.year != 0 && this.month != 0 && this.day != 0){
            Intent intent = new Intent(SelectActivity.this, PlayActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void onTimeSet(TimePicker view, int hour, int minuit) {
        this.hour = hour;
        this.minuit = minuit;

        if (timePickFrag == true) {
            Intent intent = new Intent(SelectActivity.this, PlayActivity.class);
            startActivity(intent);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePick();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePick();
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    public static int getMode() {
        return mode;
    }

    public static int getYear() {
        return year;
    }

    public static int getMonth() {
        return month;
    }

    public static int getDay() {
        return day;
    }

    public static int getHour() {
        return hour;
    }

    public static int getMinuit() {
        return minuit;
    }
}
