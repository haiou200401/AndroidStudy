package com.studyapi;

import android.app.Application;
import android.content.res.Configuration;
import android.util.Log;

/**
 * Created by gaoqingguang on 2016/5/16.
 */
public class MainApplication extends Application {
    final String TAG = "gqg:Application";

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.e(TAG, "gqg MainApplication");
        Log.e(TAG, newConfig.toString());
    }
}
