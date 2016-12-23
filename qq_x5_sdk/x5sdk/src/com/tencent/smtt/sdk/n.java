package com.tencent.smtt.sdk;

class n implements Runnable {
    final /* synthetic */ m a;

    n(m mVar) {
        this.a = mVar;
    }

    public void run() {
        if (!TbsShareManager.forceLoadX5FromTBSDemo(this.a.b.getContext()) && TbsDownloader.needDownload(this.a.b.getContext(), false)) {
            TbsDownloader.startDownload(this.a.b.getContext());
        }
    }
}
