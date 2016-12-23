package com.tencent.smtt.sdk;

import android.content.Context;
import android.os.Handler;
import android.os.Process;
import com.tencent.smtt.utils.TbsLog;
import com.tencent.smtt.utils.b;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileLock;
import java.util.Properties;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

class z {
    public static ThreadLocal<Integer> a = new aa();
    static boolean b = false;
    private static z c = null;
    private static final Lock d = new ReentrantLock();
    private static final Lock e = new ReentrantLock();
    private static Handler g = null;
    private static final Long[][] h;
    private static boolean i = false;
    private boolean f = false;

    static {
        r0 = new Long[7][];
        r0[0] = new Long[]{Long.valueOf(25413), Long.valueOf(11460320)};
        r0[1] = new Long[]{Long.valueOf(25436), Long.valueOf(12009376)};
        r0[2] = new Long[]{Long.valueOf(25437), Long.valueOf(11489180)};
        r0[3] = new Long[]{Long.valueOf(25438), Long.valueOf(11489180)};
        r0[4] = new Long[]{Long.valueOf(25439), Long.valueOf(12013472)};
        r0[5] = new Long[]{Long.valueOf(25440), Long.valueOf(11489180)};
        r0[6] = new Long[]{Long.valueOf(25442), Long.valueOf(11489180)};
        h = r0;
    }

    private z() {
    }

    static synchronized z a() {
        z zVar;
        synchronized (z.class) {
            if (c == null) {
                c = new z();
            }
            zVar = c;
        }
        return zVar;
    }

    static File f(Context context) {
        File file = new File(context.getDir("tbs", 0), "core_private");
        return file != null ? (file.isDirectory() || file.mkdir()) ? file : null : null;
    }

    public int a(boolean z, Context context) {
        if (z || ((Integer) a.get()).intValue() <= 0) {
            a.set(Integer.valueOf(b(context)));
        }
        return ((Integer) a.get()).intValue();
    }

    void a(Context context, boolean z) {
        if (z) {
            this.f = true;
        }
        TbsLog.i("TbsInstaller", "TbsInstaller-continueInstallTbsCore currentProcessName=" + context.getApplicationInfo().processName);
        TbsLog.i("TbsInstaller", "TbsInstaller-continueInstallTbsCore currentProcessId=" + Process.myPid());
        TbsLog.i("TbsInstaller", "TbsInstaller-continueInstallTbsCore currentThreadName=" + Thread.currentThread().getName());
        FileOutputStream b = b.b(context, true, "tbslock.txt");
        if (b != null) {
            FileLock a = b.a(context, b);
            if (a != null) {
                if (d.tryLock()) {
                    d.unlock();
                }
                b.a(a, b);
                if (TbsShareManager.isThirdPartyApp(context)) {
                    b(context, TbsShareManager.a(context, false));
                }
            }
        }
    }

    boolean a(Context context) {
        return false;
    }

    boolean a(Context context, int i) {
        return false;
    }

    public synchronized boolean a(Context context, Context context2) {
        TbsLog.i("TbsInstaller", "TbsInstaller--quickDexOptForThirdPartyApp");
        if (!i) {
            i = true;
            new ab(this, context2, context).start();
        }
        return true;
    }

    public boolean a(Context context, File[] fileArr) {
        return false;
    }

    int b(Context context) {
        Exception e;
        Throwable th;
        int i = 0;
        FileInputStream fileInputStream = null;
        FileInputStream fileInputStream2;
        try {
            File file = new File(d(context), "tbs.conf");
            if (file == null || !file.exists()) {
                if (fileInputStream != null) {
                    try {
                        fileInputStream.close();
                    } catch (IOException e2) {
                        TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVerInNolock IOException=" + e2.toString());
                    }
                }
                return i;
            }
            Properties properties = new Properties();
            fileInputStream2 = new FileInputStream(file);
            try {
                properties.load(fileInputStream2);
                fileInputStream2.close();
                String property = properties.getProperty("tbs_core_version");
                if (property != null) {
                    i = Integer.parseInt(property);
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (IOException e22) {
                            TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVerInNolock IOException=" + e22.toString());
                        }
                    }
                } else if (fileInputStream2 != null) {
                    try {
                        fileInputStream2.close();
                    } catch (IOException e222) {
                        TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVerInNolock IOException=" + e222.toString());
                    }
                }
            } catch (Exception e3) {
                e = e3;
                try {
                    TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVerInNolock Exception=" + e.toString());
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (IOException e2222) {
                            TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVerInNolock IOException=" + e2222.toString());
                        }
                    }
                    return i;
                } catch (Throwable th2) {
                    th = th2;
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (IOException e22222) {
                            TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVerInNolock IOException=" + e22222.toString());
                        }
                    }
                    throw th;
                }
            }
            return i;
        } catch (Exception e4) {
            e = e4;
            fileInputStream2 = fileInputStream;
            TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVerInNolock Exception=" + e.toString());
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            return i;
        } catch (Throwable th3) {
            th = th3;
            fileInputStream2 = fileInputStream;
            if (fileInputStream2 != null) {
                fileInputStream2.close();
            }
            throw th;
        }
    }

    File b(Context context, Context context2) {
        File file = new File(context2.getDir("tbs", 0), "core_share");
        return file != null ? (file.isDirectory() || ((context != null && TbsShareManager.isThirdPartyApp(context)) || file.mkdir())) ? file : null : null;
    }

    void b(Context context, int i) {
        TbsLog.i("TbsInstaller", "TbsInstaller-installTbsCoreForThirdPartyApp");
        if (i > 0) {
            int b = b(context);
            if (b != i) {
                Context c = TbsShareManager.c(context);
                if (c != null) {
                    TbsLog.i("TbsInstaller", "TbsInstaller--quickDexOptForThirdPartyApp hostContext != null");
                    a(context, c);
                } else if (b <= 0) {
                    TbsLog.i("TbsInstaller", "TbsInstaller--installTbsCoreForThirdPartyApp hostContext == null");
                    QbSdk.a(context, "TbsInstaller::installTbsCoreForThirdPartyApp forceSysWebViewInner #2");
                }
            }
        }
    }

    void b(Context context, boolean z) {
    }

    int c(Context context) {
        Exception e;
        Throwable th;
        FileOutputStream b = b.b(context, true, "tbslock.txt");
        if (b == null) {
            return -1;
        }
        FileLock a = b.a(context, b);
        if (a == null) {
            return -1;
        }
        boolean tryLock = d.tryLock();
        TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVer locked=" + tryLock);
        if (tryLock) {
            FileInputStream fileInputStream = null;
            FileInputStream fileInputStream2;
            try {
                File file = new File(d(context), "tbs.conf");
                if (file == null || !file.exists()) {
                    if (fileInputStream != null) {
                        try {
                            fileInputStream.close();
                        } catch (IOException e2) {
                            TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVer IOException=" + e2.toString());
                        }
                    }
                    d.unlock();
                    b.a(a, b);
                    return 0;
                }
                Properties properties = new Properties();
                fileInputStream2 = new FileInputStream(file);
                try {
                    properties.load(fileInputStream2);
                    fileInputStream2.close();
                    String property = properties.getProperty("tbs_core_version");
                    if (property == null) {
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (IOException e22) {
                                TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVer IOException=" + e22.toString());
                            }
                        }
                        d.unlock();
                        b.a(a, b);
                        return 0;
                    }
                    a.set(Integer.valueOf(Integer.parseInt(property)));
                    int intValue = ((Integer) a.get()).intValue();
                    if (fileInputStream2 != null) {
                        try {
                            fileInputStream2.close();
                        } catch (IOException e3) {
                            TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVer IOException=" + e3.toString());
                        }
                    }
                    d.unlock();
                    b.a(a, b);
                    return intValue;
                } catch (Exception e4) {
                    e = e4;
                    try {
                        TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVer Exception=" + e.toString());
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (IOException e222) {
                                TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVer IOException=" + e222.toString());
                            }
                        }
                        d.unlock();
                        b.a(a, b);
                        return 0;
                    } catch (Throwable th2) {
                        th = th2;
                        if (fileInputStream2 != null) {
                            try {
                                fileInputStream2.close();
                            } catch (IOException e32) {
                                TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVer IOException=" + e32.toString());
                            }
                        }
                        d.unlock();
                        b.a(a, b);
                        throw th;
                    }
                }
            } catch (Exception e5) {
                e = e5;
                fileInputStream2 = fileInputStream;
                TbsLog.i("TbsInstaller", "TbsInstaller--getTbsCoreInstalledVer Exception=" + e.toString());
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                d.unlock();
                b.a(a, b);
                return 0;
            } catch (Throwable th3) {
                th = th3;
                fileInputStream2 = fileInputStream;
                if (fileInputStream2 != null) {
                    fileInputStream2.close();
                }
                d.unlock();
                b.a(a, b);
                throw th;
            }
        }
        b.a(a, b);
        return 0;
    }

    File d(Context context) {
        return b(null, context);
    }

    File e(Context context) {
        File file = new File(context.getDir("tbs", 0), "share");
        return file != null ? (file.isDirectory() || file.mkdir()) ? file : null : null;
    }
}
