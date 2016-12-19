package com.example.administrator.testclassloader;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.mylibrary.TestInterface;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;

import dalvik.system.DexClassLoader;


// see: http://www.tuicool.com/articles/FneqEjz
public class MainActivity extends Activity {
    private final String TAG = "gqg:";

    private static final String plugin_package = "com.example.myplugin.client";
    private PackageManager pm;
    private ResolveInfo resolveInfo;
    private PackageInfo sPackageInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.id_load_class).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadApk();
            }
        });
    }
    public static void addAssetPath(Application app, String path) {
        try {
            Method addAssetPath = app.getAssets().getClass().getMethod(
                    "addAssetPath", String.class);
            addAssetPath.invoke(app.getAssets(), path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadApk() {
        Intent classIntent = new Intent(plugin_package, null);
        pm = getPackageManager();

        List<ResolveInfo> activities = pm.queryIntentActivities(classIntent, 0);
        resolveInfo = activities.get(0);
        ActivityInfo activityInfo = resolveInfo.activityInfo;

        String div = System.getProperty("path.separator");
        String packageName = activityInfo.packageName;
        String sourceDir = activityInfo.applicationInfo.sourceDir;
        Log.e(TAG, sourceDir);

        String outDir = getApplicationInfo().dataDir;
        Log.e(TAG, outDir);

        String libraryDir = activityInfo.applicationInfo.nativeLibraryDir;
        Log.e(TAG, libraryDir);

//        sourceDir = "/mnt/sdcard/bclplugin/app-debug.apk";
        DexClassLoader dexcl = new DexClassLoader(sourceDir, outDir, libraryDir, this.getClass().getClassLoader());
        try {
            Application initialApplication = getApplication();
            sPackageInfo = initialApplication.getPackageManager().getPackageInfo(packageName,
                    PackageManager.GET_META_DATA);
            // Construct a package context to load the Java code into the current app.
            Context webViewContext = initialApplication.createPackageContext(
                    packageName,
                    Context.CONTEXT_INCLUDE_CODE | Context.CONTEXT_IGNORE_SECURITY);
            addAssetPath(initialApplication,  webViewContext.getApplicationInfo().sourceDir);

            String s1 = dexcl.toString();
            //Class<?> loadClass = dexcl.loadClass(packageName + ".Plugin");
//            Class<?> loadClass1 = dexcl.loadClass("com.example.myplugin.MainActivity");
            Class<?> loadClass = dexcl.loadClass("com.example.myplugin.Plugin");
            Object instance = loadClass.newInstance();
            Method method = null;

            method = loadClass.getMethod("getName");
            String name = (String)method.invoke(instance);
            Log.e(TAG, "result = " + name);

            Class[] params = new Class[2];
            params[0] = Integer.TYPE;
            params[1] = Integer.TYPE;

            method = loadClass.getMethod("add", params);
            Integer result = (Integer)method.invoke(instance, 3, 8);
            Log.e(TAG, "result = " + String.valueOf(result));


            Method methodGetInterface = loadClass.getMethod("getInterface", params);
            Object iobj = methodGetInterface.invoke(instance, 1, 3);
            if (iobj instanceof TestInterface) {
                TestInterface ti = (TestInterface)iobj;
                int r2 = ti.fun1(9, 6);
                Log.e(TAG, "r2 = " + String.valueOf(r2));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
