package com.yiming.myapplication;

import android.widget.AbsListView;

import java.lang.reflect.Method;

/**
 * Created by Administrator on 2017/3/19.
 */

public class HookUtils {
    public static Object callMethod() {
        try {
            // gloam:
            Method mReportScrollStateChangeMethod = AbsListView.class.getMethod("reportScrollStateChange", int.class);
        } catch (Exception exp) {
            exp.printStackTrace();;
        }

        return null;
    }
}
