package com.tencent.smtt.sdk;

import android.content.Context;
import android.util.Log;
import com.tencent.smtt.export.external.DexLoader;
import com.tencent.smtt.sdk.TbsListener.ErrorCode;
import com.tencent.smtt.utils.TbsLog;
import java.nio.channels.FileLock;

class ao {
    private static ao a;
    private static FileLock e = null;
    private ap b;
    private boolean c;
    private boolean d;

    private ao() {
    }

    public static synchronized ao a() {
        ao aoVar;
        synchronized (ao.class) {
            if (a == null) {
                a = new ao();
            }
            aoVar = a;
        }
        return aoVar;
    }

    public ap a(boolean z) {
        return z ? this.b : c();
    }

    public synchronized void a(Context context) {
        Object obj = null;
        synchronized (this) {
            Object obj2;
            f a = f.a(true);
            a.a(context, false, false);
            StringBuilder stringBuilder = new StringBuilder();
            if (a.b()) {
                if (!this.d) {
                    this.b = new ap(a.a().a());
                    try {
                        this.c = this.b.a();
                        if (!this.c) {
                            stringBuilder.append("can not use X5 by x5corewizard return false");
                        }
                        obj2 = null;
                    } catch (NoSuchMethodException e) {
                        this.c = true;
                        obj2 = null;
                    } catch (Throwable th) {
                        obj2 = th;
                        this.c = false;
                        stringBuilder.append("can not use x5 by throwable " + Log.getStackTraceString(obj2));
                    }
                    if (this.c) {
                        CookieManager.getInstance().a();
                    }
                }
                obj2 = null;
            } else {
                this.c = false;
                stringBuilder.append("can not use X5 by !tbs available");
                obj2 = null;
            }
            if (!this.c) {
                TbsLog.e("X5CoreEngine", "mCanUseX5 is false --> report");
                if (a.b() && obj2 == null) {
                    Throwable th2;
                    try {
                        DexLoader a2 = a.a().a();
                        if (a2 != null) {
                            obj = a2.invokeStaticMethod("com.tencent.tbs.tbsshell.TBSShell", "getLoadFailureDetails", new Class[0], new Object[0]);
                        }
                        if (obj instanceof Throwable) {
                            th2 = (Throwable) obj;
                            stringBuilder.append("#" + th2.getMessage() + "; cause: " + th2.getCause() + "; th: " + th2);
                        }
                        if (obj instanceof String) {
                            stringBuilder.append("failure detail:" + obj);
                        }
                    } catch (Throwable th22) {
                        th22.printStackTrace();
                    }
                    if (stringBuilder != null) {
                        if (stringBuilder.toString().contains("isPreloadX5Disabled:-10000")) {
                            TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_CAN_NOT_DISABLED_BY_CRASH, new Throwable("X5CoreEngine::init, mCanUseX5=false, available true, details: " + stringBuilder.toString()));
                        }
                    }
                    TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_CAN_NOT_LOAD_X5, new Throwable("X5CoreEngine::init, mCanUseX5=false, available true, details: " + stringBuilder.toString()));
                } else if (a.b()) {
                    TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_CAN_NOT_USE_X5_TBS_AVAILABLE, new Throwable("mCanUseX5=false, available true, reason: " + obj2));
                } else {
                    TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_CAN_NOT_USE_X5_TBS_NOTAVAILABLE, new Throwable("mCanUseX5=false, available false, reason: " + obj2));
                }
            }
            this.d = true;
        }
    }

    public boolean b() {
        return QbSdk.a ? false : this.c;
    }

    public ap c() {
        return QbSdk.a ? null : this.b;
    }

    boolean d() {
        return this.d;
    }
}
