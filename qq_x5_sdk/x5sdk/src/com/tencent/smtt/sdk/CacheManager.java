package com.tencent.smtt.sdk;

import com.tencent.smtt.utils.g;
import java.io.File;
import java.io.InputStream;
import java.util.Map;

public final class CacheManager {
    @Deprecated
    public static boolean cacheDisabled() {
        ao a = ao.a();
        if (a != null && a.b()) {
            return ((Boolean) a.c().c()).booleanValue();
        }
        Object a2 = g.a("android.webkit.CacheManager", "cacheDisabled");
        return a2 == null ? false : ((Boolean) a2).booleanValue();
    }

    public static InputStream getCacheFile(String str, boolean z) {
        ao a = ao.a();
        return (a == null || !a.b()) ? null : a.c().a(str, z);
    }

    public static Object getCacheFile(String str, Map<String, String> map) {
        ao a = ao.a();
        if (a != null && a.b()) {
            return a.c().f();
        }
        try {
            return g.a(Class.forName("android.webkit.CacheManager"), "getCacheFile", new Class[]{String.class, Map.class}, str, map);
        } catch (Exception e) {
            return null;
        }
    }

    @Deprecated
    public static File getCacheFileBaseDir() {
        ao a = ao.a();
        return (a == null || !a.b()) ? (File) g.a("android.webkit.CacheManager", "getCacheFileBaseDir") : (File) a.c().f();
    }
}
