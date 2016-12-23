package com.tencent.smtt.sdk;

import com.tencent.smtt.export.external.interfaces.DownloadListener;

class b implements DownloadListener {
    private DownloadListener a;
    private WebView b;

    public b(WebView webView, DownloadListener downloadListener, boolean z) {
        this.a = downloadListener;
        this.b = webView;
    }

    public void onDownloadStart(String str, String str2, String str3, String str4, long j) {
        onDownloadStart(str, null, null, str2, str3, str4, j, null, null);
    }

    public void onDownloadStart(String str, String str2, byte[] bArr, String str3, String str4, String str5, long j, String str6, String str7) {
        if (this.a != null) {
            this.a.onDownloadStart(str, str3, str4, str5, j);
        }
    }

    public void onDownloadVideo(String str, long j, int i) {
    }
}
