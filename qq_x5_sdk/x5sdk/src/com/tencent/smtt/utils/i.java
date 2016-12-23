package com.tencent.smtt.utils;

import android.annotation.TargetApi;
import android.content.Context;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Properties;

public class i {
    private static i c = null;
    private Context a = null;
    private File b = null;
    private String d = "http://log.tbs.qq.com/ajax?c=pu&v=2&k=";
    private String e = "http://log.tbs.qq.com/ajax?c=pu&tk=";
    private String f = "http://wup.imtt.qq.com:8080";
    private String g = "http://log.tbs.qq.com/ajax?c=dl&k=";
    private String h = "http://cfg.imtt.qq.com/tbs?v=2&mk=";
    private String i = "http://log.tbs.qq.com/ajax?c=ul&v=2&k=";
    private String j = "http://mqqad.html5.qq.com/adjs";
    private String k = "http://log.tbs.qq.com/ajax?c=ucfu&k=";

    @TargetApi(11)
    private i(Context context) {
        TbsLog.w("TbsCommonConfig", "TbsCommonConfig constructing...");
        this.a = context.getApplicationContext();
        f();
    }

    public static synchronized i a() {
        i iVar;
        synchronized (i.class) {
            iVar = c;
        }
        return iVar;
    }

    public static synchronized i a(Context context) {
        i iVar;
        synchronized (i.class) {
            if (c == null) {
                c = new i(context);
            }
            iVar = c;
        }
        return iVar;
    }

    private synchronized void f() {
        try {
            File g = g();
            if (g == null) {
                TbsLog.e("TbsCommonConfig", "Config file is null, default values will be applied");
            } else {
                InputStream fileInputStream = new FileInputStream(g);
                Properties properties = new Properties();
                properties.load(fileInputStream);
                String property = properties.getProperty("pv_post_url", "");
                if (!"".equals(property)) {
                    this.d = property;
                }
                property = properties.getProperty("wup_proxy_domain", "");
                if (!"".equals(property)) {
                    this.f = property;
                }
                property = properties.getProperty("tbs_download_stat_post_url", "");
                if (!"".equals(property)) {
                    this.g = property;
                }
                property = properties.getProperty("tbs_downloader_post_url", "");
                if (!"".equals(property)) {
                    this.h = property;
                }
                property = properties.getProperty("tbs_log_post_url", "");
                if (!"".equals(property)) {
                    this.i = property;
                }
                property = properties.getProperty("tips_url", "");
                if (!"".equals(property)) {
                    this.j = property;
                }
                property = properties.getProperty("tbs_cmd_post_url", "");
                if (!"".equals(property)) {
                    this.k = property;
                }
                String property2 = properties.getProperty("pv_post_url_tk", "");
                if (!"".equals(property2)) {
                    this.e = property2;
                }
                fileInputStream.close();
            }
        } catch (Throwable th) {
            Writer stringWriter = new StringWriter();
            th.printStackTrace(new PrintWriter(stringWriter));
            TbsLog.e("TbsCommonConfig", "exceptions occurred1:" + stringWriter.toString());
        }
    }

    private File g() {
        File file;
        Throwable th;
        Writer stringWriter;
        try {
            if (this.b == null) {
                this.b = new File(b.a(this.a, 5));
                if (this.b == null || !this.b.isDirectory()) {
                    return null;
                }
            }
            file = new File(this.b, "tbsnet.conf");
            if (file.exists()) {
                try {
                    TbsLog.w("TbsCommonConfig", "pathc:" + file.getCanonicalPath());
                    return file;
                } catch (Throwable th2) {
                    th = th2;
                    stringWriter = new StringWriter();
                    th.printStackTrace(new PrintWriter(stringWriter));
                    TbsLog.e("TbsCommonConfig", "exceptions occurred2:" + stringWriter.toString());
                    return file;
                }
            }
            TbsLog.e("TbsCommonConfig", "Get file(" + file.getCanonicalPath() + ") failed!");
            return null;
        } catch (Throwable th3) {
            Throwable th4 = th3;
            file = null;
            th = th4;
            stringWriter = new StringWriter();
            th.printStackTrace(new PrintWriter(stringWriter));
            TbsLog.e("TbsCommonConfig", "exceptions occurred2:" + stringWriter.toString());
            return file;
        }
    }

    public String b() {
        return this.d;
    }

    public String c() {
        return this.g;
    }

    public String d() {
        return this.h;
    }

    public String e() {
        return this.e;
    }
}
