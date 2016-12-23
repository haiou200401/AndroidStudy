package com.tencent.smtt.sdk;

import android.content.Context;
import android.text.TextUtils;
import com.tencent.smtt.export.external.DexLoader;
import com.tencent.smtt.export.external.libwebp;
import com.tencent.smtt.sdk.TbsListener.ErrorCode;
import com.tencent.smtt.utils.TbsLog;
import java.util.Map;

class ag {
    private Context a = null;
    private Context b = null;
    private String c = null;
    private String[] d = null;
    private DexLoader e = null;
    private String f = "TbsDexOpt";
    private String g = null;

    public ag(Context context, Context context2, String str, String str2, String[] strArr, String str3) {
        TbsLog.i("TbsWizard", "construction start...");
        if (context == null || context2 == null || TextUtils.isEmpty(str) || strArr == null || strArr.length == 0) {
//            throw new Exception("TbsWizard paramter error:-1callerContext:" + context + "hostcontext" + context2 + "isEmpty" + TextUtils.isEmpty(str) + "dexfileList" + strArr);
        }
        this.a = context.getApplicationContext();
        this.b = context2;
        this.c = str;
        this.d = strArr;
        this.f = str2;
        this.e = new DexLoader(str3, this.a, this.d, str2);
        libwebp.loadWepLibraryIfNeed(context2, this.c);
        if ("com.nd.android.pandahome2".equals(this.a.getApplicationInfo().packageName)) {
            this.e.invokeStaticMethod("com.tencent.tbs.common.beacon.X5CoreBeaconUploader", "getInstance", new Class[]{Context.class}, this.a);
        }
        if (QbSdk.f != null) {
            this.e.invokeStaticMethod("com.tencent.tbs.tbsshell.TBSShell", "initTbsSettings", new Class[]{Map.class}, QbSdk.f);
        }
        int a = a(context);
        if (a < 0) {
//            throw new Exception("TbsWizard init error: " + a + "; msg: " + this.g);
        }
        TbsLog.i("TbsWizard", "construction end...");
    }

    private int a(Context context) {
        int i;
        Object invokeStaticMethod = this.e.invokeStaticMethod("com.tencent.tbs.tbsshell.TBSShell", "initTesRuntimeEnvironment", new Class[]{Context.class, Context.class, DexLoader.class, String.class, String.class, String.class, Integer.TYPE, String.class}, context, this.b, this.e, this.c, this.f, TbsConfig.TBS_SDK_VERSIONNAME, Integer.valueOf(36880), QbSdk.a());
        if (invokeStaticMethod == null) {
            b();
            c();
            invokeStaticMethod = this.e.invokeStaticMethod("com.tencent.tbs.tbsshell.TBSShell", "initTesRuntimeEnvironment", new Class[]{Context.class, Context.class, DexLoader.class, String.class, String.class}, context, this.b, this.e, this.c, this.f);
        }
        if (invokeStaticMethod == null) {
            i = -3;
        } else if (invokeStaticMethod instanceof Integer) {
            i = ((Integer) invokeStaticMethod).intValue();
        } else if (invokeStaticMethod instanceof Throwable) {
            TbsCoreLoadStat.getInstance().a(this.a, ErrorCode.THROWABLE_INITTESRUNTIMEENVIRONMENT, (Throwable) invokeStaticMethod);
            i = -5;
        } else {
            i = -4;
        }
        if (i < 0) {
            Object invokeStaticMethod2 = this.e.invokeStaticMethod("com.tencent.tbs.tbsshell.TBSShell", "getLoadFailureDetails", new Class[0], new Object[0]);
            if (invokeStaticMethod2 instanceof Throwable) {
                Throwable th = (Throwable) invokeStaticMethod2;
                this.g = "#" + th.getMessage() + "; cause: " + th.getCause() + "; th: " + th;
            }
            if (invokeStaticMethod2 instanceof String) {
                this.g = (String) invokeStaticMethod2;
            }
        } else {
            this.g = null;
        }
        return i;
    }

    private void b() {
        this.e.invokeStaticMethod("com.tencent.tbs.tbsshell.TBSShell", "setTesSdkVersionName", new Class[]{String.class}, TbsConfig.TBS_SDK_VERSIONNAME);
    }

    private void c() {
        this.e.setStaticField("com.tencent.tbs.tbsshell.TBSShell", "VERSION", Integer.valueOf(36880));
    }

    public DexLoader a() {
        return this.e;
    }
}
