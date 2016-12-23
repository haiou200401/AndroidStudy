package com.tencent.smtt.sdk;

import java.io.File;
import java.io.FileFilter;

class ad implements FileFilter {
    final /* synthetic */ ab a;

    ad(ab abVar) {
        this.a = abVar;
    }

    public boolean accept(File file) {
        return file.getName().endsWith("tbs.conf");
    }
}
