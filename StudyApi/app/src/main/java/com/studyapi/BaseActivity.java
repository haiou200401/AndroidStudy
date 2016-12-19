package com.studyapi;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by gaoqingguang on 2016/6/17.
 */
public class BaseActivity extends AppCompatActivity {
    final String TAG = "gqg:BaseActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String className = getClass().getName();
        Log.e(TAG, className);
    }

    @Override
    protected void onStart() {
        super.onStart();

        String className = getClass().getName();
        Log.e(TAG, className);
    }

    @Override
    protected void onResume() {
        super.onResume();
        String className = getClass().getName();
        Log.e(TAG, className);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        String className = getClass().getName();
        Log.e(TAG, className);
    }

    @Override
    protected void onPause() {
        super.onPause();
        String className = getClass().getName();
        Log.e(TAG, className);
    }

    @Override
    protected void onStop() {
        super.onStop();
        String className = getClass().getName();
        Log.e(TAG, className);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        String className = getClass().getName();
        Log.e(TAG, className);
    }
}
