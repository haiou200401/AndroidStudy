package com.tencent.smtt.sdk;

import android.net.Uri;
import android.webkit.ValueCallback;

class k implements ValueCallback<Uri[]> {
    final /* synthetic */ ValueCallback a;
    final /* synthetic */ h b;

    k(h hVar, ValueCallback valueCallback) {
        this.b = hVar;
        this.a = valueCallback;
    }

    public void a(Uri[] uriArr) {
        this.a.onReceiveValue(uriArr);
    }

    public /* synthetic */ void onReceiveValue(Object obj) {
        a((Uri[]) obj);
    }
}
