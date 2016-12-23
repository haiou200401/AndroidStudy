package com.tencent.smtt.sdk;

import android.content.Context;
import java.lang.reflect.Field;

public class CookieSyncManager {
    private static android.webkit.CookieSyncManager a;
    private static CookieSyncManager b;
    private static boolean c = false;

    private CookieSyncManager(Context context) {
        ao a = ao.a();
        if (a != null && a.b()) {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieSyncManager_createInstance", new Class[]{Context.class}, context);
            c = true;
        }
    }

    public static synchronized CookieSyncManager createInstance(Context context) {
        CookieSyncManager cookieSyncManager;
        synchronized (CookieSyncManager.class) {
            a = android.webkit.CookieSyncManager.createInstance(context);
            if (b == null || !c) {
                b = new CookieSyncManager(context.getApplicationContext());
            }
            cookieSyncManager = b;
        }
        return cookieSyncManager;
    }

    public static synchronized CookieSyncManager getInstance() {
        CookieSyncManager cookieSyncManager;
        synchronized (CookieSyncManager.class) {
            if (b == null) {
                throw new IllegalStateException("CookieSyncManager::createInstance() needs to be called before CookieSyncManager::getInstance()");
            }
            cookieSyncManager = b;
        }
        return cookieSyncManager;
    }

    public void startSync() {
        ao a = ao.a();
        if (a == null || !a.b()) {
            a.startSync();
            try {
                Field declaredField = Class.forName("android.webkit.WebSyncManager").getDeclaredField("mSyncThread");
                declaredField.setAccessible(true);
                ((Thread) declaredField.get(a)).setUncaughtExceptionHandler(new g());
                return;
            } catch (Exception e) {
                return;
            }
        }
        a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieSyncManager_startSync", new Class[0], new Object[0]);
    }

    public void stopSync() {
        ao a = ao.a();
        if (a == null || !a.b()) {
            a.stopSync();
        } else {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieSyncManager_stopSync", new Class[0], new Object[0]);
        }
    }

    public void sync() {
        ao a = ao.a();
        if (a == null || !a.b()) {
            a.sync();
        } else {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieSyncManager_Sync", new Class[0], new Object[0]);
        }
    }
}
