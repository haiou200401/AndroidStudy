package com.tencent.smtt.sdk;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import com.tencent.smtt.utils.TbsLog;
import com.tencent.smtt.utils.a;
import com.tencent.smtt.utils.b;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class TbsShareManager {
    private static Context a;
    private static boolean b;
    private static String c = null;
    private static int d = 0;
    private static String e = null;
    private static boolean f = false;
    private static boolean g = false;
    private static String h = null;
    private static boolean i = false;

    static int a(Context context, boolean z) {
        b(context, z);
        return d;
    }

    private static File a(Context context, String str) {
        File e = z.a().e(context);
        if (e == null) {
            return null;
        }
        File file = new File(e, str);
        if (file != null && file.exists()) {
            return file;
        }
        try {
            file.createNewFile();
            return file;
        } catch (IOException e2) {
            e2.printStackTrace();
            return null;
        }
    }

    static String a(Context context) {
        g(context);
        return c;
    }

    static int b(Context context) {
        return a(context, true);
    }

    private static Context b(Context context, String str) {
        Context context2 = null;
        try {
            context2 = context.createPackageContext(str, 2);
        } catch (NameNotFoundException e) {
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        return context2;
    }

    static boolean b(Context context, boolean z) {
        if (f(context)) {
            return true;
        }
        if (z) {
            QbSdk.a(context, "TbsShareManager::isShareTbsCoreAvailable forceSysWebViewInner!");
        }
        return false;
    }

    static Context c(Context context) {
        g(context);
        return e != null ? b(context, e) : null;
    }

    private static void c(Context context, boolean z) {
        Throwable th;
        OutputStream outputStream;
        FileInputStream fileInputStream;
        FileOutputStream fileOutputStream = null;
        FileInputStream fileInputStream2 = null;
        FileOutputStream fileOutputStream2 = null;
        FileInputStream fileInputStream3;
        try {
            File a = a(context, "core_info");
            if (a == null) {
                try {
                    fileInputStream2.close();
                } catch (Exception e) {
                }
                try {
                    fileOutputStream2.close();
                    return;
                } catch (Exception e2) {
                    return;
                }
            }
            fileInputStream3 = new FileInputStream(a);
            try {
                Properties properties = new Properties();
                properties.load(fileInputStream3);
                properties.setProperty("core_disabled", String.valueOf(false));
                if (z) {
                    String absolutePath = z.a().d(context).getAbsolutePath();
                    String packageName = context.getApplicationContext().getPackageName();
                    int b = a.b(context);
                    properties.setProperty("core_packagename", packageName);
                    properties.setProperty("core_path", absolutePath);
                    properties.setProperty("app_version", String.valueOf(b));
                }
                OutputStream fileOutputStream3 = new FileOutputStream(a);
                try {
                    properties.store(fileOutputStream3, null);
                    try {
                        fileInputStream3.close();
                    } catch (Exception e3) {
                    }
                    try {
                        fileOutputStream3.close();
                    } catch (Exception e4) {
                    }
                } catch (Throwable th2) {
                    th = th2;
                    outputStream = fileOutputStream3;
                    fileInputStream3.close();
                    fileOutputStream.close();
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fileInputStream3.close();
                fileOutputStream.close();
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            fileInputStream3 = null;
            fileInputStream3.close();
            fileOutputStream.close();
            throw th;
        }
    }

    static synchronized String d(Context context) {
        FileInputStream fileInputStream;
        Throwable th;
        Throwable th2;
        String str = null;
        synchronized (TbsShareManager.class) {
            FileInputStream fileInputStream2 = null;
            try {
                File a = a(context, "core_info");
                if (a != null) {
                    fileInputStream = new FileInputStream(a);
                    try {
                        Properties properties = new Properties();
                        properties.load(fileInputStream);
                        String property = properties.getProperty("core_packagename", "");
                        if (!"".equals(property)) {
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (Exception e) {
                                }
                            }
                            str = property;
                        } else if (fileInputStream != null) {
                            try {
                                fileInputStream.close();
                            } catch (Exception e2) {
                            }
                        }
                    } catch (Throwable th3) {
                        th = th3;
                        try {
                            th.printStackTrace();
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (Exception e3) {
                                }
                            }
                            return str;
                        } catch (Throwable th4) {
                            th2 = th4;
                            if (fileInputStream != null) {
                                try {
                                    fileInputStream.close();
                                } catch (Exception e4) {
                                }
                            }
                            throw th2;
                        }
                    }
                } else if (null != null) {
                    try {
                        fileInputStream2.close();
                    } catch (Exception e5) {
                    }
                }
            } catch (Throwable th5) {
                fileInputStream = null;
                th2 = th5;
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
                throw th2;
            }
        }
        return str;
    }

    static synchronized int e(Context context) {
        Throwable th;
        int i = 0;
        synchronized (TbsShareManager.class) {
            FileInputStream fileInputStream = null;
            FileInputStream fileInputStream2;
            try {
                File a = a(context, "core_info");
                if (a == null) {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    fileInputStream2 = new FileInputStream(a);
                    try {
                        Properties properties = new Properties();
                        properties.load(fileInputStream2);
                        String property = properties.getProperty("core_version", "");
                        if ("".equals(property)) {
                            if (fileInputStream2 != null) {
                                try {
                                    fileInputStream2.close();
                                } catch (Exception e2) {
                                    e2.printStackTrace();
                                }
                            }
                        } else {
                            i = Math.max(Integer.parseInt(property), 0);
                            if (fileInputStream2 != null) {
                                try {
                                    fileInputStream2.close();
                                } catch (Exception e22) {
                                    e22.printStackTrace();
                                }
                            }
                        }
                    } catch (Throwable th2) {
                        th = th2;
                        try {
                            th.printStackTrace();
                            if (fileInputStream2 != null) {
                                try {
                                    fileInputStream2.close();
                                } catch (Exception e3) {
                                    e3.printStackTrace();
                                }
                            }
                            i = -2;
                            return i;
                        } catch (Throwable th3) {
                            th = th3;
                            if (fileInputStream2 != null) {
                                try {
                                    fileInputStream2.close();
                                } catch (Exception e222) {
                                    e222.printStackTrace();
                                }
                            }
                            throw th;
                        }
                    }
                }
            } catch (Throwable th4) {
                th = th4;
                fileInputStream2 = fileInputStream;
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                throw th;
            }
        }
        return i;
    }

    static boolean f(Context context) {
        try {
            if (d == 0) {
                findCoreForThirdPartyApp(context);
            }
            if (d == 0) {
                TbsLog.addLog(TbsLog.TBSLOG_CODE_SDK_NO_SHARE_X5CORE, null, new Object[0]);
                return false;
            } else if (d != 0 && getSharedTbsCoreVersion(context, e) == d) {
                return true;
            } else {
                c = null;
                d = 0;
                TbsLog.addLog(TbsLog.TBSLOG_CODE_SDK_CONFLICT_X5CORE, null, new Object[0]);
                QbSdk.a(context, "TbsShareManager::isShareTbsCoreAvailableInner forceSysWebViewInner!");
                return false;
            }
        } catch (Throwable th) {
            th.printStackTrace();
            TbsLog.addLog(TbsLog.TBSLOG_CODE_SDK_UNAVAIL_X5CORE, null, new Object[0]);
            return false;
        }
    }

    public static int findCoreForThirdPartyApp(Context context) {
        k(context);
        TbsLog.i("TbsShareManager", "core_info mAvailableCoreVersion is " + d + " mAvailableCorePath is " + c + " mSrcPackageName is " + e);
        if (!(h(context) || i(context))) {
            d = 0;
            c = null;
            e = null;
            TbsLog.i("TbsShareManager", "core_info error checkCoreInfo is false and checkCoreInOthers is false ");
        }
        if (d > 0 && (QbSdk.a(context, d) || f)) {
            d = 0;
            c = null;
            e = null;
            TbsLog.i("TbsShareManager", "core_info error QbSdk.isX5Disabled ");
        }
        return d;
    }

    public static boolean forceLoadX5FromTBSDemo(Context context) {
        if (context == null || z.a().a(context, null)) {
            return false;
        }
        int sharedTbsCoreVersion = getSharedTbsCoreVersion(context, TbsConfig.APP_DEMO);
        if (sharedTbsCoreVersion <= 0) {
            return false;
        }
        writeProperties(context, Integer.toString(sharedTbsCoreVersion), TbsConfig.APP_DEMO, z.a().d(b(context, TbsConfig.APP_DEMO)).getAbsolutePath(), "1");
        return true;
    }

    public static void forceToLoadX5ForThirdApp(Context context, boolean z) {
        try {
            if (isThirdPartyApp(context)) {
                File e = z.a().e(context);
                if (e != null) {
                    if (z) {
                        File file = new File(e, "core_info");
                        if (file != null && file.exists()) {
                            return;
                        }
                    }
                    for (String str : getCoreProviderAppList()) {
                        int sharedTbsCoreVersion = getSharedTbsCoreVersion(context, str);
                        if (sharedTbsCoreVersion > 0) {
                            c = z.a().b(context, b(context, str)).getAbsolutePath();
                            e = str;
                            d = sharedTbsCoreVersion;
                            if (QbSdk.canLoadX5FirstTimeThirdApp(context)) {
                                writeProperties(context, Integer.toString(d), e, c, Integer.toString(a.b(context)));
                                return;
                            }
                            d = 0;
                            c = null;
                            e = null;
                        }
                    }
                }
            }
        } catch (Exception e2) {
        }
    }

    static boolean g(Context context) {
        return b(context, true);
    }

    public static boolean getCoreDisabled() {
        return f;
    }

    public static String[] getCoreProviderAppList() {
        return new String[]{TbsConfig.APP_WX, TbsConfig.APP_QQ, TbsConfig.APP_QB, TbsConfig.APP_QZONE, "com.tencent.qqpimsecure", TbsConfig.APP_DEMO, TbsConfig.APP_DEMO_TEST};
    }

    public static long getHostCoreVersions(Context context) {
        long j = 0;
        for (String str : getCoreProviderAppList()) {
            if (str.equalsIgnoreCase(TbsConfig.APP_WX)) {
                j += ((long) getSharedTbsCoreVersion(context, str)) * 10000000000L;
            } else if (str.equalsIgnoreCase(TbsConfig.APP_QQ)) {
                j += ((long) getSharedTbsCoreVersion(context, str)) * 100000;
            } else if (str.equalsIgnoreCase(TbsConfig.APP_QZONE)) {
                j += (long) getSharedTbsCoreVersion(context, str);
            }
        }
        return j;
    }

    public static int getSharedTbsCoreVersion(Context context, String str) {
        Context b = b(context, str);
        return b != null ? z.a().b(b) : 0;
    }

    private static boolean h(Context context) {
        return e != null && d == getSharedTbsCoreVersion(context, e);
    }

    private static boolean i(Context context) {
        String[] coreProviderAppList = getCoreProviderAppList();
        int length = coreProviderAppList.length;
        int i = 0;
        while (i < length) {
            String str = coreProviderAppList[i];
            if (d <= 0 || d != getSharedTbsCoreVersion(context, str)) {
                i++;
            } else {
                c = z.a().b(context, b(context, str)).getAbsolutePath();
                e = str;
                return true;
            }
        }
        return false;
    }

    public static boolean isThirdPartyApp(Context context) {
        try {
            if (a != null && a.equals(context.getApplicationContext())) {
                return b;
            }
            a = context.getApplicationContext();
            String packageName = a.getPackageName();
            for (Object equals : getCoreProviderAppList()) {
                if (packageName.equals(equals)) {
                    b = false;
                    return false;
                }
            }
            b = true;
            return true;
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static boolean j(Context context) {
        if (context == null) {
            return false;
        }
        writeProperties(context, Integer.toString(0), "", "", Integer.toString(0));
        return true;
    }

    private static synchronized void k(Context context) {
        Throwable th;
        synchronized (TbsShareManager.class) {
            if (!i) {
                FileInputStream fileInputStream = null;
                FileInputStream fileInputStream2;
                try {
                    File a = a(context, "core_info");
                    if (a != null) {
                        fileInputStream2 = new FileInputStream(a);
                        try {
                            Properties properties = new Properties();
                            properties.load(fileInputStream2);
                            String property = properties.getProperty("core_version", "");
                            if (!"".equals(property)) {
                                d = Math.max(Integer.parseInt(property), 0);
                            }
                            property = properties.getProperty("core_packagename", "");
                            if (!"".equals(property)) {
                                e = property;
                            }
                            property = properties.getProperty("core_path", "");
                            if (!"".equals(property)) {
                                c = property;
                            }
                            property = properties.getProperty("app_version", "");
                            if (!"".equals(property)) {
                                h = property;
                            }
                            f = Boolean.parseBoolean(properties.getProperty("core_disabled", "false"));
                            i = true;
                            if (fileInputStream2 != null) {
                                try {
                                    fileInputStream2.close();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        } catch (Throwable th2) {
                            th = th2;
                            try {
                                th.printStackTrace();
                                if (fileInputStream2 != null) {
                                    try {
                                        fileInputStream2.close();
                                    } catch (Exception e2) {
                                        e2.printStackTrace();
                                    }
                                }
                            } catch (Throwable th3) {
                                th = th3;
                                if (fileInputStream2 != null) {
                                    try {
                                        fileInputStream2.close();
                                    } catch (Exception e3) {
                                        e3.printStackTrace();
                                    }
                                }
                                throw th;
                            }
                        }
                    } else if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (Exception e22) {
                            e22.printStackTrace();
                        }
                    }
                } catch (Throwable th4) {
                    th = th4;
                    fileInputStream2 = fileInputStream;
                    if (fileInputStream2 != null) {
                        fileInputStream2.close();
                    }
                    throw th;
                }
            }
        }
    }

    public static synchronized void writeCoreInfoForThirdPartyApp(Context context, int i, boolean z) {
        int i2 = 0;
        synchronized (TbsShareManager.class) {
            if (i == 0) {
                j(context);
            } else {
                int e = e(context);
                if (e >= 0) {
                    if (i == e) {
                        c(context, z);
                    } else if (i < e) {
                        j(context);
                    } else {
                        String[] coreProviderAppList = getCoreProviderAppList();
                        if (z) {
                            coreProviderAppList = new String[]{context.getApplicationContext().getPackageName()};
                        }
                        int length = coreProviderAppList.length;
                        while (i2 < length) {
                            String str = coreProviderAppList[i2];
                            if (i == getSharedTbsCoreVersion(context, str)) {
                                String absolutePath = z.a().d(b(context, str)).getAbsolutePath();
                                length = a.b(context);
                                if (!str.equals(context.getApplicationContext().getPackageName())) {
                                    TbsLog.i("TbsShareManager", "thirdAPP pre--> delete old core_share Directory:" + i);
                                    try {
                                        b.a(z.a().d(context));
                                        TbsLog.i("TbsShareManager", "thirdAPP success--> delete old core_share Directory");
                                    } catch (Throwable th) {
                                        th.printStackTrace();
                                    }
                                }
                                writeProperties(context, Integer.toString(i), str, absolutePath, Integer.toString(length));
                                try {
                                    File a = a(context, "core_info");
                                    if (!(g || a == null)) {
                                        TbsLinuxToolsJni tbsLinuxToolsJni = new TbsLinuxToolsJni(a);
                                        tbsLinuxToolsJni.a(a.getAbsolutePath(), "644");
                                        tbsLinuxToolsJni.a(z.a().e(context).getAbsolutePath(), "755");
                                        g = true;
                                    }
                                } catch (Throwable th2) {
                                    th2.printStackTrace();
                                }
                            } else {
                                i2++;
                            }
                        }
                    }
                }
            }
        }
    }

    public static void writeProperties(Context context, String str, String str2, String str3, String str4) {
        Throwable th;
        OutputStream outputStream;
        FileOutputStream fileOutputStream = null;
        int i = 0;
        FileInputStream fileInputStream = null;
        FileOutputStream fileOutputStream2 = null;
        FileInputStream fileInputStream2;
        try {
            File a = a(context, "core_info");
            if (a == null) {
                try {
                    fileInputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    fileOutputStream2.close();
                    return;
                } catch (Exception e2) {
                    e2.printStackTrace();
                    return;
                }
            }
            fileInputStream2 = new FileInputStream(a);
            try {
                Properties properties = new Properties();
                properties.load(fileInputStream2);
                try {
                    i = Integer.parseInt(str);
                } catch (Exception e3) {
                }
                if (i != 0) {
                    properties.setProperty("core_version", str);
                    properties.setProperty("core_disabled", String.valueOf(false));
                    properties.setProperty("core_packagename", str2);
                    properties.setProperty("core_path", str3);
                    properties.setProperty("app_version", str4);
                } else {
                    properties.setProperty("core_disabled", String.valueOf(true));
                }
                OutputStream fileOutputStream3 = new FileOutputStream(a);
                try {
                    properties.store(fileOutputStream3, null);
                    i = false;
                    try {
                        fileInputStream2.close();
                    } catch (Exception e22) {
                        e22.printStackTrace();
                    }
                    try {
                        fileOutputStream3.close();
                    } catch (Exception e222) {
                        e222.printStackTrace();
                    }
                } catch (Throwable th2) {
                    th = th2;
                    outputStream = fileOutputStream3;
                    fileInputStream2.close();
                    fileOutputStream.close();
                    throw th;
                }
            } catch (Throwable th3) {
                th = th3;
                fileInputStream2.close();
                fileOutputStream.close();
                throw th;
            }
        } catch (Throwable th4) {
            th = th4;
            fileInputStream2 = null;
            fileInputStream2.close();
            fileOutputStream.close();
            throw th;
        }
    }
}
