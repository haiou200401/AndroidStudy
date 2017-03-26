package org.chromium.base;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.StrictMode;
import android.os.SystemClock;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by Administrator on 2017/2/19.
 */

public abstract class PathUtils {
    private static final String THUMBNAIL_DIRECTORY_NAME = "textures";

    private static final int DATA_DIRECTORY = 0;
    private static final int THUMBNAIL_DIRECTORY = 1;
    private static final int DATABASE_DIRECTORY = 2;
    private static final int CACHE_DIRECTORY = 3;
    private static final int NUM_DIRECTORIES = 4;
    private static final AtomicBoolean sInitializationStarted = new AtomicBoolean();
    private static AsyncTask<Void, Void, String[]> sDirPathFetchTask;

    // In setPrivateDataDirectorySuffix(), we store the app's context. If the AsyncTask started in
    // setPrivateDataDirectorySuffix() fails to complete by the time we need the values, we will
    // need the context so that we can restart the task synchronously on the UI thread.
    private static Context sDataDirectoryAppContext;

    // We also store the directory path suffix from setPrivateDataDirectorySuffix() for the same
    // reason as above.
    private static String sDataDirectorySuffix;

    // Prevent instantiation.
    private PathUtils() {}

    /**
     * Initialization-on-demand holder. This exists for thread-safe lazy initialization. It will
     * cause getOrComputeDirectoryPaths() to be called (safely) the first time DIRECTORY_PATHS is
     * accessed.
     *
     * <p>See https://en.wikipedia.org/wiki/Initialization-on-demand_holder_idiom.
     */
    private static class Holder {
        private static final String[] DIRECTORY_PATHS = getOrComputeDirectoryPaths();
    }

    /**
     * Get the directory paths from sDirPathFetchTask if available, or compute it synchronously
     * on the UI thread otherwise. This should only be called as part of Holder's initialization
     * above to guarantee thread-safety as part of the initialization-on-demand holder idiom.
     */
    private static String[] getOrComputeDirectoryPaths() {
        try {
            // We need to call sDirPathFetchTask.cancel() here to prevent races. If it returns
            // true, that means that the task got canceled successfully (and thus, it did not
            // finish running its task). Otherwise, it failed to cancel, meaning that it was
            // already finished.
            if (sDirPathFetchTask.cancel(false)) {
                // Allow disk access here because we have no other choice.
                StrictMode.ThreadPolicy oldPolicy = StrictMode.allowThreadDiskReads();
                StrictMode.allowThreadDiskWrites();
                try {
                    // sDirPathFetchTask did not complete. We have to run the code it was supposed
                    // to be responsible for synchronously on the UI thread.
                    return PathUtils.setPrivateDataDirectorySuffixInternal();
                } finally {
                    StrictMode.setThreadPolicy(oldPolicy);
                }
            } else {
                // sDirPathFetchTask succeeded, and the values we need should be ready to access
                // synchronously in its internal future.
                return sDirPathFetchTask.get();
            }
        } catch (InterruptedException e) {
        } catch (ExecutionException e) {
        }

        return null;
    }

    /**
     * Fetch the path of the directory where private data is to be stored by the application. This
     * is meant to be called in an AsyncTask in setPrivateDataDirectorySuffix(), but if we need the
     * result before the AsyncTask has had a chance to finish, then it's best to cancel the task
     * and run it on the UI thread instead, inside getOrComputeDirectoryPaths().
     *
     * @see Context#getDir(String, int)
     */
    private static String[] setPrivateDataDirectorySuffixInternal() {
        String[] paths = new String[NUM_DIRECTORIES];
        paths[DATA_DIRECTORY] = sDataDirectoryAppContext.getDir(sDataDirectorySuffix,
                Context.MODE_PRIVATE).getPath();
        paths[THUMBNAIL_DIRECTORY] = sDataDirectoryAppContext.getDir(
                THUMBNAIL_DIRECTORY_NAME, Context.MODE_PRIVATE).getPath();
        paths[DATABASE_DIRECTORY] = sDataDirectoryAppContext.getDatabasePath("foo").getParent();
        if (sDataDirectoryAppContext.getCacheDir() != null) {
            paths[CACHE_DIRECTORY] = sDataDirectoryAppContext.getCacheDir().getPath();
        }
        return paths;
    }

    /**
     * Starts an asynchronous task to fetch the path of the directory where private data is to be
     * stored by the application.
     *
     * <p>This task can run long (or more likely be delayed in a large task queue), in which case we
     * want to cancel it and run on the UI thread instead. Unfortunately, this means keeping a bit
     * of extra static state - we need to store the suffix and the application context in case we
     * need to try to re-execute later.
     *
     * @param suffix The private data directory suffix.
     * @see Context#getDir(String, int)
     */
    public static void setPrivateDataDirectorySuffix(String suffix, Context context) {
        // This method should only be called once, but many tests end up calling it multiple times,
        // so adding a guard here.
        if (!sInitializationStarted.getAndSet(true)) {
            sDataDirectorySuffix = suffix;
            sDataDirectoryAppContext = context.getApplicationContext();
            sDirPathFetchTask = new AsyncTask<Void, Void, String[]>() {
                @Override
                protected String[] doInBackground(Void... unused) {
                    return PathUtils.setPrivateDataDirectorySuffixInternal();
                }
            }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
        }
    }

    /**
     * @param index The index of the cached directory path.
     * @return The directory path requested.
     */
    private static String getDirectoryPath(int index) {
        return Holder.DIRECTORY_PATHS[index];
    }

    /**
     * @return the private directory that is used to store application data.
     */
    public static String getDataDirectory() {
        assert sDirPathFetchTask != null : "setDataDirectorySuffix must be called first.";
        return getDirectoryPath(DATA_DIRECTORY);
    }

    /**
     * @return the private directory that is used to store application database.
     */
    public static String getDatabaseDirectory() {
        assert sDirPathFetchTask != null : "setDataDirectorySuffix must be called first.";
        return getDirectoryPath(DATABASE_DIRECTORY);
    }

    /**
     * @return the cache directory.
     */
    public static String getCacheDirectory() {
        assert sDirPathFetchTask != null : "setDataDirectorySuffix must be called first.";
        return getDirectoryPath(CACHE_DIRECTORY);
    }

    public static String getThumbnailCacheDirectory() {
        assert sDirPathFetchTask != null : "setDataDirectorySuffix must be called first.";
        return getDirectoryPath(THUMBNAIL_DIRECTORY);
    }

    /**
     * @return the external storage directory.
     */
    @SuppressWarnings("unused")
    public static String getExternalStorageDirectory() {
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public static String getPageCacheDir() {
        return sDataDirectoryAppContext.getExternalCacheDir().getAbsolutePath();
    }
}