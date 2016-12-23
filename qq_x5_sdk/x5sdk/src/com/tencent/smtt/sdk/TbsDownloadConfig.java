package com.tencent.smtt.sdk;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import java.util.HashMap;
import java.util.Map;

public class TbsDownloadConfig {
    public static final long DEFAULT_RETRY_INTERVAL_SEC = 86400;
    private static TbsDownloadConfig b;
    Map<String, Object> a = new HashMap();
    private Context c;
    public SharedPreferences mPreferences;

    public interface TbsConfigKey {
        public static final String KEY_APP_METADATA = "app_metadata";
        public static final String KEY_APP_VERSIONCODE = "app_versioncode";
        public static final String KEY_APP_VERSIONCODE_FOR_SWITCH = "app_versioncode_for_switch";
        public static final String KEY_APP_VERSIONNAME = "app_versionname";
        public static final String KEY_DESkEY_TOKEN = "tbs_deskey_token";
        public static final String KEY_DEVICE_CPUABI = "device_cpuabi";
        public static final String KEY_DOWNLOADURL_LIST = "tbs_downloadurl_list";
        public static final String KEY_DOWNLOAD_FAILED_MAX_RETRYTIMES = "tbs_download_failed_max_retrytimes";
        public static final String KEY_DOWNLOAD_FAILED_RETRYTIMES = "tbs_download_failed_retrytimes";
        public static final String KEY_DOWNLOAD_INTERRUPT_CODE = "tbs_download_interrupt_code";
        public static final String KEY_DOWNLOAD_INTERRUPT_CODE_REASON = "tbs_download_interrupt_code_reason";
        public static final String KEY_DOWNLOAD_INTERRUPT_TIME = "tbs_download_interrupt_time";
        public static final String KEY_DOWNLOAD_MAXFLOW = "tbs_download_maxflow";
        public static final String KEY_DOWNLOAD_MIN_FREE_SPACE = "tbs_download_min_free_space";
        public static final String KEY_DOWNLOAD_SINGLE_TIMEOUT = "tbs_single_timeout";
        public static final String KEY_DOWNLOAD_SUCCESS_MAX_RETRYTIMES = "tbs_download_success_max_retrytimes";
        public static final String KEY_DOWNLOAD_SUCCESS_RETRYTIMES = "tbs_download_success_retrytimes";
        public static final String KEY_INSTALL_INTERRUPT_CODE = "tbs_install_interrupt_code";
        public static final String KEY_IS_OVERSEA = "is_oversea";
        public static final String KEY_LAST_CHECK = "last_check";
        public static final String KEY_LAST_THIRDAPP_SENDREQUEST_COREVERSION = "last_thirdapp_sendrequest_coreversion";
        public static final String KEY_NEEDDOWNLOAD = "tbs_needdownload";
        public static final String KEY_RESPONSECODE = "tbs_responsecode";
        public static final String KEY_RETRY_INTERVAL = "retry_interval";
        public static final String KEY_TBSAPKFILESIZE = "tbs_apkfilesize";
        public static final String KEY_TBSAPK_MD5 = "tbs_apk_md5";
        public static final String KEY_TBSDOWNLOADURL = "tbs_downloadurl";
        public static final String KEY_TBSDOWNLOAD_FLOW = "tbs_downloadflow";
        public static final String KEY_TBSDOWNLOAD_STARTTIME = "tbs_downloadstarttime";
        public static final String KEY_TBS_DOWNLOAD_V = "tbs_download_version";
        public static final String KEY_USE_BACKUP_VERSION = "use_backup_version";
    }

    private TbsDownloadConfig(Context context) {
        this.mPreferences = context.getSharedPreferences("tbs_download_config", 4);
        this.c = context.getApplicationContext();
    }

    public static synchronized TbsDownloadConfig getInstance() {
        TbsDownloadConfig tbsDownloadConfig;
        synchronized (TbsDownloadConfig.class) {
            tbsDownloadConfig = b;
        }
        return tbsDownloadConfig;
    }

    public static synchronized TbsDownloadConfig getInstance(Context context) {
        TbsDownloadConfig tbsDownloadConfig;
        synchronized (TbsDownloadConfig.class) {
            if (b == null) {
                b = new TbsDownloadConfig(context);
            }
            tbsDownloadConfig = b;
        }
        return tbsDownloadConfig;
    }

    public synchronized void commit() {
        Editor edit = this.mPreferences.edit();
        for (String str : this.a.keySet()) {
            Object obj = this.a.get(str);
            if (obj instanceof String) {
                edit.putString(str, (String) obj);
            } else {
                try {
                    if (obj instanceof Boolean) {
                        edit.putBoolean(str, ((Boolean) obj).booleanValue());
                    } else if (obj instanceof Long) {
                        edit.putLong(str, ((Long) obj).longValue());
                    } else if (obj instanceof Integer) {
                        edit.putInt(str, ((Integer) obj).intValue());
                    } else if (obj instanceof Float) {
                        edit.putFloat(str, ((Float) obj).floatValue());
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        edit.commit();
        this.a.clear();
        return;
    }

    public synchronized long getRetryInterval() {
        return this.mPreferences.getLong(TbsConfigKey.KEY_RETRY_INTERVAL, DEFAULT_RETRY_INTERVAL_SEC);
    }

    public synchronized void setInstallInterruptCode(int i) {
        Editor edit = this.mPreferences.edit();
        edit.putInt(TbsConfigKey.KEY_INSTALL_INTERRUPT_CODE, i);
        edit.commit();
    }
}
