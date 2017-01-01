package com.yiming.servicetest;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;

import com.yiming.addoninterface.Action;
import com.yiming.addoninterface.IAddonInterface;

import java.util.List;

public class MainActivity extends AppCompatActivity {
    final String TAG = "gqg:";

    final String ADDON_ACTION = "com.yiming.AddonService.intent";

    IAddonInterface mAddonInterface;
    AddonConnection mAddonConnection;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAddonConnection = new AddonConnection();

        PackageManager pm = this.getPackageManager();
        Intent baseIntent = new Intent(ADDON_ACTION);
        List<ResolveInfo> resolveInfos = pm.queryIntentServices(baseIntent, PackageManager.GET_RESOLVED_FILTER);
        if (null != resolveInfos && resolveInfos.size() > 0) {
            ResolveInfo info = resolveInfos.get(0);
            Intent intent = new Intent(ADDON_ACTION);
            String category = info.filter.categoriesIterator().next();
            intent.addCategory(category);
            intent.setPackage(info.serviceInfo.packageName);
            this.bindService(intent, mAddonConnection, Context.BIND_AUTO_CREATE);
        }
    }

    public class AddonConnection implements ServiceConnection {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mAddonInterface = IAddonInterface.Stub.asInterface(service);
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    }

    public void click_start_service(View btn) {
        Log.e(TAG, "click_start_service");
        Intent intent = new Intent();
        intent.setAction(ADDON_ACTION);
        intent.setPackage(getPackageName());
        startService(intent);
    }

    public void click_stop_service(View btn) {
        Log.e(TAG, "click_stop_service");
    }

    public void click_bind_service(View btn) {
        Log.e(TAG, "click_bind_service");
        Intent intent = new Intent();
        intent.setAction(ADDON_ACTION);
        intent.setPackage("com.yiming.addon");
        bindService(intent, mAddonConnection, BIND_AUTO_CREATE);
    }

    public void click_unbind_service(View btn) {
        Log.e(TAG, "click_unbind_service");
    }

    public void click_call_service(View btn) {
        Log.e(TAG, "click_call_service");
        try {
            String name = mAddonInterface.getAddonName();
            Log.e(TAG, name);
            Action action = mAddonInterface.getAction();
            Log.e(TAG, action.toString());
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }
}
