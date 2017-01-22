package com.yiming.servicetest;

import android.app.PendingIntent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by gaoqingguang on 2017/1/22.
 */

public class WebViewDownloader {
    //private final String mUrl = "http://down.360safe.com/360mse/360mse_nb00142.apk";
    private final String mUrl = "http://10.18.72.46/apks/AndroidWebView.apk";


    private static WebViewDownloader mInstance;

    public static WebViewDownloader instance() {
        if (null == mInstance) {
            mInstance = new WebViewDownloader();
        }
        return mInstance;
    }
    public void startDownload() {
        String testData = "test data";
        PostRequestTask postTask = new PostRequestTask(testData.getBytes());
        postTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUrl);
    }

    private class PostRequestTask extends AsyncTask<String, Integer, Boolean> {
        private static final String TAG = "PostRequestTask";

        private byte[] mDrmRequest;
        private byte[] mResponseBody;

        public PostRequestTask(byte[] drmRequest) {
            mDrmRequest = drmRequest;
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            mResponseBody = postRequest(urls[0], mDrmRequest);
            if (mResponseBody != null) {
                Log.d(TAG, "response length=" + String.valueOf(mResponseBody.length));
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            Log.e(TAG, "onPostExecute");
            super.onProgressUpdate(values);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            Log.e(TAG, "onPostExecute");
        }


        private byte[] postRequest(String url, byte[] drmRequest) {
            HttpURLConnection urlConnection = null;
            try {
                //URL request = new URL(url + "&signedRequest=" + new String(drmRequest));
                URL request = new URL(url);
                urlConnection = (HttpURLConnection) request.openConnection();
                urlConnection.setDoOutput(true);
                urlConnection.setDoInput(true);
                urlConnection.setUseCaches(false);
                urlConnection.setRequestMethod("POST");
                urlConnection.setRequestProperty("User-Agent", "Widevine CDM v1.0");
                urlConnection.setRequestProperty("Content-Type", "application/json");

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    BufferedInputStream bis =
                            new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    int read = 0;
                    int bufferSize = 512;
                    byte[] buffer = new byte[bufferSize];
                    try {
                        while (true) {
                            read = bis.read(buffer);
                            if (read == -1) break;
                            bos.write(buffer, 0, read);
                        }
                    } finally {
                        bis.close();
                    }
                    return bos.toByteArray();
                } else {
                    Log.d(TAG, "Server returned HTTP error code " + String.valueOf(responseCode));
                    return null;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (Exception exp) {
              exp.printStackTrace();
            } finally {
                if (urlConnection != null) urlConnection.disconnect();
            }
            return null;
        }
    }
}
