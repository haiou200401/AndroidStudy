package com.tencent.smtt.sdk;

import android.webkit.ValueCallback;
import java.util.Map;

public class WebStorage {
    private static WebStorage a;

    @Deprecated
    public interface QuotaUpdater {
        void updateQuota(long j);
    }

    private static synchronized WebStorage a() {
        WebStorage webStorage;
        synchronized (WebStorage.class) {
            if (a == null) {
                a = new WebStorage();
            }
            webStorage = a;
        }
        return webStorage;
    }

    public static WebStorage getInstance() {
        return a();
    }

    public void deleteAllData() {
        ao a = ao.a();
        if (a == null || !a.b()) {
            android.webkit.WebStorage.getInstance().deleteAllData();
        } else {
            a.c().m();
        }
    }

    public void deleteOrigin(String str) {
        ao a = ao.a();
        if (a == null || !a.b()) {
            android.webkit.WebStorage.getInstance().deleteOrigin(str);
        } else {
            a.c().e(str);
        }
    }

    public void getOrigins(ValueCallback<Map> valueCallback) {
        ao a = ao.a();
        if (a == null || !a.b()) {
            android.webkit.WebStorage.getInstance().getOrigins(valueCallback);
        } else {
            a.c().a((ValueCallback) valueCallback);
        }
    }

    public void getQuotaForOrigin(String str, ValueCallback<Long> valueCallback) {
        ao a = ao.a();
        if (a == null || !a.b()) {
            android.webkit.WebStorage.getInstance().getQuotaForOrigin(str, valueCallback);
        } else {
            a.c().b(str, valueCallback);
        }
    }

    public void getUsageForOrigin(String str, ValueCallback<Long> valueCallback) {
        ao a = ao.a();
        if (a == null || !a.b()) {
            android.webkit.WebStorage.getInstance().getUsageForOrigin(str, valueCallback);
        } else {
            a.c().a(str, (ValueCallback) valueCallback);
        }
    }

    @Deprecated
    public void setQuotaForOrigin(String str, long j) {
        ao a = ao.a();
        if (a == null || !a.b()) {
            android.webkit.WebStorage.getInstance().setQuotaForOrigin(str, j);
        } else {
            a.c().a(str, j);
        }
    }
}
