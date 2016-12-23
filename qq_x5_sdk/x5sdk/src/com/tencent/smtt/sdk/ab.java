package com.tencent.smtt.sdk;

import android.content.Context;
import com.tencent.smtt.utils.TbsLog;
import com.tencent.smtt.utils.b;
import java.io.File;

class ab extends Thread {
    final /* synthetic */ Context a;
    final /* synthetic */ Context b;
    final /* synthetic */ z c;

    ab(z zVar, Context context, Context context2) {
        this.c = zVar;
        this.a = context;
        this.b = context2;
    }

    public void run() {
        TbsLog.i("TbsInstaller", "TbsInstaller--quickDexOptForThirdPartyApp thread start");
        try {
            File d = this.c.d(this.a);
            File d2 = this.c.d(this.b);
            b.a(d, d2, new ac(this));
            b.a(d, d2, new ad(this));
            TbsLog.i("TbsInstaller", "TbsInstaller--quickDexOptForThirdPartyApp thread done");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
