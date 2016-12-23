package com.tencent.smtt.sdk;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import com.tencent.smtt.sdk.TbsListener.ErrorCode;
import com.tencent.smtt.utils.TbsLog;
import com.tencent.smtt.utils.TbsLogClient;
import com.tencent.smtt.utils.g;
import dalvik.system.DexClassLoader;
import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Map;

@SuppressLint({"NewApi"})
public class QbSdk {
    public static final int SVNVERSION = 420812;
    public static final int VERSION = 1;
    static boolean a = false;
    static boolean b = false;
    static boolean c = true;
    static String d;
    static volatile boolean e = a;
    static Map<String, Object> f = null;
    private static int g = 0;
    private static String h = "";
    private static Class<?> i;
    private static Object j;
    private static boolean k = false;
    private static String[] l;
    private static String m = "NULL";
    private static String n = "UNKNOWN";
    private static boolean o = true;

    public interface PreInitCallback {
        void onCoreInitFinished();

        void onViewInitFinished(boolean z);
    }

    static String a() {
        return h;
    }

    static synchronized void a(Context context, String str) {
        synchronized (QbSdk.class) {
            if (!a) {
                a = true;
                n = "forceSysWebViewInner: " + str;
                TbsLog.e("QbSdk", "QbSdk.SysWebViewForcedInner..." + n);
                TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_FORCE_SYSTEM_WEBVIEW_INNER, new Throwable(n));
            }
        }
    }

    private static boolean a(Context context) {
        try {
            if (i != null) {
                return true;
            }
            File d = z.a().d(context);
            if (d == null) {
                TbsLog.e("QbSdk", "QbSdk initForX5DisableConfig (false) optDir == null");
                return false;
            }
            File file = null;
            if (TbsShareManager.isThirdPartyApp(context)) {
                if (TbsShareManager.g(context)) {
                    file = new File(TbsShareManager.a(context), "tbs_sdk_extension_dex.jar");
                } else {
                    TbsCoreLoadStat.getInstance().a(context, ErrorCode.ERROR_HOST_UNAVAILABLE);
                    return false;
                }
            }
            if (file.exists()) {
                i = new DexClassLoader(file.getAbsolutePath(), d.getAbsolutePath(), file.getAbsolutePath(), QbSdk.class.getClassLoader()).loadClass("com.tencent.tbs.sdk.extension.TbsSDKExtension");
                Constructor constructor = i.getConstructor(new Class[]{Context.class, Context.class});
                if (TbsShareManager.isThirdPartyApp(context)) {
                    j = constructor.newInstance(new Object[]{context, TbsShareManager.c(context)});
                } else {
                    j = constructor.newInstance(new Object[]{context, context});
                }
                g.a(j, "setClientVersion", new Class[]{Integer.TYPE}, Integer.valueOf(1));
                return true;
            }
            TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_MISS_SDKEXTENSION_JAR_OLD, new Exception("initForX5DisableConfig failure -- tbs_sdk_extension_dex.jar is not exist!"));
            return false;
        } catch (Throwable th) {
            TbsLog.e("QbSdk", "initForX5DisableConfig sys WebView: " + Log.getStackTraceString(th));
            return false;
        }
    }

    static boolean a(Context context, int i) {
        return a(context, i, 20000);
    }

    static boolean a(Context context, int i, int i2) {
        if (!a(context)) {
            return true;
        }
        Object a = g.a(j, "isX5Disabled", new Class[]{Integer.TYPE, Integer.TYPE, Integer.TYPE}, Integer.valueOf(i), Integer.valueOf(36880), Integer.valueOf(i2));
        if (a != null) {
            return ((Boolean) a).booleanValue();
        }
        a = g.a(j, "isX5Disabled", new Class[]{Integer.TYPE, Integer.TYPE}, Integer.valueOf(i), Integer.valueOf(36880));
        return a != null ? ((Boolean) a).booleanValue() : true;
    }

    @SuppressLint({"NewApi"})
    private static boolean a(Context context, boolean z) {
        File file = null;
        TbsLog.initIfNeed(context);
        if (a && !z) {
            TbsLog.e("QbSdk", "QbSdk init: " + n, false);
            TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_SDKINIT_IS_SYS_FORCED, new Throwable(n));
            return false;
        } else if (b) {
            TbsLog.e("QbSdk", "QbSdk init mIsSysWebViewForcedByOuter = true", true);
            TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_FORCE_SYSTEM_WEBVIEW_OUTER, new Throwable(m));
            return false;
        } else {
            try {
                File d = z.a().d(context);
                if (d == null) {
                    TbsLog.e("QbSdk", "QbSdk init (false) optDir == null");
                    TbsCoreLoadStat.getInstance().a(context, ErrorCode.ERROR_TBSCORE_SHARE_DIR, new Throwable("QbSdk.init (false) TbsCoreShareDir is null"));
                    return false;
                }
                if (TbsShareManager.isThirdPartyApp(context)) {
                    if (g == 0 || g == TbsShareManager.b(context)) {
                        g = TbsShareManager.b(context);
                    } else {
                        i = null;
                        j = null;
                        TbsLog.e("QbSdk", "QbSdk init (false) ERROR_UNMATCH_TBSCORE_VER_THIRDPARTY!");
                        TbsCoreLoadStat.getInstance().a(context, ErrorCode.ERROR_UNMATCH_TBSCORE_VER_THIRDPARTY, new Throwable("sTbsVersion: " + g + "; AvailableTbsCoreVersion: " + TbsShareManager.b(context)));
                        return false;
                    }
                }
                if (i != null) {
                    return true;
                }
                if (TbsShareManager.isThirdPartyApp(context)) {
                    if (TbsShareManager.g(context)) {
                        file = new File(TbsShareManager.a(context), "tbs_sdk_extension_dex.jar");
                    } else {
                        TbsCoreLoadStat.getInstance().a(context, ErrorCode.ERROR_HOST_UNAVAILABLE, new Throwable("isShareTbsCoreAvailable false!"));
                        return false;
                    }
                }
                if (file.exists()) {
                    i = new DexClassLoader(file.getAbsolutePath(), d.getAbsolutePath(), file.getAbsolutePath(), QbSdk.class.getClassLoader()).loadClass("com.tencent.tbs.sdk.extension.TbsSDKExtension");
                    Constructor constructor = i.getConstructor(new Class[]{Context.class, Context.class});
                    if (TbsShareManager.isThirdPartyApp(context)) {
                        j = constructor.newInstance(new Object[]{context, TbsShareManager.c(context)});
                    }
                    g.a(j, "setClientVersion", new Class[]{Integer.TYPE}, Integer.valueOf(1));
                    return true;
                }
                TbsLog.e("QbSdk", "QbSdk init (false) tbs_sdk_extension_dex.jar is not exist!");
                int b = z.a().b(context);
                if (new File(file.getParentFile(), "tbs_jars_fusion_dex.jar").exists()) {
                    if (b > 0) {
                        TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_MISS_SDKEXTENSION_JAR_WITH_FUSION_DEX_WITH_CORE, new Exception("tbs_sdk_extension_dex not exist(with fusion dex)!" + b));
                        return false;
                    }
                    TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_MISS_SDKEXTENSION_JAR_WITH_FUSION_DEX_WITHOUT_CORE, new Exception("tbs_sdk_extension_dex not exist(with fusion dex)!" + b));
                    return false;
                } else if (b > 0) {
                    TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_INFO_MISS_SDKEXTENSION_JAR_WITHOUT_FUSION_DEX_WITH_CORE, new Exception("tbs_sdk_extension_dex not exist(without fusion dex)!" + b));
                    return false;
                } else {
                    TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_INFO_MISS_SDKEXTENSION_JAR_WITHOUT_FUSION_DEX_WITHOUT_CORE, new Exception("tbs_sdk_extension_dex not exist(without fusion dex)!" + b));
                    return false;
                }
            } catch (Throwable th) {
                TbsLog.e("QbSdk", "QbSdk init Throwable: " + Log.getStackTraceString(th));
                TbsCoreLoadStat.getInstance().a(context, ErrorCode.THROWABLE_QBSDK_INIT, th);
                return false;
            }
        }
    }

    static boolean a(Context context, boolean z, boolean z2) {
        boolean z3 = true;
        boolean z4 = false;
        if (TbsShareManager.isThirdPartyApp(context) && !TbsShareManager.f(context)) {
            TbsCoreLoadStat.getInstance().a(context, ErrorCode.ERROR_UNMATCH_TBSCORE_VER_THIRDPARTY);
        } else if (a(context, z)) {
            Object a = g.a(j, "canLoadX5Core", new Class[]{Integer.TYPE}, Integer.valueOf(36880));
            if (a == null) {
                a = g.a(j, "canLoadX5", new Class[]{Integer.TYPE}, Integer.valueOf(a.a()));
                if (a == null) {
                    TbsCoreLoadStat.getInstance().a(context, ErrorCode.ERROR_CANLOADX5_RETURN_NULL);
                } else if (!((a instanceof String) && ((String) a).equalsIgnoreCase("AuthenticationFail"))) {
                    if (a instanceof Boolean) {
                        g = f.c();
                        boolean a2 = a(context, f.c());
                        if (((Boolean) a).booleanValue() && !a2) {
                            z4 = true;
                        }
                        if (!z4) {
                            TbsLog.e(TbsListener.tag_load_error, "318");
                            TbsLog.w(TbsListener.tag_load_error, "isX5Disable:" + a2);
                            TbsLog.w(TbsListener.tag_load_error, "(Boolean) ret:" + ((Boolean) a));
                        }
                    }
                }
            } else if (!((a instanceof String) && ((String) a).equalsIgnoreCase("AuthenticationFail"))) {
                if (a instanceof Bundle) {
                    Bundle bundle = (Bundle) a;
                    if (bundle.isEmpty()) {
                        TbsCoreLoadStat.getInstance().a(context, ErrorCode.ERROR_QBSDK_INIT_ERROR_EMPTY_BUNDLE, new Throwable("" + a));
                        TbsLog.e(TbsListener.tag_load_error, "empty bundle");
                    } else {
                        int i = bundle.getInt("result_code", -1);
                        if (i != 0) {
                            z3 = false;
                        }
                        if (TbsShareManager.isThirdPartyApp(context)) {
                            f.a(TbsShareManager.b(context));
                            h = String.valueOf(TbsShareManager.b(context));
                            if (h.length() == 5) {
                                h = "0" + h;
                            }
                            if (h.length() != 6) {
                                h = "";
                            }
                        }
                        try {
                            l = bundle.getStringArray("tbs_jarfiles");
                            if (l instanceof String[]) {
                                d = bundle.getString("tbs_librarypath");
                                a = null;
                                if (i != 0) {
                                    try {
                                        a = g.a(j, "getErrorCodeForLogReport", new Class[0], new Object[0]);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                                switch (i) {
                                    case -2:
                                        if (!(a instanceof Integer)) {
                                            TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_DISABLE_X5, new Throwable("detail: " + a));
                                            break;
                                        }
                                        TbsCoreLoadStat.getInstance().a(context, ((Integer) a).intValue(), new Throwable("detail: " + a));
                                        break;
                                    case -1:
                                        if (!(a instanceof Integer)) {
                                            TbsCoreLoadStat.getInstance().a(context, ErrorCode.ERROR_CANLOADX5_RETURN_FALSE, new Throwable("detail: " + a));
                                            break;
                                        }
                                        TbsCoreLoadStat.getInstance().a(context, ((Integer) a).intValue(), new Throwable("detail: " + a));
                                        break;
                                    case 0:
                                        break;
                                    default:
                                        TbsCoreLoadStat.getInstance().a(context, ErrorCode.INFO_INITX5_FALSE_DEFAULT, new Throwable("detail: " + a + "errcode" + i));
                                        break;
                                }
                                z4 = z3;
                            } else {
                                TbsCoreLoadStat.getInstance().a(context, ErrorCode.ERROR_CANLOADX5_RETURN_FALSE, new Throwable("sJarFiles not instanceof String[]: " + l));
                            }
                        } catch (Throwable th) {
                            TbsCoreLoadStat.getInstance().a(context, ErrorCode.ERROR_GETSTRINGARRAY_JARFILE, th);
                        }
                    }
                } else {
                    TbsCoreLoadStat.getInstance().a(context, ErrorCode.ERROR_QBSDK_INIT_ERROR_RET_TYPE_NOT_BUNDLE, new Throwable("" + a));
                    TbsLog.e(TbsListener.tag_load_error, "ret not instance of bundle");
                }
            }
            if (!z4) {
                TbsLog.e(TbsListener.tag_load_error, "319");
            }
        } else {
            TbsLog.e("QbSdk", "QbSdk.init failure!");
        }
        return z4;
    }

    public static boolean canLoadX5FirstTimeThirdApp(Context context) {
        try {
            if (i == null) {
                File d = z.a().d(context);
                if (d == null) {
                    TbsLog.e("QbSdk", "QbSdk canLoadX5FirstTimeThirdApp (false) optDir == null");
                    return false;
                }
                File file = new File(TbsShareManager.a(context), "tbs_sdk_extension_dex.jar");
                if (file.exists()) {
                    i = new DexClassLoader(file.getAbsolutePath(), d.getAbsolutePath(), file.getAbsolutePath(), QbSdk.class.getClassLoader()).loadClass("com.tencent.tbs.sdk.extension.TbsSDKExtension");
                } else {
                    TbsLog.e("QbSdk", "QbSdk canLoadX5FirstTimeThirdApp (false) dexFile.exists()=false", true);
                    return false;
                }
            }
            if (j == null) {
                j = i.getConstructor(new Class[]{Context.class, Context.class}).newInstance(new Object[]{context, context});
            }
            Object a = g.a(j, "canLoadX5CoreForThirdApp", new Class[0], new Object[0]);
            return (a == null || !(a instanceof Boolean)) ? false : ((Boolean) a).booleanValue();
        } catch (Throwable th) {
            TbsLog.e("QbSdk", "canLoadX5FirstTimeThirdApp sys WebView: " + Log.getStackTraceString(th));
            return false;
        }
    }

    public static void forceSysWebView() {
        b = true;
        m = "SysWebViewForcedByOuter: " + Log.getStackTraceString(new Throwable());
        TbsLog.e("QbSdk", "sys WebView: SysWebViewForcedByOuter");
    }

    public static String[] getDexLoaderFileList(Context context, Context context2, String str) {
        int i = 0;
        if (l instanceof String[]) {
            int length = l.length;
            String[] strArr = new String[length];
            while (i < length) {
                strArr[i] = str + l[i];
                i++;
            }
            return strArr;
        }
        Object a = g.a(j, "getJarFiles", new Class[]{Context.class, Context.class, String.class}, context, context2, str);
        if (!(a instanceof String[])) {
            a = new String[]{""};
        }
        return (String[]) a;
    }

    public static int getTbsVersion(Context context) {
        return TbsShareManager.isThirdPartyApp(context) ? TbsShareManager.a(context, false) : z.a().b(context);
    }

    public static void initTbsSettings(Map<String, Object> map) {
        if (f == null) {
            f = map;
            return;
        }
        try {
            f.putAll(map);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void initX5Environment(Context context, PreInitCallback preInitCallback) {
        TbsDownloader.needDownload(context, false, new e(context, preInitCallback));
    }

    public static boolean isTbsCoreInited() {
        f a = f.a(false);
        return a != null && a.d();
    }

    public static synchronized void preInit(Context context) {
        synchronized (QbSdk.class) {
            preInit(context, null);
        }
    }

    public static synchronized void preInit(Context context, PreInitCallback preInitCallback) {
        synchronized (QbSdk.class) {
            TbsLog.initIfNeed(context);
            e = a;
            if (!k) {
                new d(context, new c(Looper.getMainLooper(), preInitCallback, context)).start();
                k = true;
            }
        }
    }

    public static void setTbsLogClient(TbsLogClient tbsLogClient) {
        TbsLog.setTbsLogClient(tbsLogClient);
    }

    public static void unForceSysWebView() {
        b = false;
        TbsLog.e("QbSdk", "sys WebView: unForceSysWebView called");
    }
}
