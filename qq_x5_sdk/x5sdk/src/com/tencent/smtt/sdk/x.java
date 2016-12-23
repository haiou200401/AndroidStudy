package com.tencent.smtt.sdk;

import com.tencent.smtt.utils.TbsLog;
import com.tencent.smtt.utils.d.a;

final class x implements a {
    x() {
    }

    public void a(int i) {
        TbsLog.i(TbsDownloader.LOGTAG, "[TbsDownloader.sendRequest] httpResponseCode=" + i);
    }
}
