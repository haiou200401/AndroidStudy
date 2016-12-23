package com.tencent.smtt.sdk;

import android.content.Context;
import android.os.Handler;

final class d extends Thread {
    final /* synthetic */ Context a;
    final /* synthetic */ Handler b;

    d(Context context, Handler handler) {
        this.a = context;
        this.b = handler;
    }

    public void run() {
        if (z.a().a(true, this.a) == 0) {
            z.a().b(this.a, true);
        }
        f.a(true).a(this.a, false, false);
        ao a = ao.a();
        a.a(this.a);
        boolean b = a.b();
        this.b.sendEmptyMessage(3);
        if (b) {
            this.b.sendEmptyMessage(1);
        } else {
            this.b.sendEmptyMessage(2);
        }
    }
}
