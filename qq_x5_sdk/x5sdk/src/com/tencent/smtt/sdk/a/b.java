package com.tencent.smtt.sdk.a;

import MTT.ThirdAppInfoNew;
import android.content.Context;
import android.text.TextUtils;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsCoreLoadStat;
import com.tencent.smtt.sdk.TbsShareManager;
import com.tencent.smtt.utils.TbsLog;
import com.tencent.smtt.utils.a;
import com.tencent.smtt.utils.h;
import com.tencent.smtt.utils.i;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.TimeZone;
import org.json.JSONObject;

public class b {
    public static byte[] a;

    static {
        a = null;
        try {
            a = "65dRa93L".getBytes("utf-8");
        } catch (UnsupportedEncodingException e) {
        }
    }

    private static String a(Context context) {
        String str = null;
        try {
            byte[] toByteArray = context.getPackageManager().getPackageInfo(context.getPackageName(), 64).signatures[0].toByteArray();
            if (toByteArray != null) {
                MessageDigest instance = MessageDigest.getInstance("SHA-1");
                instance.update(toByteArray);
                toByteArray = instance.digest();
                if (toByteArray != null) {
                    StringBuilder stringBuilder = new StringBuilder("");
                    if (toByteArray != null && toByteArray.length > 0) {
                        for (int i = 0; i < toByteArray.length; i++) {
                            String toUpperCase = Integer.toHexString(toByteArray[i] & 255).toUpperCase();
                            if (i > 0) {
                                stringBuilder.append(":");
                            }
                            if (toUpperCase.length() < 2) {
                                stringBuilder.append(0);
                            }
                            stringBuilder.append(toUpperCase);
                        }
                        str = stringBuilder.toString();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return str;
    }

    public static void a(ThirdAppInfoNew thirdAppInfoNew, Context context) {
        new c("HttpUtils", context, thirdAppInfoNew).start();
    }

    public static void a(Context context, String str, String str2, String str3, int i, boolean z) {
        String str4 = "";
        try {
            ThirdAppInfoNew thirdAppInfoNew = new ThirdAppInfoNew();
            thirdAppInfoNew.sAppName = context.getApplicationContext().getApplicationInfo().packageName;
            i.a(context);
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            simpleDateFormat.setTimeZone(TimeZone.getTimeZone("GMT+08"));
            thirdAppInfoNew.sTime = simpleDateFormat.format(Calendar.getInstance().getTime());
            thirdAppInfoNew.sGuid = str;
            if (z) {
                thirdAppInfoNew.sQua2 = str2;
            } else {
                thirdAppInfoNew.sQua2 = h.a(context);
            }
            thirdAppInfoNew.sLc = str3;
            String e = a.e(context);
            String c = a.c(context);
            String d = a.d(context);
            Object f = a.f(context);
            if (!(c == null || "".equals(c))) {
                thirdAppInfoNew.sImei = c;
            }
            if (!(d == null || "".equals(d))) {
                thirdAppInfoNew.sImsi = d;
            }
            if (!TextUtils.isEmpty(f)) {
                thirdAppInfoNew.sAndroidID = f;
            }
            if (!(e == null || "".equals(e))) {
                thirdAppInfoNew.sMac = e;
            }
            thirdAppInfoNew.iPv = (long) i;
            thirdAppInfoNew.iCoreType = z ? 1 : 0;
            thirdAppInfoNew.sAppVersionName = str4;
            thirdAppInfoNew.sAppSignature = a(context);
            if (!z) {
                thirdAppInfoNew.localCoreVersion = QbSdk.getTbsVersion(context);
            }
            a(thirdAppInfoNew, context);
        } catch (Throwable th) {
            th.printStackTrace();
        }
    }

    private static JSONObject c(ThirdAppInfoNew thirdAppInfoNew, Context context) {
        try {
            JSONObject jSONObject = new JSONObject();
            jSONObject.put("APPNAME", thirdAppInfoNew.sAppName);
            jSONObject.put("TIME", thirdAppInfoNew.sTime);
            jSONObject.put("QUA2", thirdAppInfoNew.sQua2);
            jSONObject.put("LC", thirdAppInfoNew.sLc);
            jSONObject.put("GUID", thirdAppInfoNew.sGuid);
            jSONObject.put("IMEI", thirdAppInfoNew.sImei);
            jSONObject.put("IMSI", thirdAppInfoNew.sImsi);
            jSONObject.put("MAC", thirdAppInfoNew.sMac);
            jSONObject.put("PV", thirdAppInfoNew.iPv);
            jSONObject.put("CORETYPE", thirdAppInfoNew.iCoreType);
            jSONObject.put("APPVN", thirdAppInfoNew.sAppVersionName);
            if (thirdAppInfoNew.sAppSignature == null) {
                jSONObject.put("SIGNATURE", "0");
            } else {
                jSONObject.put("SIGNATURE", thirdAppInfoNew.sAppSignature);
            }
            jSONObject.put("PROTOCOL_VERSION", 3);
            jSONObject.put("ANDROID_ID", thirdAppInfoNew.sAndroidID);
            if (TbsShareManager.isThirdPartyApp(context)) {
                jSONObject.put("HOST_COREVERSION", TbsShareManager.getHostCoreVersions(context));
            }
            if (thirdAppInfoNew.iCoreType != 0) {
                return jSONObject;
            }
            jSONObject.put("WIFICONNECTEDTIME", thirdAppInfoNew.sWifiConnectedTime);
            jSONObject.put("CORE_EXIST", thirdAppInfoNew.localCoreVersion);
            int i = TbsCoreLoadStat.mLoadErrorCode;
            if (thirdAppInfoNew.localCoreVersion > 0) {
                jSONObject.put("TBS_ERROR_CODE", i);
            }
            if (i != -1) {
                return jSONObject;
            }
            TbsLog.e("sdkreport", "ATTENTION: Load errorCode missed!");
            return jSONObject;
        } catch (Exception e) {
            TbsLog.e("sdkreport", "getPostData exception!");
            return null;
        }
    }
}
