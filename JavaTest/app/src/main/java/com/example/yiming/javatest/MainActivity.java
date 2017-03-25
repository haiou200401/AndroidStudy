package com.example.yiming.javatest;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;


public class MainActivity extends Activity {
    private final String TAG = "gqg:";

    private Handler mHandler;
    FutureTask<String> mDecodeTask;
    ExecutorService mExecutorService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViewById(R.id.id_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startExe();
            }
        });

        mHandler = new Handler();
        mExecutorService = Executors.newSingleThreadExecutor();
    }

    private void startExe() {
        Callable<String> task = new Callable<String>() {
            public String call() {
                Log.e(TAG, "Sleep start");
                try {
                    Thread.sleep(1000 * 10);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
                Log.e(TAG, "Sleep end");

                return "time=" + System.currentTimeMillis();
            }
        };


        //使用Executors来执行
        System.out.println("=========");
        if (null == mDecodeTask) {
            mDecodeTask = new FutureTask(task);
            mExecutorService.submit(mDecodeTask);
        }

        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (null != mDecodeTask) {
                    try {
                        Log.e(TAG, "start get result");
                        long startTime = System.currentTimeMillis();
                        String result = mDecodeTask.get();
                        long delay = System.currentTimeMillis() - startTime;
                        Log.e(TAG, "result=" + result + "; delay time=" + String.valueOf(delay));
                    } catch (Exception exp) {
                        exp.printStackTrace();
                    }
                    mDecodeTask = null;
                }
            }
        }, 1000*6);
//
//        try {
//            Log.e(TAG, "waiting execute result");
//            String result = mDecodeTask.get();
//            Log.e(TAG, "result=" + result);
//        } catch (InterruptedException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        } catch (ExecutionException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

        Log.e(TAG, "end====");

    }
}
