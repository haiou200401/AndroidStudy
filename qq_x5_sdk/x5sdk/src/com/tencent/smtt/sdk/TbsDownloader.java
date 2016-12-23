package com.tencent.smtt.sdk;

import android.content.Context;
import android.os.Build;
import android.os.Build.VERSION;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import com.tencent.smtt.sdk.TbsDownloadConfig.TbsConfigKey;
import com.tencent.smtt.utils.Apn;
import com.tencent.smtt.utils.TbsLog;
import com.tencent.smtt.utils.a;
import com.tencent.smtt.utils.b;
import com.tencent.smtt.utils.d;
import com.tencent.smtt.utils.i;
import java.io.File;
import java.util.Locale;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.json.JSONArray;
import org.json.JSONObject;

public class TbsDownloader {
    public static final boolean DEBUG_DISABLE_DOWNLOAD = false;
    public static boolean DOWNLOAD_OVERSEA_TBS = false;
    public static final String LOGTAG = "TbsDownload";
    private static String a;
    private static Context b;
    private static Handler c;
    private static String d;
    private static Object e = new byte[0];
    private static HandlerThread f;
    private static boolean g = false;
    private static boolean h = false;
    private static boolean i = false;

    public interface TbsDownloaderCallback {
        void onNeedDownloadFinish(boolean z, int i);
    }

    static String a(Context context) {
        if (!TextUtils.isEmpty(a)) {
            return a;
        }
        String str;
        Locale locale = Locale.getDefault();
        StringBuffer stringBuffer = new StringBuffer();
        String str2 = VERSION.RELEASE;
        try {
            str = new String(str2.getBytes("UTF-8"), "ISO8859-1");
        } catch (Exception e) {
            str = str2;
        }
        if (str.length() > 0) {
            stringBuffer.append(str);
        } else {
            stringBuffer.append("1.0");
        }
        stringBuffer.append("; ");
        str = locale.getLanguage();
        if (str != null) {
            stringBuffer.append(str.toLowerCase());
            str = locale.getCountry();
            if (str != null) {
                stringBuffer.append("-");
                stringBuffer.append(str.toLowerCase());
            }
        } else {
            stringBuffer.append("en");
        }
        if ("REL".equals(VERSION.CODENAME)) {
            str2 = Build.MODEL;
            try {
                str = new String(str2.getBytes("UTF-8"), "ISO8859-1");
            } catch (Exception e2) {
                str = str2;
            }
            if (str.length() > 0) {
                stringBuffer.append("; ");
                stringBuffer.append(str);
            }
        }
        str = Build.ID.replaceAll("[一-龥]", "");
        if (str.length() > 0) {
            stringBuffer.append(" Build/");
            stringBuffer.append(str);
        }
        str = String.format("Mozilla/5.0 (Linux; U; Android %s) AppleWebKit/533.1 (KHTML, like Gecko)Version/4.0 Mobile Safari/533.1", new Object[]{stringBuffer});
        a = str;
        return str;
    }

    private static String a(String str) {
        return str == null ? "" : str;
    }

    private static void a(TbsDownloaderCallback tbsDownloaderCallback) {
        TbsLog.i(LOGTAG, "[TbsDownloader.queryConfig]");
        c.removeMessages(100);
        Message obtain = Message.obtain(c, 100);
        if (tbsDownloaderCallback != null) {
            obtain.obj = tbsDownloaderCallback;
        }
        obtain.arg1 = 0;
        obtain.sendToTarget();
    }

    private static boolean a(Context context, boolean z) {
        TbsDownloadConfig instance = TbsDownloadConfig.getInstance(context);
        String string = instance.mPreferences.getString(TbsConfigKey.KEY_APP_VERSIONNAME, null);
        int i = instance.mPreferences.getInt(TbsConfigKey.KEY_APP_VERSIONCODE, 0);
        String string2 = instance.mPreferences.getString(TbsConfigKey.KEY_APP_METADATA, null);
        String a = a.a(b);
        int b = a.b(b);
        String a2 = a.a(b, "com.tencent.mm.BuildInfo.CLIENT_VERSION");
        long currentTimeMillis = System.currentTimeMillis();
        long j = instance.mPreferences.getLong(TbsConfigKey.KEY_LAST_CHECK, 0);
        long retryInterval = instance.getRetryInterval();
        TbsLog.i(LOGTAG, "retryInterval = " + retryInterval + " s");
        if (currentTimeMillis - j > 1000 * retryInterval) {
            return true;
        }
        if (!TbsShareManager.isThirdPartyApp(b) || TbsShareManager.findCoreForThirdPartyApp(b) != 0 || d()) {
            return (a == null || b == 0 || a2 == null) ? false : (a.equals(string) && b == i && a2.equals(string2)) ? false : true;
        } else {
            b.a(b.getDir("tbs", 0));
            z.a.set(Integer.valueOf(0));
            return true;
        }
    }

    private static boolean a(Context context, boolean z, TbsDownloaderCallback tbsDownloaderCallback) {
        Matcher matcher = null;
        TbsDownloadConfig instance = TbsDownloadConfig.getInstance(context);
        if (VERSION.SDK_INT < 8) {
            return false;
        }
        if (QbSdk.c || !TbsShareManager.isThirdPartyApp(b) || b()) {
            d = instance.mPreferences.getString(TbsConfigKey.KEY_DEVICE_CPUABI, matcher);
            if (!TextUtils.isEmpty(d)) {
                try {
                    matcher = Pattern.compile("i686|mips|x86_64").matcher(d);
                } catch (Exception e) {
                }
                if (matcher != null && matcher.find()) {
                    if (tbsDownloaderCallback == null) {
                        return false;
                    }
                    tbsDownloaderCallback.onNeedDownloadFinish(false, 0);
                    return false;
                }
            }
            return true;
        } else if (tbsDownloaderCallback == null) {
            return false;
        } else {
            tbsDownloaderCallback.onNeedDownloadFinish(false, 0);
            return false;
        }
    }

    /* JADX WARNING: inconsistent code. */
    /* Code decompiled incorrectly, please refer to instructions dump. */
    @android.annotation.TargetApi(11)
    private static boolean a(java.lang.String r26, int r27, boolean r28, boolean r29) {
        /*
        r2 = "TbsDownload";
        r3 = new java.lang.StringBuilder;
        r3.<init>();
        r4 = "[TbsDownloader.readResponse] response=";
        r3 = r3.append(r4);
        r0 = r26;
        r3 = r3.append(r0);
        r3 = r3.toString();
        com.tencent.smtt.utils.TbsLog.i(r2, r3);
        r2 = b;
        r10 = com.tencent.smtt.sdk.TbsDownloadConfig.getInstance(r2);
        r2 = android.text.TextUtils.isEmpty(r26);
        if (r2 == 0) goto L_0x0028;
    L_0x0026:
        r2 = 0;
    L_0x0027:
        return r2;
    L_0x0028:
        r7 = new org.json.JSONObject;
        r0 = r26;
        r7.<init>(r0);
        r2 = "RET";
        r2 = r7.getInt(r2);
        if (r2 == 0) goto L_0x0039;
    L_0x0037:
        r2 = 0;
        goto L_0x0027;
    L_0x0039:
        r2 = "RESPONSECODE";
        r11 = r7.getInt(r2);
        r2 = "DOWNLOADURL";
        r12 = r7.getString(r2);
        r2 = "URLLIST";
        r3 = "";
        r13 = r7.optString(r2, r3);
        r2 = "TBSAPKSERVERVERSION";
        r14 = r7.getInt(r2);
        r2 = "DOWNLOADMAXFLOW";
        r15 = r7.getInt(r2);
        r2 = "DOWNLOAD_MIN_FREE_SPACE";
        r16 = r7.getInt(r2);
        r2 = "DOWNLOAD_SUCCESS_MAX_RETRYTIMES";
        r17 = r7.getInt(r2);
        r2 = "DOWNLOAD_FAILED_MAX_RETRYTIMES";
        r18 = r7.getInt(r2);
        r2 = "DOWNLOAD_SINGLE_TIMEOUT";
        r20 = r7.getLong(r2);
        r2 = "TBSAPKFILESIZE";
        r22 = r7.getLong(r2);
        r2 = "RETRY_INTERVAL";
        r4 = 0;
        r8 = r7.optLong(r2, r4);
        r2 = "FLOWCTR";
        r3 = -1;
        r19 = r7.optInt(r2, r3);
        r2 = 0;
        r3 = "USEBBACKUPVER";
        r2 = r7.getInt(r3);	 Catch:{ Exception -> 0x0265 }
    L_0x008d:
        r3 = r10.a;
        r4 = "use_backup_version";
        r2 = java.lang.Integer.valueOf(r2);
        r3.put(r4, r2);
        r6 = 0;
        r5 = 0;
        r4 = 0;
        r3 = "";
        r2 = "PKGMD5";
        r6 = r7.getString(r2);	 Catch:{ Exception -> 0x0174 }
        r2 = "RESETX5";
        r7.getInt(r2);	 Catch:{ Exception -> 0x025f }
        r2 = "UPLOADLOG";
        r5 = r7.getInt(r2);	 Catch:{ Exception -> 0x025f }
        r2 = "RESETTOKEN";
        r2 = r7.has(r2);	 Catch:{ Exception -> 0x025f }
        if (r2 == 0) goto L_0x00c0;
    L_0x00b6:
        r2 = "RESETTOKEN";
        r2 = r7.getInt(r2);	 Catch:{ Exception -> 0x025f }
        if (r2 == 0) goto L_0x0171;
    L_0x00be:
        r2 = 1;
    L_0x00bf:
        r4 = r2;
    L_0x00c0:
        r2 = "SETTOKEN";
        r2 = r7.has(r2);	 Catch:{ Exception -> 0x025f }
        if (r2 == 0) goto L_0x026e;
    L_0x00c8:
        r2 = "SETTOKEN";
        r2 = r7.getString(r2);	 Catch:{ Exception -> 0x025f }
    L_0x00ce:
        r3 = r4;
        r4 = r5;
        r5 = r6;
    L_0x00d1:
        r6 = e;
        monitor-enter(r6);
        if (r3 == 0) goto L_0x00e1;
    L_0x00d6:
        r3 = r10.a;	 Catch:{ all -> 0x017f }
        r7 = "tbs_deskey_token";
        r24 = "";
        r0 = r24;
        r3.put(r7, r0);	 Catch:{ all -> 0x017f }
    L_0x00e1:
        r3 = android.text.TextUtils.isEmpty(r2);	 Catch:{ all -> 0x017f }
        if (r3 != 0) goto L_0x0111;
    L_0x00e7:
        r3 = r2.length();	 Catch:{ all -> 0x017f }
        r7 = 96;
        if (r3 != r7) goto L_0x0111;
    L_0x00ef:
        r3 = new java.lang.StringBuilder;	 Catch:{ all -> 0x017f }
        r3.<init>();	 Catch:{ all -> 0x017f }
        r2 = r3.append(r2);	 Catch:{ all -> 0x017f }
        r3 = "&";
        r2 = r2.append(r3);	 Catch:{ all -> 0x017f }
        r3 = com.tencent.smtt.utils.e.c();	 Catch:{ all -> 0x017f }
        r2 = r2.append(r3);	 Catch:{ all -> 0x017f }
        r2 = r2.toString();	 Catch:{ all -> 0x017f }
        r3 = r10.a;	 Catch:{ all -> 0x017f }
        r7 = "tbs_deskey_token";
        r3.put(r7, r2);	 Catch:{ all -> 0x017f }
    L_0x0111:
        monitor-exit(r6);	 Catch:{ all -> 0x017f }
        r2 = 1;
        if (r4 != r2) goto L_0x0127;
    L_0x0115:
        r2 = c;
        r3 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        r2.removeMessages(r3);
        r2 = c;
        r3 = 104; // 0x68 float:1.46E-43 double:5.14E-322;
        r2 = android.os.Message.obtain(r2, r3);
        r2.sendToTarget();
    L_0x0127:
        r6 = 86400; // 0x15180 float:1.21072E-40 double:4.26873E-319;
        r2 = 1;
        r0 = r19;
        if (r0 != r2) goto L_0x0268;
    L_0x012f:
        r2 = 604800; // 0x93a80 float:8.47505E-40 double:2.98811E-318;
        r2 = (r8 > r2 ? 1 : (r8 == r2 ? 0 : -1));
        if (r2 <= 0) goto L_0x026b;
    L_0x0136:
        r2 = 604800; // 0x93a80 float:8.47505E-40 double:2.98811E-318;
    L_0x0139:
        r8 = 0;
        r4 = (r2 > r8 ? 1 : (r2 == r8 ? 0 : -1));
        if (r4 <= 0) goto L_0x0268;
    L_0x013f:
        r4 = r10.a;
        r6 = "retry_interval";
        r2 = java.lang.Long.valueOf(r2);
        r4.put(r6, r2);
        r2 = b;
        r2 = com.tencent.smtt.sdk.TbsShareManager.isThirdPartyApp(r2);
        if (r2 == 0) goto L_0x0182;
    L_0x0152:
        r2 = r10.a;
        r3 = "tbs_needdownload";
        r4 = 0;
        r4 = java.lang.Boolean.valueOf(r4);
        r2.put(r3, r4);
        r10.commit();
        r2 = "TbsDownload";
        r3 = "downloadUrl is empty --> disable current tbs!";
        com.tencent.smtt.utils.TbsLog.e(r2, r3);
        r2 = b;
        r3 = 0;
        com.tencent.smtt.sdk.TbsShareManager.writeCoreInfoForThirdPartyApp(r2, r14, r3);
        r2 = 0;
        goto L_0x0027;
    L_0x0171:
        r2 = 0;
        goto L_0x00bf;
    L_0x0174:
        r2 = move-exception;
        r2 = r4;
        r4 = r5;
        r5 = r6;
    L_0x0178:
        r25 = r3;
        r3 = r2;
        r2 = r25;
        goto L_0x00d1;
    L_0x017f:
        r2 = move-exception;
        monitor-exit(r6);	 Catch:{ all -> 0x017f }
        throw r2;
    L_0x0182:
        if (r11 != 0) goto L_0x0196;
    L_0x0184:
        r2 = r10.a;
        r3 = "tbs_needdownload";
        r4 = 0;
        r4 = java.lang.Boolean.valueOf(r4);
        r2.put(r3, r4);
        r10.commit();
        r2 = 0;
        goto L_0x0027;
    L_0x0196:
        r2 = b;
        r2 = com.tencent.smtt.sdk.TbsDownloadConfig.getInstance(r2);
        r2 = r2.mPreferences;
        r3 = "tbs_download_version";
        r4 = 0;
        r2.getInt(r3, r4);
        r0 = r27;
        if (r0 >= r14) goto L_0x01ae;
    L_0x01a8:
        r2 = android.text.TextUtils.isEmpty(r12);
        if (r2 == 0) goto L_0x01c0;
    L_0x01ae:
        r2 = r10.a;
        r3 = "tbs_needdownload";
        r4 = 0;
        r4 = java.lang.Boolean.valueOf(r4);
        r2.put(r3, r4);
        r10.commit();
        r2 = 0;
        goto L_0x0027;
    L_0x01c0:
        r2 = r10.a;
        r3 = "tbs_download_version";
        r4 = java.lang.Integer.valueOf(r14);
        r2.put(r3, r4);
        r2 = r10.a;
        r3 = "tbs_downloadurl";
        r2.put(r3, r12);
        r2 = r10.a;
        r3 = "tbs_downloadurl_list";
        r2.put(r3, r13);
        r2 = r10.a;
        r3 = "tbs_responsecode";
        r4 = java.lang.Integer.valueOf(r11);
        r2.put(r3, r4);
        r2 = r10.a;
        r3 = "tbs_download_maxflow";
        r4 = java.lang.Integer.valueOf(r15);
        r2.put(r3, r4);
        r2 = r10.a;
        r3 = "tbs_download_min_free_space";
        r4 = java.lang.Integer.valueOf(r16);
        r2.put(r3, r4);
        r2 = r10.a;
        r3 = "tbs_download_success_max_retrytimes";
        r4 = java.lang.Integer.valueOf(r17);
        r2.put(r3, r4);
        r2 = r10.a;
        r3 = "tbs_download_failed_max_retrytimes";
        r4 = java.lang.Integer.valueOf(r18);
        r2.put(r3, r4);
        r2 = r10.a;
        r3 = "tbs_single_timeout";
        r4 = java.lang.Long.valueOf(r20);
        r2.put(r3, r4);
        r2 = r10.a;
        r3 = "tbs_apkfilesize";
        r4 = java.lang.Long.valueOf(r22);
        r2.put(r3, r4);
        r10.commit();
        if (r5 == 0) goto L_0x0232;
    L_0x022b:
        r2 = r10.a;
        r3 = "tbs_apk_md5";
        r2.put(r3, r5);
    L_0x0232:
        if (r29 != 0) goto L_0x0252;
    L_0x0234:
        r2 = com.tencent.smtt.sdk.z.a();
        r3 = b;
        r2 = r2.a(r3, r14);
        if (r2 == 0) goto L_0x0252;
    L_0x0240:
        r2 = r10.a;
        r3 = "tbs_needdownload";
        r4 = 0;
        r4 = java.lang.Boolean.valueOf(r4);
        r2.put(r3, r4);
    L_0x024c:
        r10.commit();
        r2 = 1;
        goto L_0x0027;
    L_0x0252:
        r2 = r10.a;
        r3 = "tbs_needdownload";
        r4 = 1;
        r4 = java.lang.Boolean.valueOf(r4);
        r2.put(r3, r4);
        goto L_0x024c;
    L_0x025f:
        r2 = move-exception;
        r2 = r4;
        r4 = r5;
        r5 = r6;
        goto L_0x0178;
    L_0x0265:
        r3 = move-exception;
        goto L_0x008d;
    L_0x0268:
        r2 = r6;
        goto L_0x013f;
    L_0x026b:
        r2 = r8;
        goto L_0x0139;
    L_0x026e:
        r2 = r3;
        goto L_0x00ce;
        */
        throw new UnsupportedOperationException("Method not decompiled: com.tencent.smtt.sdk.TbsDownloader.a(java.lang.String, int, boolean, boolean):boolean");
    }

    private static JSONObject b(boolean z, boolean z2) {
        Object simCountryIso;
        JSONObject jSONObject;
        int a;
        JSONArray g;
        TbsDownloadConfig instance = TbsDownloadConfig.getInstance(b);
        String a2 = a(b);
        String d = a.d(b);
        String c = a.c(b);
        String f = a.f(b);
        String str = "";
        String str2 = "";
        String str3 = "";
        str3 = TimeZone.getDefault().getID();
        if (str3 != null) {
            Object obj = str3;
        } else {
            String str4 = str;
        }
        try {
            TelephonyManager telephonyManager = (TelephonyManager) b.getSystemService("phone");
            if (telephonyManager != null) {
                simCountryIso = telephonyManager.getSimCountryIso();
                if (simCountryIso == null) {
                    str = str2;
                }
                jSONObject = new JSONObject();
                jSONObject.put("TIMEZONEID", obj);
                jSONObject.put("COUNTRYISO", simCountryIso);
                jSONObject.put("PROTOCOLVERSION", 1);
                a = TbsShareManager.isThirdPartyApp(b) ? QbSdk.c ? TbsShareManager.a(b, false) : TbsDownloadConfig.getInstance(b).mPreferences.getInt(TbsConfigKey.KEY_TBS_DOWNLOAD_V, 0) : 0;
                if (z) {
                    jSONObject.put("FUNCTION", a != 0 ? 0 : 1);
                } else {
                    jSONObject.put("FUNCTION", 2);
                }
                if (TbsShareManager.isThirdPartyApp(b)) {
                    g = g();
                    if (Apn.getApnType(b) != 3 && g.length() != 0 && a == 0 && z) {
                        jSONObject.put("TBSBACKUPARR", g);
                    }
                } else {
                    g = e();
                    jSONObject.put("TBSVLARR", g);
                    instance.a.put(TbsConfigKey.KEY_LAST_THIRDAPP_SENDREQUEST_COREVERSION, g.toString());
                    instance.commit();
                }
                jSONObject.put("APPN", b.getPackageName());
                jSONObject.put("APPVN", a(instance.mPreferences.getString(TbsConfigKey.KEY_APP_VERSIONNAME, null)));
                jSONObject.put("APPVC", instance.mPreferences.getInt(TbsConfigKey.KEY_APP_VERSIONCODE, 0));
                jSONObject.put("APPMETA", a(instance.mPreferences.getString(TbsConfigKey.KEY_APP_METADATA, null)));
                jSONObject.put("TBSSDKV", 36880);
                jSONObject.put("TBSV", a);
                jSONObject.put("CPU", d);
                jSONObject.put("UA", a2);
                jSONObject.put("IMSI", a(d));
                jSONObject.put("IMEI", a(c));
                jSONObject.put("ANDROID_ID", a(f));
                if (getOverSea(b)) {
                    jSONObject.put("OVERSEA", 1);
                }
                if (z2) {
                    jSONObject.put("DOWNLOAD_FOREGROUND", 1);
                }
                TbsLog.i(LOGTAG, "[TbsDownloader.postJsonData] jsonData=" + jSONObject.toString());
                return jSONObject;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        str = str3;
        if (simCountryIso == null) {
            str = str2;
        }
        jSONObject = new JSONObject();
        try {
            jSONObject.put("TIMEZONEID", obj);
            jSONObject.put("COUNTRYISO", simCountryIso);
            jSONObject.put("PROTOCOLVERSION", 1);
            if (TbsShareManager.isThirdPartyApp(b)) {
                if (QbSdk.c) {
                }
            }
            if (z) {
                if (a != 0) {
                }
                jSONObject.put("FUNCTION", a != 0 ? 0 : 1);
            } else {
                jSONObject.put("FUNCTION", 2);
            }
            if (TbsShareManager.isThirdPartyApp(b)) {
                g = g();
                jSONObject.put("TBSBACKUPARR", g);
            } else {
                g = e();
                jSONObject.put("TBSVLARR", g);
                instance.a.put(TbsConfigKey.KEY_LAST_THIRDAPP_SENDREQUEST_COREVERSION, g.toString());
                instance.commit();
            }
            jSONObject.put("APPN", b.getPackageName());
            jSONObject.put("APPVN", a(instance.mPreferences.getString(TbsConfigKey.KEY_APP_VERSIONNAME, null)));
            jSONObject.put("APPVC", instance.mPreferences.getInt(TbsConfigKey.KEY_APP_VERSIONCODE, 0));
            jSONObject.put("APPMETA", a(instance.mPreferences.getString(TbsConfigKey.KEY_APP_METADATA, null)));
            jSONObject.put("TBSSDKV", 36880);
            jSONObject.put("TBSV", a);
            jSONObject.put("CPU", d);
            jSONObject.put("UA", a2);
            jSONObject.put("IMSI", a(d));
            jSONObject.put("IMEI", a(c));
            jSONObject.put("ANDROID_ID", a(f));
            if (getOverSea(b)) {
                jSONObject.put("OVERSEA", 1);
            }
            if (z2) {
                jSONObject.put("DOWNLOAD_FOREGROUND", 1);
            }
        } catch (Exception e2) {
        }
        TbsLog.i(LOGTAG, "[TbsDownloader.postJsonData] jsonData=" + jSONObject.toString());
        return jSONObject;
    }

    private static boolean b() {
        try {
            for (String sharedTbsCoreVersion : TbsShareManager.getCoreProviderAppList()) {
                if (TbsShareManager.getSharedTbsCoreVersion(b, sharedTbsCoreVersion) > 0) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private static synchronized void c() {
        synchronized (TbsDownloader.class) {
            if (f == null) {
                f = y.a();
                c = new w(f.getLooper());
            }
        }
    }

    private static boolean c(boolean z, boolean z2) {
        TbsLog.i(LOGTAG, "[TbsDownloader.sendRequest]isQuery: " + z);
        if (z.a().a(b)) {
            TbsLog.i(LOGTAG, "[TbsDownloader.sendRequest] -- isTbsLocalInstalled!");
            return false;
        }
        int i;
        boolean a;
        TbsDownloadConfig instance = TbsDownloadConfig.getInstance(b);
        File file = new File(b.a(b, 1), getOverSea(b) ? "x5.oversea.tbs.org" : "x5.tbs.org");
        File file2 = new File(b.a(b, 2), getOverSea(b) ? "x5.oversea.tbs.org" : "x5.tbs.org");
        File file3 = new File(b.a(b, 3), getOverSea(b) ? "x5.oversea.tbs.org" : "x5.tbs.org");
        File file4 = new File(b.a(b, 4), getOverSea(b) ? "x5.oversea.tbs.org" : "x5.tbs.org");
        if (!file4.exists()) {
            if (file3.exists()) {
                file3.renameTo(file4);
            } else if (file2.exists()) {
                file2.renameTo(file4);
            } else if (file.exists()) {
                file.renameTo(file4);
            }
        }
        instance.a.put(TbsConfigKey.KEY_LAST_CHECK, Long.valueOf(System.currentTimeMillis()));
        instance.a.put(TbsConfigKey.KEY_APP_VERSIONNAME, a.a(b));
        instance.a.put(TbsConfigKey.KEY_APP_VERSIONCODE, Integer.valueOf(a.b(b)));
        instance.a.put(TbsConfigKey.KEY_APP_METADATA, a.a(b, "com.tencent.mm.BuildInfo.CLIENT_VERSION"));
        instance.commit();
        if (d == null) {
            d = a.a();
            instance.a.put(TbsConfigKey.KEY_DEVICE_CPUABI, d);
            instance.commit();
        }
        if (!TextUtils.isEmpty(d)) {
            Matcher matcher = null;
            try {
                matcher = Pattern.compile("i686|mips|x86_64").matcher(d);
            } catch (Exception e) {
            }
            if (matcher != null && matcher.find()) {
                return false;
            }
        }
        JSONObject b = b(z, z2);
        try {
            i = b.getInt("TBSV");
        } catch (Exception e2) {
            i = -1;
        }
        if (i != -1) {
            try {
                String d = i.a(b).d();
                TbsLog.i(LOGTAG, "[TbsDownloader.sendRequest] postUrl=" + d);
                a = a(d.a(d, b.toString().getBytes("utf-8"), new x(), false), i, z, z2);
            } catch (Throwable th) {
                th.printStackTrace();
            }
            return a;
        }
        a = false;
        return a;
    }

    private static boolean d() {
        try {
            return TbsDownloadConfig.getInstance(b).mPreferences.getString(TbsConfigKey.KEY_LAST_THIRDAPP_SENDREQUEST_COREVERSION, "").equals(e().toString());
        } catch (Exception e) {
            return false;
        }
    }

    private static JSONArray e() {
        if (!TbsShareManager.isThirdPartyApp(b)) {
            return null;
        }
        JSONArray jSONArray = new JSONArray();
        Object coreProviderAppList = TbsShareManager.getCoreProviderAppList();
        String packageName = b.getApplicationContext().getPackageName();
        Object obj;
        if (packageName.equals(TbsShareManager.d(b))) {
            int length = coreProviderAppList.length;
            obj = new String[(length + 1)];
            System.arraycopy(coreProviderAppList, 0, obj, 0, length);
            obj[length] = packageName;
        } else {
            obj = coreProviderAppList;
        }
        for (String sharedTbsCoreVersion : r0) {
            int sharedTbsCoreVersion2 = TbsShareManager.getSharedTbsCoreVersion(b, sharedTbsCoreVersion);
            if (sharedTbsCoreVersion2 > 0) {
                int i;
                for (i = 0; i < jSONArray.length(); i++) {
                    if (jSONArray.optInt(i) == sharedTbsCoreVersion2) {
                        i = 1;
                        break;
                    }
                }
                i = 0;
                if (i == 0) {
                    jSONArray.put(sharedTbsCoreVersion2);
                }
            }
        }
        return jSONArray;
    }

    private static boolean f() {
        return true;
    }

    private static JSONArray g() {
        JSONArray jSONArray = new JSONArray();
        String[] coreProviderAppList = TbsShareManager.getCoreProviderAppList();
        int length = coreProviderAppList.length;
        int i = 0;
        while (i < length) {
            File file = new File(b.a(b, coreProviderAppList[i], 4, false), getOverSea(b) ? "x5.oversea.tbs.org" : "x5.tbs.org");
            i = (file == null || file.exists()) ? i + 1 : i + 1;
        }
        return jSONArray;
    }

    public static synchronized boolean getOverSea(Context context) {
        boolean z;
        synchronized (TbsDownloader.class) {
            z = h;
        }
        return z;
    }

    public static HandlerThread getsTbsHandlerThread() {
        return f;
    }

    public static boolean needDownload(Context context, boolean z) {
        return needDownload(context, z, null);
    }

    public static boolean needDownload(Context context, boolean z, TbsDownloaderCallback tbsDownloaderCallback) {
        TbsLog.initIfNeed(context);
        if (!z.b) {
            TbsLog.app_extra(LOGTAG, context);
            b = context.getApplicationContext();
            TbsDownloadConfig instance = TbsDownloadConfig.getInstance(b);
            if (!a(b, z, tbsDownloaderCallback)) {
                return false;
            }
            boolean z2;
            c();
            boolean a = a(b, false);
            if (a) {
                a(tbsDownloaderCallback);
            }
            c.removeMessages(102);
            Message.obtain(c, 102).sendToTarget();
            if (QbSdk.c || !TbsShareManager.isThirdPartyApp(context)) {
                boolean contains = instance.mPreferences.contains(TbsConfigKey.KEY_NEEDDOWNLOAD);
                TbsLog.i(LOGTAG, "[TbsDownloader.needDownload] hasNeedDownloadKey=" + contains);
                z2 = (contains || TbsShareManager.isThirdPartyApp(context)) ? instance.mPreferences.getBoolean(TbsConfigKey.KEY_NEEDDOWNLOAD, false) : true;
            } else {
                z2 = false;
            }
            if (!z2) {
                int c = z.a().c(b);
                if (a || c <= 0) {
                    c.removeMessages(103);
                    if (c > 0 || a) {
                        Message.obtain(c, 103, 1, 0, b).sendToTarget();
                    } else {
                        Message.obtain(c, 103, 0, 0, b).sendToTarget();
                    }
                }
            } else if (!f()) {
                z2 = false;
            }
            if (!(a || tbsDownloaderCallback == null)) {
                tbsDownloaderCallback.onNeedDownloadFinish(false, 0);
            }
            TbsLog.i(LOGTAG, "[TbsDownloader.needDownload] needDownload=" + z2);
            return z2;
        } else if (tbsDownloaderCallback == null) {
            return false;
        } else {
            tbsDownloaderCallback.onNeedDownloadFinish(false, 0);
            return false;
        }
    }

    public static void startDownload(Context context) {
        startDownload(context, false);
    }

    public static synchronized void startDownload(Context context, boolean z) {
        synchronized (TbsDownloader.class) {
        }
    }

    public static void stopDownload() {
    }
}
