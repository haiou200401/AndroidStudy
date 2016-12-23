package com.tencent.smtt.sdk;

class u implements Runnable {
    final /* synthetic */ t a;

    u(t tVar) {
        this.a = tVar;
    }

    public void run() {
        if (!TbsShareManager.forceLoadX5FromTBSDemo(this.a.b.getContext()) && !TbsDownloader.needDownload(this.a.b.getContext(), false)) {
        }
    }
}
