package com.tencent.smtt.sdk;

import android.os.Build.VERSION;
import android.webkit.ValueCallback;
import android.webkit.WebView;
import com.tencent.smtt.utils.g;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

public class CookieManager {
    private static CookieManager b;
    ArrayList<a> a;

    class a {
        int a;
        String b;
        String c;
        ValueCallback<Boolean> d;
        final /* synthetic */ CookieManager e;

        a(CookieManager cookieManager) {
            this.e = cookieManager;
        }
    }

    private CookieManager() {
    }

    public static synchronized CookieManager getInstance() {
        CookieManager cookieManager;
        synchronized (CookieManager.class) {
            if (b == null) {
                b = new CookieManager();
            }
            cookieManager = b;
        }
        return cookieManager;
    }

    synchronized void a() {
        if (!(this.a == null || this.a.size() == 0)) {
            ao a = ao.a();
            if (a != null && a.b()) {
                Iterator it = this.a.iterator();
                while (it.hasNext()) {
                    a aVar = (a) it.next();
                    switch (aVar.a) {
                        case 1:
                            setCookie(aVar.b, aVar.c, aVar.d);
                            break;
                        case 2:
                            setCookie(aVar.b, aVar.c);
                            break;
                        default:
                            break;
                    }
                }
                this.a.clear();
            }
        }
    }

    public boolean acceptCookie() {
        ao a = ao.a();
        return (a == null || !a.b()) ? android.webkit.CookieManager.getInstance().acceptCookie() : a.c().d();
    }

    public synchronized boolean acceptThirdPartyCookies(WebView webView) {
        boolean booleanValue;
        ao a = ao.a();
        Object invokeStaticMethod;
        if (a != null && a.b()) {
            invokeStaticMethod = a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieManager_acceptThirdPartyCookies", new Class[]{Object.class}, webView.getView());
            booleanValue = invokeStaticMethod != null ? ((Boolean) invokeStaticMethod).booleanValue() : true;
        } else if (VERSION.SDK_INT < 21) {
            booleanValue = true;
        } else {
            invokeStaticMethod = g.a(android.webkit.CookieManager.getInstance(), "acceptThirdPartyCookies", new Class[]{WebView.class}, webView.getView());
            booleanValue = invokeStaticMethod != null ? ((Boolean) invokeStaticMethod).booleanValue() : false;
        }
        return booleanValue;
    }

    public void flush() {
        ao a = ao.a();
        if (a != null && a.b()) {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieManager_flush", new Class[0], new Object[0]);
        } else if (VERSION.SDK_INT >= 21) {
            g.a(android.webkit.CookieManager.getInstance(), "flush", new Class[0], new Object[0]);
        }
    }

    public String getCookie(String str) {
        ao a = ao.a();
        return (a == null || !a.b()) ? android.webkit.CookieManager.getInstance().getCookie(str) : a.c().a(str);
    }

    public boolean hasCookies() {
        ao a = ao.a();
        return (a == null || !a.b()) ? android.webkit.CookieManager.getInstance().hasCookies() : a.c().g();
    }

    public void removeAllCookie() {
        ao a = ao.a();
        if (a == null || !a.b()) {
            android.webkit.CookieManager.getInstance().removeAllCookie();
        } else {
            a.c().e();
        }
    }

    public void removeAllCookies(ValueCallback<Boolean> valueCallback) {
        ao a = ao.a();
        if (a != null && a.b()) {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieManager_removeAllCookies", new Class[]{ValueCallback.class}, valueCallback);
        } else if (VERSION.SDK_INT >= 21) {
            g.a(android.webkit.CookieManager.getInstance(), "removeAllCookies", new Class[]{ValueCallback.class}, valueCallback);
        }
    }

    public void removeExpiredCookie() {
        ao a = ao.a();
        if (a == null || !a.b()) {
            android.webkit.CookieManager.getInstance().removeExpiredCookie();
        } else {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieManager_removeExpiredCookie", new Class[0], new Object[0]);
        }
    }

    public void removeSessionCookie() {
        ao a = ao.a();
        if (a == null || !a.b()) {
            android.webkit.CookieManager.getInstance().removeSessionCookie();
        } else {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieManager_removeSessionCookie", new Class[0], new Object[0]);
        }
    }

    public void removeSessionCookies(ValueCallback<Boolean> valueCallback) {
        ao a = ao.a();
        if (a != null && a.b()) {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieManager_removeSessionCookies", new Class[]{ValueCallback.class}, valueCallback);
        } else if (VERSION.SDK_INT >= 21) {
            g.a(android.webkit.CookieManager.getInstance(), "removeSessionCookies", new Class[]{ValueCallback.class}, valueCallback);
        }
    }

    public synchronized void setAcceptCookie(boolean z) {
        ao a = ao.a();
        if (a == null || !a.b()) {
            android.webkit.CookieManager.getInstance().setAcceptCookie(z);
        } else {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieManager_setAcceptCookie", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
        }
    }

    public synchronized void setAcceptThirdPartyCookies(WebView webView, boolean z) {
        ao a = ao.a();
        if (a != null && a.b()) {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieManager_setAcceptThirdPartyCookies", new Class[]{Object.class, Boolean.TYPE}, webView.getView(), Boolean.valueOf(z));
        } else if (VERSION.SDK_INT >= 21) {
            g.a(android.webkit.CookieManager.getInstance(), "setAcceptThirdPartyCookies", new Class[]{WebView.class, Boolean.TYPE}, webView.getView(), Boolean.valueOf(z));
        }
    }

    public synchronized void setCookie(String str, String str2) {
        ao a = ao.a();
        if (a == null || !a.b()) {
            android.webkit.CookieManager.getInstance().setCookie(str, str2);
            if (!ao.a().d()) {
                a aVar = new a(this);
                aVar.a = 2;
                aVar.b = str;
                aVar.c = str2;
                aVar.d = null;
                if (this.a == null) {
                    this.a = new ArrayList();
                }
                this.a.add(aVar);
            }
        } else {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieManager_setCookie", new Class[]{String.class, String.class}, str, str2);
        }
    }

    public synchronized void setCookie(String str, String str2, ValueCallback<Boolean> valueCallback) {
        ao a = ao.a();
        if (a == null || !a.b()) {
            if (!ao.a().d()) {
                a aVar = new a(this);
                aVar.a = 1;
                aVar.b = str;
                aVar.c = str2;
                aVar.d = valueCallback;
                if (this.a == null) {
                    this.a = new ArrayList();
                }
                this.a.add(aVar);
            }
            if (VERSION.SDK_INT >= 21) {
                g.a(android.webkit.CookieManager.getInstance(), "setCookie", new Class[]{String.class, String.class, ValueCallback.class}, str, str2, valueCallback);
            }
        } else {
            a.c().b().invokeStaticMethod("com.tencent.tbs.tbsshell.WebCoreProxy", "cookieManager_setCookie", new Class[]{String.class, String.class, ValueCallback.class}, str, str2, valueCallback);
        }
    }

    public void setCookies(Map<String, String[]> map) {
        ao a = ao.a();
        boolean a2 = (a == null || !a.b()) ? false : a.c().a((Map) map);
        if (!a2) {
            for (String str : map.keySet()) {
                for (String cookie : (String[]) map.get(str)) {
                    setCookie(str, cookie);
                }
            }
        }
    }
}
