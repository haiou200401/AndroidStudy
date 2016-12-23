package com.tencent.smtt.sdk;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ComponentCallbacks;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Picture;
import android.graphics.Rect;
import android.net.http.SslCertificate;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebChromeClientExtension;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebSettingsExtension;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewClientExtension;
import com.tencent.smtt.export.external.extension.interfaces.IX5WebViewExtension;
import com.tencent.smtt.export.external.interfaces.IX5WebChromeClient;
import com.tencent.smtt.export.external.interfaces.IX5WebViewBase;
import com.tencent.smtt.export.external.interfaces.IX5WebViewBase.FindListener;
import com.tencent.smtt.export.external.interfaces.IX5WebViewClient;
import com.tencent.smtt.sdk.a.b;
import com.tencent.smtt.utils.TbsLog;
import com.tencent.smtt.utils.g;
import com.tencent.smtt.utils.j;
import java.io.BufferedWriter;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

public class WebView extends FrameLayout implements OnLongClickListener {
    public static final String SCHEME_GEO = "geo:0,0?q=";
    public static final String SCHEME_MAILTO = "mailto:";
    public static final String SCHEME_TEL = "tel:";
    public static String TBS_DEBUG_INSTALL_ONLINE = "tbsdebug_install_online_";
    private static Context h = null;
    private static j j = null;
    private static Method k = null;
    public static boolean mSysWebviewCreated = false;
    int a;
    private final String b;
    private boolean c;
    private IX5WebViewBase d;
    private a e;
    private WebSettings f;
    private Context g;
    private boolean i;
    private final int l;
    private final int m;
    public WebViewCallbackClient mWebViewCallbackClient;
    private final int n;
    private Object o;
    private OnLongClickListener p;

    public interface PictureListener {
        void onNewPicture(WebView webView, Picture picture);
    }

    public class WebViewTransport {
        final /* synthetic */ WebView a;
        private WebView b;

        public WebViewTransport(WebView webView) {
            this.a = webView;
        }

        public synchronized WebView getWebView() {
            return this.b;
        }

        public synchronized void setWebView(WebView webView) {
            this.b = webView;
        }
    }

    private class a extends android.webkit.WebView {
        final /* synthetic */ WebView a;

        public a(WebView webView, Context context, AttributeSet attributeSet) {
            this.a = webView;
            super(context, attributeSet);
            CookieSyncManager.createInstance(webView.g).startSync();
            try {
                Method declaredMethod = Class.forName("android.webkit.WebViewWorker").getDeclaredMethod("getHandler", new Class[0]);
                declaredMethod.setAccessible(true);
                ((Handler) declaredMethod.invoke(null, new Object[0])).getLooper().getThread().setUncaughtExceptionHandler(new g());
                WebView.mSysWebviewCreated = true;
            } catch (Exception e) {
            }
        }

        public void a() {
            super.computeScroll();
        }

        public void a(int i, int i2, int i3, int i4) {
            super.onScrollChanged(i, i2, i3, i4);
        }

        @TargetApi(9)
        public void a(int i, int i2, boolean z, boolean z2) {
            if (VERSION.SDK_INT >= 9) {
                super.onOverScrolled(i, i2, z, z2);
            }
        }

        @TargetApi(9)
        public boolean a(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
            return VERSION.SDK_INT >= 9 ? super.overScrollBy(i, i2, i3, i4, i5, i6, i7, i8, z) : false;
        }

        public boolean a(MotionEvent motionEvent) {
            return super.onTouchEvent(motionEvent);
        }

        public boolean b(MotionEvent motionEvent) {
            return super.dispatchTouchEvent(motionEvent);
        }

        public boolean c(MotionEvent motionEvent) {
            return super.onInterceptTouchEvent(motionEvent);
        }

        public void computeScroll() {
            if (this.a.mWebViewCallbackClient != null) {
                this.a.mWebViewCallbackClient.computeScroll(this);
            } else {
                super.computeScroll();
            }
        }

        public boolean dispatchTouchEvent(MotionEvent motionEvent) {
            return this.a.mWebViewCallbackClient != null ? this.a.mWebViewCallbackClient.dispatchTouchEvent(motionEvent, this) : super.dispatchTouchEvent(motionEvent);
        }

        public WebSettings getSettings() {
            try {
                return super.getSettings();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        public boolean onInterceptTouchEvent(MotionEvent motionEvent) {
            return this.a.mWebViewCallbackClient != null ? this.a.mWebViewCallbackClient.onInterceptTouchEvent(motionEvent, this) : super.onInterceptTouchEvent(motionEvent);
        }

        @TargetApi(9)
        public void onOverScrolled(int i, int i2, boolean z, boolean z2) {
            if (this.a.mWebViewCallbackClient != null) {
                this.a.mWebViewCallbackClient.onOverScrolled(i, i2, z, z2, this);
            } else if (VERSION.SDK_INT >= 9) {
                super.onOverScrolled(i, i2, z, z2);
            }
        }

        protected void onScrollChanged(int i, int i2, int i3, int i4) {
            if (this.a.mWebViewCallbackClient != null) {
                this.a.mWebViewCallbackClient.onScrollChanged(i, i2, i3, i4, this);
                return;
            }
            super.onScrollChanged(i, i2, i3, i4);
            this.a.onScrollChanged(i, i2, i3, i4);
        }

        @SuppressLint({"ClickableViewAccessibility"})
        public boolean onTouchEvent(MotionEvent motionEvent) {
            if (!hasFocus()) {
                requestFocus();
            }
            if (this.a.mWebViewCallbackClient != null) {
                return this.a.mWebViewCallbackClient.onTouchEvent(motionEvent, this);
            }
            try {
                return super.onTouchEvent(motionEvent);
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @TargetApi(9)
        public boolean overScrollBy(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
            return this.a.mWebViewCallbackClient != null ? this.a.mWebViewCallbackClient.overScrollBy(i, i2, i3, i4, i5, i6, i7, i8, z, this) : VERSION.SDK_INT >= 9 ? super.overScrollBy(i, i2, i3, i4, i5, i6, i7, i8, z) : false;
        }
    }

    public WebView(Context context) {
        this(context, null);
    }

    public WebView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, 0);
    }

    public WebView(Context context, AttributeSet attributeSet, int i) {
        this(context, attributeSet, i, false);
    }

    @TargetApi(11)
    public WebView(Context context, AttributeSet attributeSet, int i, Map<String, Object> map, boolean z) {
        super(context, attributeSet, i);
        this.b = "WebView";
        this.c = false;
        this.f = null;
        this.g = null;
        this.a = 0;
        this.i = false;
        this.l = 1;
        this.m = 2;
        this.n = 3;
        this.o = null;
        this.p = null;
        TbsLog.initIfNeed(context);
        if (context == null) {
            throw new IllegalArgumentException("Invalid context argument");
        }
        if (j == null) {
            j = j.a(context);
        }
        a(context);
        this.g = context;
        if (context != null) {
            h = context.getApplicationContext();
        }
        if (!this.c || QbSdk.a) {
            this.d = null;
            if (TbsShareManager.isThirdPartyApp(this.g)) {
                this.e = new a(this, context, attributeSet);
            }
            TbsLog.i("WebView", "SystemWebView Created Success! #2");
            this.e.setFocusableInTouchMode(true);
            addView(this.e, new LayoutParams(-1, -1));
            setDownloadListener(null);
        } else {
            this.d = ao.a().a(true).a(context);
            if (this.d == null || this.d.getView() == null) {
                TbsLog.e("WebView", "sys WebView: failed to createTBSWebview", true);
                this.d = null;
                this.c = false;
                QbSdk.a(context, "failed to createTBSWebview!");
                a(context);
                if (TbsShareManager.isThirdPartyApp(this.g)) {
                    this.e = new a(this, context, attributeSet);
                }
                TbsLog.i("WebView", "SystemWebView Created Success! #1");
                this.e.setFocusableInTouchMode(true);
                addView(this.e, new LayoutParams(-1, -1));
                try {
                    if (VERSION.SDK_INT >= 11) {
                        removeJavascriptInterface("searchBoxJavaBridge_");
                        removeJavascriptInterface("accessibility");
                        removeJavascriptInterface("accessibilityTraversal");
                        return;
                    }
                    return;
                } catch (Throwable th) {
                    th.printStackTrace();
                    return;
                }
            }
            TbsLog.i("WebView", "X5 WebView Created Success!!");
            this.d.getView().setFocusableInTouchMode(true);
            a(attributeSet);
            addView(this.d.getView(), new LayoutParams(-1, -1));
            this.d.setDownloadListener(new b(this, null, this.c));
            this.d.getX5WebViewExtension().setWebViewClientExtension(new aj(this, ao.a().a(true).j()));
        }
        try {
            if (VERSION.SDK_INT >= 11) {
                removeJavascriptInterface("searchBoxJavaBridge_");
                removeJavascriptInterface("accessibility");
                removeJavascriptInterface("accessibilityTraversal");
            }
        } catch (Throwable th2) {
            th2.printStackTrace();
        }
    }

    public WebView(Context context, AttributeSet attributeSet, int i, boolean z) {
        this(context, attributeSet, i, null, z);
    }

    private void a(Context context) {
        ao a = ao.a();
        a.a(context);
        this.c = a.b();
    }

    private void a(AttributeSet attributeSet) {
        if (attributeSet != null) {
            try {
                int attributeCount = attributeSet.getAttributeCount();
                for (int i = 0; i < attributeCount; i++) {
                    if (attributeSet.getAttributeName(i).equalsIgnoreCase("scrollbars")) {
                        int[] intArray = getResources().getIntArray(16842974);
                        int attributeIntValue = attributeSet.getAttributeIntValue(i, -1);
                        if (attributeIntValue == intArray[1]) {
                            this.d.getView().setVerticalScrollBarEnabled(false);
                            this.d.getView().setHorizontalScrollBarEnabled(false);
                        } else if (attributeIntValue == intArray[2]) {
                            this.d.getView().setVerticalScrollBarEnabled(false);
                        } else if (attributeIntValue == intArray[3]) {
                            this.d.getView().setHorizontalScrollBarEnabled(false);
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private boolean a(View view) {
        if (this.g != null && getTbsCoreVersion(this.g) > 36200) {
            return false;
        }
        Object a = g.a(this.o, "onLongClick", new Class[]{View.class}, view);
        return a != null ? ((Boolean) a).booleanValue() : false;
    }

    @Deprecated
    public static void disablePlatformNotifications() {
        if (!ao.a().b()) {
            g.a("android.webkit.WebView", "disablePlatformNotifications");
        }
    }

    @Deprecated
    public static void enablePlatformNotifications() {
        if (!ao.a().b()) {
            g.a("android.webkit.WebView", "enablePlatformNotifications");
        }
    }

    public static String findAddress(String str) {
        return !ao.a().b() ? android.webkit.WebView.findAddress(str) : null;
    }

    public static String getCrashExtraMessage(Context context) {
        return TbsShareManager.isThirdPartyApp(context) ? "nothing return ^-^." + ("tbs_core_version:" + QbSdk.getTbsVersion(context) + ";" + "tbs_sdk_version:" + 36880 + ";") : new StringBuilder().toString();
    }

    @Deprecated
    public static synchronized Object getPluginList() {
        Object a;
        synchronized (WebView.class) {
            a = !ao.a().b() ? g.a("android.webkit.WebView", "getPluginList") : null;
        }
        return a;
    }

    public static int getTbsCoreVersion(Context context) {
        return QbSdk.getTbsVersion(context);
    }

    public static int getTbsSDKVersion(Context context) {
        return 36880;
    }

    public static void setWebContentsDebuggingEnabled(boolean z) {
        if (VERSION.SDK_INT >= 19) {
            try {
                k = Class.forName("android.webkit.WebView").getDeclaredMethod("setWebContentsDebuggingEnabled", new Class[]{Boolean.TYPE});
                if (k != null) {
                    k.setAccessible(true);
                    k.invoke(null, new Object[]{Boolean.valueOf(z)});
                }
            } catch (Exception e) {
                TbsLog.e("QbSdk", "Exception:" + e.getStackTrace());
                e.printStackTrace();
            }
        }
    }

    android.webkit.WebView a() {
        return !this.c ? this.e : null;
    }

    void a(android.webkit.WebView webView) {
        if (!this.c) {
        }
    }

    void a(IX5WebViewBase iX5WebViewBase) {
        this.d = iX5WebViewBase;
    }

    public void addJavascriptInterface(Object obj, String str) {
        if (this.c) {
            this.d.addJavascriptInterface(obj, str);
        } else {
            this.e.addJavascriptInterface(obj, str);
        }
    }

    public void addView(View view) {
        if (this.c) {
            View view2 = this.d.getView();
            try {
                Method a = g.a(view2, "addView", View.class);
                a.setAccessible(true);
                a.invoke(view2, new Object[]{view});
                return;
            } catch (Throwable th) {
                return;
            }
        }
        this.e.addView(view);
    }

    IX5WebViewBase b() {
        return this.d;
    }

    public boolean canGoBack() {
        return !this.c ? this.e.canGoBack() : this.d.canGoBack();
    }

    public boolean canGoBackOrForward(int i) {
        return !this.c ? this.e.canGoBackOrForward(i) : this.d.canGoBackOrForward(i);
    }

    public boolean canGoForward() {
        return !this.c ? this.e.canGoForward() : this.d.canGoForward();
    }

    @Deprecated
    public boolean canZoomIn() {
        if (this.c) {
            return this.d.canZoomIn();
        }
        if (VERSION.SDK_INT < 11) {
            return false;
        }
        Object a = g.a(this.e, "canZoomIn");
        return a == null ? false : ((Boolean) a).booleanValue();
    }

    @Deprecated
    public boolean canZoomOut() {
        if (this.c) {
            return this.d.canZoomOut();
        }
        if (VERSION.SDK_INT < 11) {
            return false;
        }
        Object a = g.a(this.e, "canZoomOut");
        return a == null ? false : ((Boolean) a).booleanValue();
    }

    @Deprecated
    public Picture capturePicture() {
        if (this.c) {
            return this.d.capturePicture();
        }
        Object a = g.a(this.e, "capturePicture");
        return a == null ? null : (Picture) a;
    }

    public void clearCache(boolean z) {
        if (this.c) {
            this.d.clearCache(z);
        } else {
            this.e.clearCache(z);
        }
    }

    public void clearFormData() {
        if (this.c) {
            this.d.clearFormData();
        } else {
            this.e.clearFormData();
        }
    }

    public void clearHistory() {
        if (this.c) {
            this.d.clearHistory();
        } else {
            this.e.clearHistory();
        }
    }

    @TargetApi(3)
    public void clearMatches() {
        if (this.c) {
            this.d.clearMatches();
        } else {
            this.e.clearMatches();
        }
    }

    public void clearSslPreferences() {
        if (this.c) {
            this.d.clearSslPreferences();
        } else {
            this.e.clearSslPreferences();
        }
    }

    @Deprecated
    public void clearView() {
        if (this.c) {
            this.d.clearView();
        } else {
            g.a(this.e, "clearView");
        }
    }

    public int computeHorizontalScrollExtent() {
        try {
            return this.c ? ((Integer) g.a(this.d.getView(), "computeHorizontalScrollExtent", new Class[0], new Object[0])).intValue() : ((Integer) g.a(this.e, "computeHorizontalScrollExtent", new Class[0], new Object[0])).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int computeHorizontalScrollOffset() {
        try {
            return this.c ? ((Integer) g.a(this.d.getView(), "computeHorizontalScrollOffset", new Class[0], new Object[0])).intValue() : ((Integer) g.a(this.e, "computeHorizontalScrollOffset", new Class[0], new Object[0])).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public int computeHorizontalScrollRange() {
        try {
            return this.c ? ((Integer) g.a(this.d.getView(), "computeHorizontalScrollRange", new Class[0], new Object[0])).intValue() : ((Integer) g.a(this.e, "computeHorizontalScrollRange", new Class[0], new Object[0])).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void computeScroll() {
        if (this.c) {
            this.d.computeScroll();
        } else {
            this.e.computeScroll();
        }
    }

    public int computeVerticalScrollRange() {
        try {
            return this.c ? ((Integer) g.a(this.d.getView(), "computeVerticalScrollRange", new Class[0], new Object[0])).intValue() : ((Integer) g.a(this.e, "computeVerticalScrollRange", new Class[0], new Object[0])).intValue();
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public WebBackForwardList copyBackForwardList() {
        return this.c ? WebBackForwardList.a(this.d.copyBackForwardList()) : WebBackForwardList.a(this.e.copyBackForwardList());
    }

    public Object createPrintDocumentAdapter(String str) {
        Object obj = null;
        if (this.c) {
            try {
                return this.d.createPrintDocumentAdapter(str);
            } catch (Throwable th) {
                th.printStackTrace();
                return obj;
            }
        } else if (VERSION.SDK_INT < 21) {
            return obj;
        } else {
            return g.a(this.e, "createPrintDocumentAdapter", new Class[]{String.class}, str);
        }
    }

    public void destroy() {
        if (!(this.i || this.a == 0)) {
            this.i = true;
            String str = "";
            String str2 = "";
            String str3 = "";
            if (this.c) {
                Bundle sdkQBStatisticsInfo = this.d.getX5WebViewExtension().getSdkQBStatisticsInfo();
                if (sdkQBStatisticsInfo != null) {
                    str = sdkQBStatisticsInfo.getString("guid");
                    str2 = sdkQBStatisticsInfo.getString("qua2");
                    str3 = sdkQBStatisticsInfo.getString("lc");
                }
            }
            b.a(this.g, str, str2, str3, this.a, this.c);
            this.a = 0;
            this.i = false;
        }
        if (this.c) {
            this.d.destroy();
            return;
        }
        Object invoke;
        try {
            Class cls = Class.forName("android.webkit.WebViewClassic");
            Method method = cls.getMethod("fromWebView", new Class[]{android.webkit.WebView.class});
            method.setAccessible(true);
            invoke = method.invoke(null, new Object[]{this.e});
            if (invoke != null) {
                Field declaredField = cls.getDeclaredField("mListBoxDialog");
                declaredField.setAccessible(true);
                Object obj = declaredField.get(invoke);
                if (obj != null) {
                    Dialog dialog = (Dialog) obj;
                    dialog.setOnCancelListener(null);
                    Class cls2 = Class.forName("android.app.Dialog");
                    Field declaredField2 = cls2.getDeclaredField("CANCEL");
                    declaredField2.setAccessible(true);
                    int intValue = ((Integer) declaredField2.get(dialog)).intValue();
                    Field declaredField3 = cls2.getDeclaredField("mListenersHandler");
                    declaredField3.setAccessible(true);
                    ((Handler) declaredField3.get(dialog)).removeMessages(intValue);
                }
            }
        } catch (Exception e) {
        }
        this.e.destroy();
        try {
            declaredField2 = Class.forName("android.webkit.BrowserFrame").getDeclaredField("sConfigCallback");
            declaredField2.setAccessible(true);
            ComponentCallbacks componentCallbacks = (ComponentCallbacks) declaredField2.get(null);
            if (componentCallbacks != null) {
                declaredField2.set(null, null);
                declaredField2 = Class.forName("android.view.ViewRoot").getDeclaredField("sConfigCallbacks");
                declaredField2.setAccessible(true);
                invoke = declaredField2.get(null);
                if (invoke != null) {
                    List list = (List) invoke;
                    synchronized (list) {
                        list.remove(componentCallbacks);
                    }
                }
            }
        } catch (Exception e2) {
        }
    }

    public void documentHasImages(Message message) {
        if (this.c) {
            this.d.documentHasImages(message);
        } else {
            this.e.documentHasImages(message);
        }
    }

    public void dumpViewHierarchyWithProperties(BufferedWriter bufferedWriter, int i) {
        if (this.c) {
            this.d.dumpViewHierarchyWithProperties(bufferedWriter, i);
            return;
        }
        g.a(this.e, "dumpViewHierarchyWithProperties", new Class[]{BufferedWriter.class, Integer.TYPE}, bufferedWriter, Integer.valueOf(i));
    }

    public void evaluateJavascript(String str, ValueCallback<String> valueCallback) {
        Method a;
        if (this.c) {
            try {
                a = g.a(this.d.getView(), "evaluateJavascript", String.class, ValueCallback.class);
                a.setAccessible(true);
                a.invoke(this.d.getView(), new Object[]{str, valueCallback});
            } catch (Exception e) {
                e.printStackTrace();
                loadUrl(str);
            }
        } else if (VERSION.SDK_INT >= 19) {
            try {
                a = Class.forName("android.webkit.WebView").getDeclaredMethod("evaluateJavascript", new Class[]{String.class, ValueCallback.class});
                a.setAccessible(true);
                a.invoke(this.e, new Object[]{str, valueCallback});
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        }
    }

    @Deprecated
    public int findAll(String str) {
        if (this.c) {
            return this.d.findAll(str);
        }
        Object a = g.a(this.e, "findAll", new Class[]{String.class}, str);
        return a == null ? 0 : ((Integer) a).intValue();
    }

    @TargetApi(16)
    public void findAllAsync(String str) {
        if (this.c) {
            this.d.findAllAsync(str);
        } else if (VERSION.SDK_INT >= 16) {
            g.a(this.e, "findAllAsync", new Class[]{String.class}, str);
        }
    }

    public View findHierarchyView(String str, int i) {
        if (this.c) {
            return this.d.findHierarchyView(str, i);
        }
        return (View) g.a(this.e, "findHierarchyView", new Class[]{String.class, Integer.TYPE}, str, Integer.valueOf(i));
    }

    @TargetApi(3)
    public void findNext(boolean z) {
        if (this.c) {
            this.d.findNext(z);
        } else {
            this.e.findNext(z);
        }
    }

    public void flingScroll(int i, int i2) {
        if (this.c) {
            this.d.flingScroll(i, i2);
        } else {
            this.e.flingScroll(i, i2);
        }
    }

    @Deprecated
    public void freeMemory() {
        if (this.c) {
            this.d.freeMemory();
        } else {
            g.a(this.e, "freeMemory");
        }
    }

    public SslCertificate getCertificate() {
        return !this.c ? this.e.getCertificate() : this.d.getCertificate();
    }

    public int getContentHeight() {
        return !this.c ? this.e.getContentHeight() : this.d.getContentHeight();
    }

    public int getContentWidth() {
        if (this.c) {
            return this.d.getContentWidth();
        }
        Object a = g.a(this.e, "getContentWidth");
        return a == null ? 0 : ((Integer) a).intValue();
    }

    public Bitmap getFavicon() {
        return !this.c ? this.e.getFavicon() : this.d.getFavicon();
    }

    public String[] getHttpAuthUsernamePassword(String str, String str2) {
        return !this.c ? this.e.getHttpAuthUsernamePassword(str, str2) : this.d.getHttpAuthUsernamePassword(str, str2);
    }

    @TargetApi(3)
    public String getOriginalUrl() {
        return !this.c ? this.e.getOriginalUrl() : this.d.getOriginalUrl();
    }

    public int getProgress() {
        return !this.c ? this.e.getProgress() : this.d.getProgress();
    }

    @Deprecated
    public float getScale() {
        if (this.c) {
            return this.d.getScale();
        }
        Object a = g.a(this.e, "getScale");
        return a == null ? 0.0f : ((Float) a).floatValue();
    }

    public int getScrollBarDefaultDelayBeforeFade() {
        return getView().getScrollBarDefaultDelayBeforeFade();
    }

    public int getScrollBarFadeDuration() {
        return getView().getScrollBarFadeDuration();
    }

    public int getScrollBarSize() {
        return getView().getScrollBarSize();
    }

    public int getScrollBarStyle() {
        return getView().getScrollBarStyle();
    }

    public WebSettings getSettings() {
        if (this.f != null) {
            return this.f;
        }
        if (this.c) {
            WebSettings webSettings = new WebSettings(this.d.getSettings());
            this.f = webSettings;
            return webSettings;
        }
        webSettings = new WebSettings(this.e.getSettings());
        this.f = webSettings;
        return webSettings;
    }

    public IX5WebSettingsExtension getSettingsExtension() {
        return !this.c ? null : this.d.getX5WebViewExtension().getSettingsExtension();
    }

    public String getTitle() {
        return !this.c ? this.e.getTitle() : this.d.getTitle();
    }

    public String getUrl() {
        return !this.c ? this.e.getUrl() : this.d.getUrl();
    }

    public View getView() {
        return !this.c ? this.e : this.d.getView();
    }

    public int getVisibleTitleHeight() {
        if (this.c) {
            return this.d.getVisibleTitleHeight();
        }
        Object a = g.a(this.e, "getVisibleTitleHeight");
        return a == null ? 0 : ((Integer) a).intValue();
    }

    public IX5WebChromeClientExtension getWebChromeClientExtension() {
        return !this.c ? null : this.d.getX5WebViewExtension().getWebChromeClientExtension();
    }

    public int getWebScrollX() {
        return this.c ? this.d.getView().getScrollX() : this.e.getScrollX();
    }

    public int getWebScrollY() {
        return this.c ? this.d.getView().getScrollY() : this.e.getScrollY();
    }

    public IX5WebViewClientExtension getWebViewClientExtension() {
        return !this.c ? null : this.d.getX5WebViewExtension().getWebViewClientExtension();
    }

    public IX5WebViewExtension getX5WebViewExtension() {
        return !this.c ? null : this.d.getX5WebViewExtension();
    }

    @Deprecated
    public View getZoomControls() {
        return !this.c ? (View) g.a(this.e, "getZoomControls") : this.d.getZoomControls();
    }

    public void goBack() {
        if (this.c) {
            this.d.goBack();
        } else {
            this.e.goBack();
        }
    }

    public void goBackOrForward(int i) {
        if (this.c) {
            this.d.goBackOrForward(i);
        } else {
            this.e.goBackOrForward(i);
        }
    }

    public void goForward() {
        if (this.c) {
            this.d.goForward();
        } else {
            this.e.goForward();
        }
    }

    public void invokeZoomPicker() {
        if (this.c) {
            this.d.invokeZoomPicker();
        } else {
            this.e.invokeZoomPicker();
        }
    }

    public boolean isPrivateBrowsingEnabled() {
        if (this.c) {
            return this.d.isPrivateBrowsingEnable();
        }
        if (VERSION.SDK_INT < 11) {
            return false;
        }
        Object a = g.a(this.e, "isPrivateBrowsingEnabled");
        return a == null ? false : ((Boolean) a).booleanValue();
    }

    public void loadData(String str, String str2, String str3) {
        if (this.c) {
            this.d.loadData(str, str2, str3);
        } else {
            this.e.loadData(str, str2, str3);
        }
    }

    public void loadDataWithBaseURL(String str, String str2, String str3, String str4, String str5) {
        if (this.c) {
            this.d.loadDataWithBaseURL(str, str2, str3, str4, str5);
        } else {
            this.e.loadDataWithBaseURL(str, str2, str3, str4, str5);
        }
    }

    public void loadUrl(String str) {
        if (this.c) {
            this.d.loadUrl(str);
        } else {
            this.e.loadUrl(str);
        }
    }

    @TargetApi(8)
    public void loadUrl(String str, Map<String, String> map) {
        if (this.c) {
            this.d.loadUrl(str, map);
        } else if (VERSION.SDK_INT >= 8) {
            this.e.loadUrl(str, map);
        }
    }

    protected void onDetachedFromWindow() {
        if (!(this.i || this.a == 0)) {
            this.i = true;
            String str = "";
            String str2 = "";
            String str3 = "";
            if (this.c) {
                Bundle sdkQBStatisticsInfo = this.d.getX5WebViewExtension().getSdkQBStatisticsInfo();
                if (sdkQBStatisticsInfo != null) {
                    str = sdkQBStatisticsInfo.getString("guid");
                    str2 = sdkQBStatisticsInfo.getString("qua2");
                    str3 = sdkQBStatisticsInfo.getString("lc");
                }
            }
            b.a(this.g, str, str2, str3, this.a, this.c);
            this.a = 0;
            this.i = false;
        }
        super.onDetachedFromWindow();
    }

    public boolean onLongClick(View view) {
        return this.p != null ? !this.p.onLongClick(view) ? a(view) : true : a(view);
    }

    public void onPause() {
        if (this.c) {
            this.d.onPause();
        } else {
            g.a(this.e, "onPause");
        }
    }

    public void onResume() {
        if (this.c) {
            this.d.onResume();
        } else {
            g.a(this.e, "onResume");
        }
    }

    protected void onVisibilityChanged(View view, int i) {
        if (this.g == null) {
            super.onVisibilityChanged(view, i);
            return;
        }
        if (!(i == 0 || this.i || this.a == 0)) {
            this.i = true;
            String str = "";
            String str2 = "";
            String str3 = "";
            if (this.c) {
                Bundle sdkQBStatisticsInfo = this.d.getX5WebViewExtension().getSdkQBStatisticsInfo();
                if (sdkQBStatisticsInfo != null) {
                    str = sdkQBStatisticsInfo.getString("guid");
                    str2 = sdkQBStatisticsInfo.getString("qua2");
                    str3 = sdkQBStatisticsInfo.getString("lc");
                }
            }
            b.a(this.g, str, str2, str3, this.a, this.c);
            this.a = 0;
            this.i = false;
        }
        super.onVisibilityChanged(view, i);
    }

    public boolean overlayHorizontalScrollbar() {
        return !this.c ? this.e.overlayHorizontalScrollbar() : this.d.overlayHorizontalScrollbar();
    }

    public boolean overlayVerticalScrollbar() {
        return this.c ? this.d.overlayVerticalScrollbar() : this.e.overlayVerticalScrollbar();
    }

    public boolean pageDown(boolean z) {
        return !this.c ? this.e.pageDown(z) : this.d.pageDown(z, -1);
    }

    public boolean pageUp(boolean z) {
        return !this.c ? this.e.pageUp(z) : this.d.pageUp(z, -1);
    }

    public void pauseTimers() {
        if (this.c) {
            this.d.pauseTimers();
        } else {
            this.e.pauseTimers();
        }
    }

    @TargetApi(5)
    public void postUrl(String str, byte[] bArr) {
        if (this.c) {
            this.d.postUrl(str, bArr);
        } else {
            this.e.postUrl(str, bArr);
        }
    }

    @Deprecated
    public void refreshPlugins(boolean z) {
        if (this.c) {
            this.d.refreshPlugins(z);
            return;
        }
        g.a(this.e, "refreshPlugins", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
    }

    public void reload() {
        if (this.c) {
            this.d.reload();
        } else {
            this.e.reload();
        }
    }

    @TargetApi(11)
    public void removeJavascriptInterface(String str) {
        if (!this.c) {
            if (VERSION.SDK_INT >= 11) {
                g.a(this.e, "removeJavascriptInterface", new Class[]{String.class}, str);
                return;
            }
            this.d.removeJavascriptInterface(str);
        }
    }

    public void removeView(View view) {
        if (this.c) {
            View view2 = this.d.getView();
            try {
                Method a = g.a(view2, "removeView", View.class);
                a.setAccessible(true);
                a.invoke(view2, new Object[]{view});
                return;
            } catch (Throwable th) {
                return;
            }
        }
        this.e.removeView(view);
    }

    public boolean requestChildRectangleOnScreen(View view, Rect rect, boolean z) {
        if (this.c) {
            View view2 = this.d.getView();
            if (!(view2 instanceof ViewGroup)) {
                return false;
            }
            ViewGroup viewGroup = (ViewGroup) view2;
            if (view != this) {
                view2 = view;
            }
            return viewGroup.requestChildRectangleOnScreen(view2, rect, z);
        }
        android.webkit.WebView webView = this.e;
        if (view == this) {
            view = this.e;
        }
        return webView.requestChildRectangleOnScreen(view, rect, z);
    }

    public void requestFocusNodeHref(Message message) {
        if (this.c) {
            this.d.requestFocusNodeHref(message);
        } else {
            this.e.requestFocusNodeHref(message);
        }
    }

    public void requestImageRef(Message message) {
        if (this.c) {
            this.d.requestImageRef(message);
        } else {
            this.e.requestImageRef(message);
        }
    }

    @Deprecated
    public boolean restorePicture(Bundle bundle, File file) {
        if (this.c) {
            return this.d.restorePicture(bundle, file);
        }
        Object a = g.a(this.e, "restorePicture", new Class[]{Bundle.class, File.class}, bundle, file);
        return a == null ? false : ((Boolean) a).booleanValue();
    }

    public WebBackForwardList restoreState(Bundle bundle) {
        return !this.c ? WebBackForwardList.a(this.e.restoreState(bundle)) : WebBackForwardList.a(this.d.restoreState(bundle));
    }

    public void resumeTimers() {
        if (this.c) {
            this.d.resumeTimers();
        } else {
            this.e.resumeTimers();
        }
    }

    @Deprecated
    public void savePassword(String str, String str2, String str3) {
        if (this.c) {
            this.d.savePassword(str, str2, str3);
            return;
        }
        g.a(this.e, "savePassword", new Class[]{String.class, String.class, String.class}, str, str2, str3);
    }

    @Deprecated
    public boolean savePicture(Bundle bundle, File file) {
        if (this.c) {
            return this.d.savePicture(bundle, file);
        }
        Object a = g.a(this.e, "savePicture", new Class[]{Bundle.class, File.class}, bundle, file);
        return a == null ? false : ((Boolean) a).booleanValue();
    }

    public WebBackForwardList saveState(Bundle bundle) {
        return !this.c ? WebBackForwardList.a(this.e.saveState(bundle)) : WebBackForwardList.a(this.d.saveState(bundle));
    }

    @TargetApi(11)
    public void saveWebArchive(String str) {
        if (this.c) {
            this.d.saveWebArchive(str);
        } else if (VERSION.SDK_INT >= 11) {
            g.a(this.e, "saveWebArchive", new Class[]{String.class}, str);
        }
    }

    @TargetApi(11)
    public void saveWebArchive(String str, boolean z, ValueCallback<String> valueCallback) {
        if (this.c) {
            this.d.saveWebArchive(str, z, valueCallback);
        } else if (VERSION.SDK_INT >= 11) {
            g.a(this.e, "saveWebArchive", new Class[]{String.class, Boolean.TYPE, ValueCallback.class}, str, Boolean.valueOf(z), valueCallback);
        }
    }

    public void setBackgroundColor(int i) {
        if (this.c) {
            this.d.setBackgroundColor(i);
        } else {
            this.e.setBackgroundColor(i);
        }
        super.setBackgroundColor(i);
    }

    @Deprecated
    public void setCertificate(SslCertificate sslCertificate) {
        if (this.c) {
            this.d.setCertificate(sslCertificate);
        } else {
            this.e.setCertificate(sslCertificate);
        }
    }

    public void setDownloadListener(DownloadListener downloadListener) {
        if (this.c) {
            this.d.setDownloadListener(new b(this, downloadListener, this.c));
        } else {
            this.e.setDownloadListener(new al(this, downloadListener));
        }
    }

    @TargetApi(16)
    public void setFindListener(FindListener findListener) {
        if (this.c) {
            this.d.setFindListener(findListener);
        } else if (VERSION.SDK_INT >= 16) {
            this.e.setFindListener(new ak(this, findListener));
        }
    }

    public void setHorizontalScrollbarOverlay(boolean z) {
        if (this.c) {
            this.d.setHorizontalScrollbarOverlay(z);
        } else {
            this.e.setHorizontalScrollbarOverlay(z);
        }
    }

    public void setHttpAuthUsernamePassword(String str, String str2, String str3, String str4) {
        if (this.c) {
            this.d.setHttpAuthUsernamePassword(str, str2, str3, str4);
        } else {
            this.e.setHttpAuthUsernamePassword(str, str2, str3, str4);
        }
    }

    public void setInitialScale(int i) {
        if (this.c) {
            this.d.setInitialScale(i);
        } else {
            this.e.setInitialScale(i);
        }
    }

    @Deprecated
    public void setMapTrackballToArrowKeys(boolean z) {
        if (this.c) {
            this.d.setMapTrackballToArrowKeys(z);
            return;
        }
        g.a(this.e, "setMapTrackballToArrowKeys", new Class[]{Boolean.TYPE}, Boolean.valueOf(z));
    }

    public void setNetworkAvailable(boolean z) {
        if (this.c) {
            this.d.setNetworkAvailable(z);
        } else if (VERSION.SDK_INT >= 3) {
            this.e.setNetworkAvailable(z);
        }
    }

    public void setOnLongClickListener(OnLongClickListener onLongClickListener) {
        if (this.c) {
            View view = this.d.getView();
            try {
                if (this.o == null) {
                    Method a = g.a(view, "getListenerInfo", new Class[0]);
                    a.setAccessible(true);
                    Object invoke = a.invoke(view, (Object[]) null);
                    Field declaredField = invoke.getClass().getDeclaredField("mOnLongClickListener");
                    declaredField.setAccessible(true);
                    this.o = declaredField.get(invoke);
                }
                this.p = onLongClickListener;
                getView().setOnLongClickListener(this);
                return;
            } catch (Throwable th) {
                return;
            }
        }
        this.e.setOnLongClickListener(onLongClickListener);
    }

    public void setOnTouchListener(OnTouchListener onTouchListener) {
        getView().setOnTouchListener(onTouchListener);
    }

    public void setPictureListener(PictureListener pictureListener) {
        if (this.c) {
            if (pictureListener == null) {
                this.d.setPictureListener(null);
            } else {
                this.d.setPictureListener(new an(this, pictureListener));
            }
        } else if (pictureListener == null) {
            this.e.setPictureListener(null);
        } else {
            this.e.setPictureListener(new am(this, pictureListener));
        }
    }

    public void setScrollBarStyle(int i) {
        if (this.c) {
            this.d.getView().setScrollBarStyle(i);
        } else {
            this.e.setScrollBarStyle(i);
        }
    }

    public void setVerticalScrollbarOverlay(boolean z) {
        if (this.c) {
            this.d.setVerticalScrollbarOverlay(z);
        } else {
            this.e.setVerticalScrollbarOverlay(z);
        }
    }

    public void setVisibility(int i) {
        super.setVisibility(i);
        getView().setVisibility(i);
    }

    public void setWebChromeClient(WebChromeClient webChromeClient) {
        WebChromeClient webChromeClient2 = null;
        if (this.c) {
            IX5WebChromeClient hVar;
            IX5WebViewBase iX5WebViewBase = this.d;
            if (webChromeClient != null) {
                hVar = new h(ao.a().a(true).h(), this, webChromeClient);
            }
            iX5WebViewBase.setWebChromeClient(hVar);
            return;
        }
        android.webkit.WebView webView = this.e;
        if (webChromeClient != null) {
            webChromeClient2 = new SystemWebChromeClient(this, webChromeClient);
        }
        webView.setWebChromeClient(webChromeClient2);
    }

    public void setWebChromeClientExtension(IX5WebChromeClientExtension iX5WebChromeClientExtension) {
        if (this.c) {
            this.d.getX5WebViewExtension().setWebChromeClientExtension(iX5WebChromeClientExtension);
        }
    }

    public void setWebViewCallbackClient(WebViewCallbackClient webViewCallbackClient) {
        this.mWebViewCallbackClient = webViewCallbackClient;
        if (this.c && getX5WebViewExtension() != null) {
            Bundle bundle = new Bundle();
            bundle.putBoolean("flag", true);
            getX5WebViewExtension().invokeMiscMethod("setWebViewCallbackClientFlag", bundle);
        }
    }

    public void setWebViewClient(WebViewClient webViewClient) {
        WebViewClient webViewClient2 = null;
        if (this.c) {
            IX5WebViewClient mVar;
            IX5WebViewBase iX5WebViewBase = this.d;
            if (webViewClient != null) {
                mVar = new m(ao.a().a(true).i(), this, webViewClient);
            }
            iX5WebViewBase.setWebViewClient(mVar);
            return;
        }
        android.webkit.WebView webView = this.e;
        if (webViewClient != null) {
            webViewClient2 = new t(this, webViewClient);
        }
        webView.setWebViewClient(webViewClient2);
    }

    public void setWebViewClientExtension(IX5WebViewClientExtension iX5WebViewClientExtension) {
        if (this.c) {
            this.d.getX5WebViewExtension().setWebViewClientExtension(iX5WebViewClientExtension);
        }
    }

    public boolean showFindDialog(String str, boolean z) {
        return false;
    }

    public void stopLoading() {
        if (this.c) {
            this.d.stopLoading();
        } else {
            this.e.stopLoading();
        }
    }

    public void super_computeScroll() {
        if (this.c) {
            try {
                g.a(this.d.getView(), "super_computeScroll");
                return;
            } catch (Throwable th) {
                th.printStackTrace();
                return;
            }
        }
        this.e.a();
    }

    public boolean super_dispatchTouchEvent(MotionEvent motionEvent) {
        if (!this.c) {
            return this.e.b(motionEvent);
        }
        try {
            Object a = g.a(this.d.getView(), "super_dispatchTouchEvent", new Class[]{MotionEvent.class}, motionEvent);
            return a == null ? false : ((Boolean) a).booleanValue();
        } catch (Throwable th) {
            return false;
        }
    }

    public boolean super_onInterceptTouchEvent(MotionEvent motionEvent) {
        if (!this.c) {
            return this.e.c(motionEvent);
        }
        try {
            Object a = g.a(this.d.getView(), "super_onInterceptTouchEvent", new Class[]{MotionEvent.class}, motionEvent);
            return a == null ? false : ((Boolean) a).booleanValue();
        } catch (Throwable th) {
            return false;
        }
    }

    public void super_onOverScrolled(int i, int i2, boolean z, boolean z2) {
        if (this.c) {
            try {
                g.a(this.d.getView(), "super_onOverScrolled", new Class[]{Integer.TYPE, Integer.TYPE, Boolean.TYPE, Boolean.TYPE}, Integer.valueOf(i), Integer.valueOf(i2), Boolean.valueOf(z), Boolean.valueOf(z2));
                return;
            } catch (Throwable th) {
                th.printStackTrace();
                return;
            }
        }
        this.e.a(i, i2, z, z2);
    }

    public void super_onScrollChanged(int i, int i2, int i3, int i4) {
        if (this.c) {
            try {
                g.a(this.d.getView(), "super_onScrollChanged", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE}, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4));
                return;
            } catch (Throwable th) {
                th.printStackTrace();
                return;
            }
        }
        this.e.a(i, i2, i3, i4);
    }

    public boolean super_onTouchEvent(MotionEvent motionEvent) {
        if (!this.c) {
            return this.e.a(motionEvent);
        }
        try {
            Object a = g.a(this.d.getView(), "super_onTouchEvent", new Class[]{MotionEvent.class}, motionEvent);
            return a == null ? false : ((Boolean) a).booleanValue();
        } catch (Throwable th) {
            return false;
        }
    }

    public boolean super_overScrollBy(int i, int i2, int i3, int i4, int i5, int i6, int i7, int i8, boolean z) {
        if (!this.c) {
            return this.e.a(i, i2, i3, i4, i5, i6, i7, i8, z);
        }
        try {
            Object a = g.a(this.d.getView(), "super_overScrollBy", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Integer.TYPE, Boolean.TYPE}, Integer.valueOf(i), Integer.valueOf(i2), Integer.valueOf(i3), Integer.valueOf(i4), Integer.valueOf(i5), Integer.valueOf(i6), Integer.valueOf(i7), Integer.valueOf(i8), Boolean.valueOf(z));
            return a == null ? false : ((Boolean) a).booleanValue();
        } catch (Throwable th) {
            return false;
        }
    }

    public boolean zoomIn() {
        return !this.c ? this.e.zoomIn() : this.d.zoomIn();
    }

    public boolean zoomOut() {
        return !this.c ? this.e.zoomOut() : this.d.zoomOut();
    }
}
