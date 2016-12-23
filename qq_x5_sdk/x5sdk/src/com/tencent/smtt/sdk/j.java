package com.tencent.smtt.sdk;

import android.net.Uri;
import android.webkit.ValueCallback;

class j implements ValueCallback<Uri> {
    final /* synthetic */ ValueCallback a;
    final /* synthetic */ h b;

    j(h hVar, ValueCallback valueCallback) {
        this.b = hVar;
        this.a = valueCallback;
    }

    public void a(Uri uri) {
        this.a.onReceiveValue(new Uri[]{uri});
    }

    public /* synthetic */ void onReceiveValue(Object obj) {
        a((Uri) obj);
    }
}
