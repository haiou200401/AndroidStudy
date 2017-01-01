package com.yiming.addon;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.yiming.addoninterface.Action;
import com.yiming.addoninterface.IAddonInterface;

public class AddonService extends Service {
    final String TAG = "gqg:";
    public AddonService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e(TAG, "onBind");
        // TODO: Return the communication channel to the service.
//        throw new UnsupportedOperationException("Not yet implemented");
        return new AddonStub();
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.e(TAG, "onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        Log.e(TAG, "onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        Log.e(TAG, "onDestroy");
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    class AddonStub extends IAddonInterface.Stub {
        public String getAddonName() {
            return "myddd haha";
        }

        public Action getAction() {
            return new Action(Action.ACTION_SHOW);
        }
    }
}
