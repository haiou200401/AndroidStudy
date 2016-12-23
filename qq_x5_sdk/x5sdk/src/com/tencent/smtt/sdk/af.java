package com.tencent.smtt.sdk;

import com.tencent.smtt.sdk.TbsListener.ErrorCode;
import com.tencent.smtt.utils.TbsLog;
import com.tencent.smtt.utils.d.a;

class af implements a {
    final /* synthetic */ TbsLogReport a;

    af(TbsLogReport tbsLogReport) {
        this.a = tbsLogReport;
    }

    public void a(int i) {
        TbsLog.i(TbsDownloader.LOGTAG, "[TbsApkDownloadStat.reportDownloadStat] onHttpResponseCode:" + i);
        if (i < ErrorCode.ERROR_CODE_LOAD_BASE) {
            this.a.d();
        }
    }
}
