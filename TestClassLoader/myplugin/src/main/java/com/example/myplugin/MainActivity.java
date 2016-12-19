package com.example.myplugin;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Plugin d = new Plugin();
        Log.e("gqg2:", String.valueOf(d.add(1, 2)) + d.getName());
    }
}
