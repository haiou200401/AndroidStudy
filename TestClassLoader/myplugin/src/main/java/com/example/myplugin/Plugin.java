package com.example.myplugin;

import android.app.Application;
import android.content.res.Resources;
import android.util.Log;

import com.example.mylibrary.TestInterface;

import java.lang.reflect.Method;

import com.example.myplugin.R;

/**
 * Created by Administrator on 2016/12/11.
 */
public class Plugin {
    private static final String TAG=Plugin.class.getSimpleName();

    private Method mCurrentApplicationMethod;

    public Plugin() {
        Log.e(TAG, "Plugin class is initialized");
        try {
            mCurrentApplicationMethod =
                    Class.forName("android.app.ActivityThread").getMethod("currentApplication");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Application getApplication() {
        try {
            return (Application) mCurrentApplicationMethod.invoke(null);
        } catch (Exception e) {
            throw new RuntimeException("Invalid reflection", e);
        }
    }

    public int add(int a, int b) {
        return a + b;
    }

    public String getName() {
        Application application = getApplication();
        Resources resources = application.getResources();

        String str;
        try {
            str = resources.getString(R.string.myplugin_str1);
            Log.e(TAG, str);
            str = resources.getString(R.string.myplugin_str2);
            Log.e(TAG, str);
            str = resources.getString(R.string.myplugin_str3);
            Log.e(TAG, str);
            str = resources.getString(R.string.myplugin_str4);
            Log.e(TAG, str);
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        return "Plugin haha!!!";
    }

    public TestInterface getInterface(int a, int b) {
        return new TestInterface() {
            @Override
            public int fun1(int a, int b) {
                return a + b;
            }

            @Override
            public int getName() {
                return 33;
            }
        };
    }
}
