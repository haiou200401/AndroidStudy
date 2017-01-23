package com.yiming.servicetest;

import android.app.PendingIntent;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
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
    private final String mUrl = "http://10.18.72.46/apks/AndroidWebView.apk";


    private static QwsDownloader mInstance;

    public static QwsDownloader instance() {
        if (null == mInstance) {
            mInstance = new QwsDownloader();
        }
        return mInstance;
    }

    public void startDownload() {
        String testData = "test data";
        Map mapHeads = new HashMap<>();
        PostRequestTask postTask = new PostRequestTask(mapHeads);
        postTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, mUrl);
    }

    public class PostRequestFile extends PostRequestTask {
        File mFile = null;

        public PostRequestFile(Map<String, String> map) {
            //PostRequestTask();
        }

        @Override
        protected void onReceiveData(BufferedInputStream bis) {

        }
    }

//
//    java.io.OutputStream os = null;
//    java.io.InputStream is = null;
//    try {
//        java.io.File file = new java.io.File(str_local_file_path);
//        if (file.exists() && file.length() > 0) {
//        } else {
//            file.createNewFile();
//
//            java.net.URL url = new java.net.URL(str_url);
//            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
//            os = new java.io.FileOutputStream(file);
//            is = conn.getInputStream();
//            byte[] buffer = new byte[1024 * 4];
//            int n_rx = 0;
//            while ((n_rx = is.read(buffer)) > 0) {
//                os.write(buffer, 0, n_rx);
//            }
//        }
//        return true;
//    } catch (MalformedURLException e) {
//    } catch (IOException e) {
//    } finally {
//        os.flush();
//        os.close();
//        is.close();
//    }
//    return false;
//
    public class PostRequestTask extends AsyncTask<String, Integer, File> {
        private static final String TAG = "PostRequestTask";
        private Map<String, String> mRequestHeaders;
        private byte[] mResponseBody;

        public void setRequestHeaders(Map<String, String> map) {
            mRequestHeaders = map;
        }

        protected void onReceiveData(BufferedInputStream bis) {
        }


        @Override
        protected File doInBackground(String... urls) {
            mResponseBody = postRequest(urls[0], mRequestHeaders);
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
        protected void onPostExecute(File result) {
            Log.e(TAG, "onPostExecute");
        }

        private byte[] postRequest(String url, Map<String, String> map) {
            HttpURLConnection urlConnection = null;
            OutputStream os = null;
            try {
                //URL request = new URL(url + "&signedRequest=" + new String(drmRequest));
                urlConnection = openHttpConnection(url);
                urlConnection.setRequestProperty("Content-Type", "application/json");
                if (null != map) {
                    for (Map.Entry entry : map.entrySet()) {
                        urlConnection.setRequestProperty((String) entry.getKey(), (String) entry.getValue());
                    }
                }

                int responseCode = urlConnection.getResponseCode();
                if (responseCode == 200) {
                    BufferedInputStream bis =
                            new BufferedInputStream(urlConnection.getInputStream());
                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    //os = new java.io.FileOutputStream(file);
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

        HttpURLConnection openHttpConnection(String url) {
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
