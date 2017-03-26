package com.yiming.javatread;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.widget.TextView;

import org.chromium.base.PathUtils;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class MainActivity extends Activity {
    private final String TAG = "gqg;";
    LruCache<String, String> mCache;

    ExecutorService mExecutorService;

    private TextView mInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        PathUtils.setPrivateDataDirectorySuffix("webview", getApplicationContext());

        setContentView(R.layout.activity_main);

        mCache = new LruCache(3);
        mInput = (TextView)findViewById(R.id.id_input);
        findViewById(R.id.id_start).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testThread();
            }
        });

        findViewById(R.id.id_get).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBitmap();
            }
        });
    }

    private class BitmapDecodeTask implements Callable<String> {
        private String mUrl;
        public BitmapDecodeTask(String url) {
            mUrl = url;
        }

        @Override
        public String call() throws Exception {
            try {
                Thread.sleep(1000*5);
            } catch (Exception exp) {
                exp.printStackTrace();
            }
            return mUrl + "value";
        }
    }

    private void getBitmap() {
        BitmapCacheManager bcm = BitmapCacheManager.instance();
        String key = "key" + mInput.getText();
        Bitmap bitmap = bcm.get(key);
        if (null != bitmap) {
            Log.e(TAG, bitmap.toString());
        }
    }

    private void testThread() {
        mExecutorService = Executors.newSingleThreadExecutor();

        BitmapCacheManager bcm = BitmapCacheManager.instance();

        for (int i=0; i<10; i++) {
            Bitmap bitmap = Bitmap.createBitmap(120, 120, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(bitmap);
            String key = String.format("key%d", i);
            Paint paint = new Paint();
            paint.setColor(Color.RED);
            canvas.drawText(key, 10, 50, paint);

            bcm.put(key, bitmap);
        }
//
//
//        BitmapDecodeTask task = new BitmapDecodeTask("url_");
//        FutureTask<String> ft2 = new FutureTask<String>(task);
//        Future ftresult = mExecutorService.submit(ft2);
//        Log.e(TAG, ftresult.toString());
//        try {
//            Thread.sleep(1000*6);
//            String value = ft2.get();
//            Log.e(TAG, value);
//        } catch (Exception exp) {
//            exp.printStackTrace();
//        }
    }

    private void start() {
        for(int i=0; i<10; i++) {
            String key = String.format("key%d", i);
            String value = String.format("value%d", i);
            mCache.put(key, value);
        }

        for(int i=0; i<10; i++) {
            String key = String.format("key%d", i);
            String value = mCache.get(key);
            if (null != value) {
                Log.e(TAG, String.format("key=%d; value = %d;", key, value));
            }
        }
    }
}
