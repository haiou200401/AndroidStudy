package com.tencent.smtt.utils;

import android.util.Base64;
import com.tencent.smtt.sdk.a.a;
import java.security.KeyFactory;
import java.security.Provider;
import java.security.Security;
import java.security.spec.X509EncodedKeySpec;
import java.util.Random;
import javax.crypto.Cipher;

public class f {
    private static final char[] a = "0123456789abcdef".toCharArray();
    private static f b;
    private String c;
    private String d;
    private String e = String.valueOf(new Random().nextInt(89999999) + 10000000);

    private f() {
        int nextInt = new Random().nextInt(89999999) + 10000000;
        this.c = this.e + String.valueOf(nextInt);
    }

    public static synchronized f a() {
        f fVar;
        synchronized (f.class) {
            if (b == null) {
                b = new f();
            }
            fVar = b;
        }
        return fVar;
    }

    private String b(byte[] bArr) {
        char[] cArr = new char[(bArr.length * 2)];
        for (int i = 0; i < bArr.length; i++) {
            int i2 = bArr[i] & 255;
            cArr[i * 2] = a[i2 >>> 4];
            cArr[(i * 2) + 1] = a[i2 & 15];
        }
        return new String(cArr);
    }

    public byte[] a(byte[] bArr) {
        return a.a(this.e.getBytes(), bArr, 1);
    }

    public void b() {
        Security.addProvider((Provider) Class.forName("com.android.org.bouncycastle.jce.provider.BouncyCastleProvider", true, ClassLoader.getSystemClassLoader()).newInstance());
    }

    public String c() {
        if (this.d == null) {
            byte[] bytes = this.c.getBytes();
            Cipher cipher = null;
            try {
                cipher = Cipher.getInstance("RSA/ECB/NoPadding");
            } catch (Exception e) {
                try {
                    b();
                    cipher = Cipher.getInstance("RSA/ECB/NoPadding");
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
            cipher.init(1, KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(Base64.decode("MCwwDQYJKoZIhvcNAQEBBQADGwAwGAIRAMRB/Q0hTCD+XtnQhpQJefUCAwEAAQ==".getBytes(), 0))));
            this.d = b(cipher.doFinal(bytes));
        }
        return this.d;
    }
}
