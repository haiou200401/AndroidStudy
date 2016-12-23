package com.tencent.smtt.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.tencent.smtt.sdk.QbSdk.PreInitCallback;

final class c extends Handler {
    final /* synthetic */ PreInitCallback a;
    final /* synthetic */ Context b;

    c(Looper looper, PreInitCallback preInitCallback, Context context) {
        this.a = preInitCallback;
        this.b = context;
        super(looper);
    }

    public void handleMessage(Message message) {
        switch (message.what) {
            case 1:
                ap c = ao.a().c();
                if (c != null) {
                    c.a(this.b);
                }
                if (this.a != null) {
                    this.a.onViewInitFinished(true);
                    return;
                }
                return;
            case 2:
                if (this.a != null) {
                    this.a.onViewInitFinished(false);
                    return;
                }
                return;
            case 3:
                if (this.a != null) {
                    this.a.onCoreInitFinished();
                    return;
                }
                return;
            default:
                return;
        }
    }
}
