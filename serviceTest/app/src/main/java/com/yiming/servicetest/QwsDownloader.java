package com.yiming.servicetest;

import android.app.PendingIntent;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.webkit.WebView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by gaoqingguang on 2017/1/22.
 */

public class QwsDownloader {
    //private final String mUrl = "http://down.360safe.com/360mse/360mse_nb00142.apk";
    private final String mUrl = "http://124.239.223.30/soft.tbs.imtt.qq.com/17421/tbs_res_imtt_tbs_release_tbs_core_3.0.0.1049_043024_20170120_170945.tbs?mkey=5885b6350974382f&f=5401&c=0&p=.tbs";


    private static QwsDownloader mInstance;

    public static QwsDownloader instance() {
        if (null == mInstance) {
            mInstance = new QwsDownloader();
        }
        return mInstance;
    }

    private void downloadConfig(final Context context) {
        String url = "http://log.tbs.qq.com/ajax?c=dl&k=4d7757ec0b8cf278bcde2f1109cef656";
        new RequestString(url, null) {
            @Override
            protected void onFinishOnUI(boolean succeed) {
                String config = getResult();
                if (null != config) {
                    Log.e("gqg:", "core file config = " + config);
                    downloadQwFile(context);
                }
            }
        }.start();
    }

    private void downloadQwFile(Context context) {
        File rootDir = context.getDir("qw", 0);
        File coreFile = new File(rootDir, "qw_core.zip");

        new RequestFile(coreFile, mUrl, null) {
            @Override
            protected void onFinishOnUI(boolean succeed) {
                File coreFile = getResult();
                if (null != coreFile) {
                    long len = coreFile.length();
                    Log.e("gqg:", "core file len = " + String.valueOf(len));
                }
            }
        }.start();
    }

    public void startDownload(Context context) {
        downloadConfig(context);
    }

    public abstract class RequestFile extends RequestTask<File> {
        public RequestFile(File resultFile, String url, Map<String, String> headers) {
            super(resultFile, url, headers);
        }
        public RequestFile(String resultFilePath, String url, Map<String, String> headers) {
            super(null, url, headers);
            try {
                File file = new File(resultFilePath);
                if (!file.exists())
                    file.createNewFile();
                setResult(file);
            } catch (Exception exp) {
                exp.printStackTrace();
            }
        }

        @Override
        protected void onReceiveDataOnThread(RequestTask task) {
            task.receiveFile(mResult, false);
        }

        @Override
        protected abstract void onFinishOnUI(boolean succeed);
    }

    public abstract class RequestString extends RequestTask<String> {
        public RequestString(String url, Map<String, String> headers) {
            super(null, url, headers);
        }

        @Override
        protected void onReceiveDataOnThread(RequestTask task) {
            mResult = task.receiveString();
        }

        @Override
        protected abstract void onFinishOnUI(boolean succeed);
    }



    public abstract class RequestTask<ResultType> extends AsyncTask<String, Integer, Boolean> {
        private static final String TAG = "RequestTask";

        private String mUrl;
        private Map<String, String> mRequestHeaders;
        protected ResultType mResult;

        private final int bufferSize = 8192;
        private BufferedInputStream mInputStream;

        public RequestTask(ResultType result, String url, Map<String, String> headers) {
            mResult = result;
            mUrl = url;
            mRequestHeaders = headers;
        }

        public boolean start() {
            if (null != mUrl) {
                executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUrl);
                return true;
            }
            return false;
        }

        public ResultType getResult() {
            return mResult;
        }

        public void setResult(ResultType result) {
            mResult = result;
        }

        protected abstract void onReceiveDataOnThread(RequestTask bis);
        protected abstract void onFinishOnUI(boolean succeed);


        @Override
        protected Boolean doInBackground(String... urls) {
            String url = urls[0];
            HttpURLConnection urlConnection = null;
            try {
                //URL request = new URL(url + "&signedRequest=" + new String(drmRequest));
                urlConnection = openHttpConnection(url);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                if (null != mRequestHeaders) {
                    for (Map.Entry entry : mRequestHeaders.entrySet()) {
                        urlConnection.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
                    }
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    mInputStream = new BufferedInputStream(urlConnection.getInputStream());
                    try {
                        onReceiveDataOnThread(RequestTask.this);
                    } finally {
                        mInputStream.close();
                        return true;
                    }
                } else {
                    Log.d(TAG, "Server returned HTTP error code " + String.valueOf(responseCode));
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
            return false;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.e(TAG, "onPostExecute");
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.e(TAG, "onPostExecute");
            onFinishOnUI(result);
        }

        protected RequestTask receiveFile(final File file, boolean append) {
            OutputStream output = null;
            try {
                output = new BufferedOutputStream(new FileOutputStream(file, append), bufferSize);
                read(mInputStream, output);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                try {
                    output.flush();
                    output.close();
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
            return RequestTask.this;
        }

        protected String receiveString() {
            byte[] bytes = receiveBytes();
            if (null != bytes) {
                try {
                    return new String(bytes, "UTF-8");
                } catch (Exception exp) {
                    exp.printStackTrace();
                }
            }
            return null;
        }

        protected byte[] receiveBytes() {
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            read(mInputStream, bos);
            return bos.toByteArray();
        }

        private RequestTask read(final InputStream input, final OutputStream output) {
            final byte[] buffer = new byte[bufferSize];
            int read;
            try {
                int totalWritten = 0;
                while ((read = input.read(buffer)) != -1) {
                    output.write(buffer, 0, read);
                    totalWritten += read;
                    publishProgress(totalWritten);
                }
            } catch (Exception exp) {
                exp.printStackTrace();
            }

            return RequestTask.this;
        }

        private HttpURLConnection openHttpConnection(String url) {
            HttpURLConnection urlConnection = null;
            try {
                URL request = new URL(url);
                urlConnection = (HttpURLConnection) request.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("User-Agent", "NetFox");
                return urlConnection;
            } catch (Exception exp) {
                exp.printStackTrace();
            }
            return null;
        }
    }
}
