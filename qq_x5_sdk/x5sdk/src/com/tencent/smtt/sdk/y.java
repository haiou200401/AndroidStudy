package com.tencent.smtt.sdk;

import android.os.HandlerThread;

class y extends HandlerThread {
    private static y a;

    public y(String str) {
        super(str);
    }

    public static synchronized y a() {
        y yVar;
        synchronized (y.class) {
            if (a == null) {
                a = new y("TbsHandlerThread");
                a.start();
            }
            yVar = a;
        }
        return yVar;
    }
}
