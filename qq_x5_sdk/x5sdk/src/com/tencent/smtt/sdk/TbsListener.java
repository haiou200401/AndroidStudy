package com.tencent.smtt.sdk;

public interface TbsListener {
    public static final String tag_load_error = "loaderror";

    public interface ErrorCode {
        public static final int ERROR_CANLOADX5_RETURN_FALSE = 307;
        public static final int ERROR_CANLOADX5_RETURN_NULL = 308;
        public static final int ERROR_CODE_LOAD_BASE = 300;
        public static final int ERROR_GETSTRINGARRAY_JARFILE = 329;
        public static final int ERROR_HOST_UNAVAILABLE = 304;
        public static final int ERROR_QBSDK_INIT_CANLOADX5 = 319;
        public static final int ERROR_QBSDK_INIT_ERROR_EMPTY_BUNDLE = 331;
        public static final int ERROR_QBSDK_INIT_ERROR_RET_TYPE_NOT_BUNDLE = 330;
        public static final int ERROR_QBSDK_INIT_ISSUPPORT = 318;
        public static final int ERROR_TBSCORE_SHARE_DIR = 312;
        public static final int ERROR_UNMATCH_TBSCORE_VER = 303;
        public static final int ERROR_UNMATCH_TBSCORE_VER_THIRDPARTY = 302;
        public static final int INFO_CAN_NOT_DISABLED_BY_CRASH = 408;
        public static final int INFO_CAN_NOT_LOAD_TBS = 405;
        public static final int INFO_CAN_NOT_LOAD_X5 = 407;
        public static final int INFO_CAN_NOT_USE_X5_FINAL_REASON = 411;
        public static final int INFO_CAN_NOT_USE_X5_TBS_AVAILABLE = 409;
        public static final int INFO_CAN_NOT_USE_X5_TBS_NOTAVAILABLE = 410;
        public static final int INFO_CODE_BASE = 400;
        public static final int INFO_DISABLE_X5 = 404;
        public static final int INFO_FORCE_SYSTEM_WEBVIEW_INNER = 401;
        public static final int INFO_FORCE_SYSTEM_WEBVIEW_OUTER = 402;
        public static final int INFO_INFO_MISS_SDKEXTENSION_JAR_WITHOUT_FUSION_DEX_WITHOUT_CORE = 4122;
        public static final int INFO_INFO_MISS_SDKEXTENSION_JAR_WITHOUT_FUSION_DEX_WITH_CORE = 4121;
        public static final int INFO_INITX5_FALSE_DEFAULT = 415;
        public static final int INFO_MISS_SDKEXTENSION_JAR = 403;
        public static final int INFO_MISS_SDKEXTENSION_JAR_OLD = 406;
        public static final int INFO_MISS_SDKEXTENSION_JAR_WITHOUT_FUSION_DEX = 412;
        public static final int INFO_MISS_SDKEXTENSION_JAR_WITH_FUSION_DEX = 413;
        public static final int INFO_MISS_SDKEXTENSION_JAR_WITH_FUSION_DEX_WITHOUT_CORE = 4132;
        public static final int INFO_MISS_SDKEXTENSION_JAR_WITH_FUSION_DEX_WITH_CORE = 4131;
        public static final int INFO_SDKINIT_IS_SYS_FORCED = 414;
        public static final int INFO_USE_BACKUP_FILE_INSTALL_BY_SERVER = 416;
        public static final int TEST_THROWABLE_ISNOT_NULL = 327;
        public static final int TEST_THROWABLE_IS_NULL = 326;
        public static final int THROWABLE_INITTESRUNTIMEENVIRONMENT = 328;
        public static final int THROWABLE_INITX5CORE = 325;
        public static final int THROWABLE_QBSDK_INIT = 306;
    }

    void onDownloadFinish(int i);

    void onDownloadProgress(int i);

    void onInstallFinish(int i);
}
