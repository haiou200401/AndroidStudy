package com.tencent.smtt.utils;

import com.tencent.smtt.utils.b.a;
import java.io.File;

final class c implements a {
    c() {
    }

    public boolean a(File file, File file2) {
        return file.length() == file2.length() && file.lastModified() == file2.lastModified();
    }
}
