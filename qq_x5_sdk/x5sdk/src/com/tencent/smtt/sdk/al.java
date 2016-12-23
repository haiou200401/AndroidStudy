package com.tencent.smtt.sdk;

import android.webkit.DownloadListener;

class al implements DownloadListener {
    final /* synthetic */ DownloadListener a;
    final /* synthetic */ WebView b;

    al(WebView webView, DownloadListener downloadListener) {
        this.b = webView;
        this.a = downloadListener;
    }

    public void onDownloadStart(String str, String str2, String str3, String str4, long j) {
        if (this.a != null) {
            this.a.onDownloadStart(str, str2, str3, str4, j);
        }
    }
}
