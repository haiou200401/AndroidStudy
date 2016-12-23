package com.tencent.smtt.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import com.tencent.smtt.export.external.interfaces.IX5WebSettings;
import com.tencent.smtt.sdk.TbsDownloadConfig.TbsConfigKey;
import com.tencent.smtt.sdk.TbsDownloader.TbsDownloaderCallback;
import com.tencent.smtt.utils.TbsLog;

final class w extends Handler {
    w(Looper looper) {
        super(looper);
    }

    public void handleMessage(Message message) {
        switch (message.what) {
            case IX5WebSettings.LOAD_CACHE_AD /*100*/:
                boolean z = message.arg1 == 1;
                boolean a = TbsDownloader.c(true, false);
                if (message.obj != null && (message.obj instanceof TbsDownloaderCallback)) {
                    TbsLog.i(TbsDownloader.LOGTAG, "needDownload-onNeedDownloadFinish needStartDownload=" + a);
                    if (!a || z) {
                        ((TbsDownloaderCallback) message.obj).onNeedDownloadFinish(a, TbsDownloadConfig.getInstance(TbsDownloader.b).mPreferences.getInt(TbsConfigKey.KEY_TBS_DOWNLOAD_V, 0));
                        return;
                    }
                    return;
                }
                return;
            case 103:
                TbsLog.i(TbsDownloader.LOGTAG, "[TbsDownloader.handleMessage] MSG_CONTINUEINSTALL_TBSCORE");
                if (message.arg1 == 0) {
                    z.a().a((Context) message.obj, true);
                    return;
                } else {
                    z.a().a((Context) message.obj, false);
                    return;
                }
            default:
                return;
        }
    }
}
