package com.tencent.smtt.sdk;

import android.content.Context;
import android.os.Build.VERSION;
import com.tencent.smtt.sdk.TbsListener.ErrorCode;
import com.tencent.smtt.utils.TbsLog;
import java.io.File;

class f {
    static boolean a = false;
    private static f c = null;
    private static int f = 0;
    private static int g = 0;
    private static int h = 3;
    private static String j = null;
    private ag b = null;
    private boolean d = false;
    private boolean e = false;
    private File i = null;

    private f() {
    }

    public static synchronized f a(boolean z) {
        f fVar;
        synchronized (f.class) {
            if (c == null && z) {
                c = new f();
            }
            fVar = c;
        }
        return fVar;
    }

    static void a(int i) {
        f = i;
    }

    public static int c() {
        return f;
    }

    public ag a() {
        return this.d ? this.b : null;
    }

    public synchronized void a(Context context, boolean z, boolean z2) {
        Context context2 = null;
        int i = 1;
        int i2 = 0;
        synchronized (this) {
            TbsLog.addLog(TbsLog.TBSLOG_CODE_SDK_INIT, null, new Object[0]);
            TbsLog.initIfNeed(context);
            g++;
            TbsCoreLoadStat.getInstance().a();
            TbsShareManager.forceToLoadX5ForThirdApp(context, true);
            boolean a = QbSdk.a(context, z, z2);
            boolean z3 = VERSION.SDK_INT >= 7;
            if (!(a && z3)) {
                i = 0;
            }
            if (i == 0) {
                String str = "can_load_x5=" + a + "; is_compatible=" + z3;
                TbsLog.e("SDKEngine", "SDKEngine.init canLoadTbs=false; failure: " + str);
                if (!(QbSdk.a && this.d)) {
                    this.d = false;
                    TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_CAN_NOT_LOAD_TBS, new Throwable(str));
                }
                this.i = z.f(context);
                this.e = true;
            } else if (!this.d) {
                try {
                    File file;
                    File d;
                    if (TbsShareManager.isThirdPartyApp(context)) {
                        TbsLog.addLog(TbsLog.TBSLOG_CODE_SDK_THIRD_MODE, null, new Object[0]);
                        if (TbsShareManager.g(context)) {
                            file = new File(TbsShareManager.a(context));
                            d = z.a().d(context);
                            context2 = TbsShareManager.c(context);
                            if (d == null) {
                                this.d = false;
                                QbSdk.a(context, "SDKEngine::useSystemWebView by error_tbs_core_dexopt_dir null!");
                            }
                        } else {
                            this.d = false;
                            QbSdk.a(context, "SDKEngine::useSystemWebView by error_host_unavailable");
                        }
                    } else {
                        d = null;
                        file = null;
                    }
                    String[] dexLoaderFileList = QbSdk.getDexLoaderFileList(context, context2, file.getAbsolutePath());
                    while (i2 < dexLoaderFileList.length) {
                        i2++;
                    }
                    this.b = new ag(context, context2, file.getAbsolutePath(), d.getAbsolutePath(), dexLoaderFileList, QbSdk.d);
                    this.d = true;
                } catch (Throwable th) {
                    TbsLog.e("SDKEngine", "useSystemWebView by exception: " + th);
                    if (th == null) {
                        TbsCoreLoadStat.getInstance().a(context, ErrorCode.TEST_THROWABLE_IS_NULL);
                    } else {
                        TbsCoreLoadStat.getInstance().a(context, ErrorCode.TEST_THROWABLE_ISNOT_NULL, th);
                    }
                    this.d = false;
                    QbSdk.a(context, "SDKEngine::useSystemWebView by exception: " + th);
                }
                this.i = z.f(context);
                this.e = true;
            }
        }
    }

    public boolean b() {
        return this.d;
    }

    boolean d() {
        return this.e;
    }
}
