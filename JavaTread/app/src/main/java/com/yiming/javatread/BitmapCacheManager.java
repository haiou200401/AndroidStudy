package com.yiming.javatread;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.util.LruCache;

import org.chromium.base.PathUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Administrator on 2017/2/19.
 */

public class BitmapCacheManager {
    private static BitmapCacheManager mInstance;

    private File mBaseDir;
    private BitmapCache mBitmapCache;
    ExecutorService mExecutorService;

    private BitmapCacheManager() {
        String cacheDir = PathUtils.getPageCacheDir();
        mBaseDir = new File(cacheDir, "pagecache");
        if (mBaseDir.exists()) {
            mBaseDir.delete();
        }
        if (!mBaseDir.exists()) {
            mBaseDir.mkdir();
        }
        mExecutorService = Executors.newSingleThreadExecutor();
        mBitmapCache = new BitmapCache(3);
    }

    public static BitmapCacheManager instance() {
        if (null == mInstance) {
            mInstance = new BitmapCacheManager();
        }
        return mInstance;
    }

    public void put(String key, Bitmap bitmap) {
        mBitmapCache.put(key, bitmap);
    }

    public Bitmap get(String key) {
        Bitmap bitmap = mBitmapCache.get(key);
        if (null == bitmap) {
            BitmapLoadTask loadTask = new BitmapLoadTask(key);
            loadTask.executeOnExecutor(mExecutorService);
        }
        return bitmap;
    }

    private class BitmapSaveTask extends AsyncTask<Void, Void, Boolean> {
        private String mKey;
        private Bitmap mBitmap;
        BitmapSaveTask(String key, Bitmap bitmap) {
            mKey = key;
            mBitmap = bitmap;
        }

        protected Boolean doInBackground(Void... unused) {
            OutputStream os = null;
            try {
                File file = new File(mBaseDir, mKey);
                file.createNewFile();
                if (file.exists()) {
                    os = new FileOutputStream(file);
                    mBitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
                    os.flush();
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            } finally {
                mBitmap.recycle();
                if (null != os) {
                    try {
                        os.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return false;
        }
    }

    private class BitmapLoadTask extends AsyncTask<Void, Void, Bitmap> {
        private String mKey;
        BitmapLoadTask(String key) {
            mKey = key;
        }

        protected Bitmap doInBackground(Void... unused) {
            InputStream is = null;
            Bitmap bitmap = null;
            try {
                File file = new File(mBaseDir, mKey);
                if (file.exists()) {
                    is = new FileInputStream(file);
                    bitmap = BitmapFactory.decodeStream(is);
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            } finally {
                if (null != is) {
                    try {
                        is.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            return bitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            if (null != bitmap) {
                mBitmapCache.put(mKey, bitmap);
            }
        }
    }

    private class BitmapCache extends LruCache<String, Bitmap> {
        BitmapCache(int maxSize) {
            super(maxSize);
        }

        @Override
        protected void entryRemoved(boolean evicted, String key, Bitmap bitmap, Bitmap newValue) {
            if (null != bitmap) {
                BitmapSaveTask saveTask = new BitmapSaveTask(key, bitmap);
                saveTask.executeOnExecutor(mExecutorService);
            }
            Log.e("gqg:", "lurCache.entryRemoved");
        }

        @Override
        protected Bitmap create(String key) {
            Log.e("gqg:", "lurCache.create");
            return null;
        }
    }



    //private BitmapLruCache dd;
}
