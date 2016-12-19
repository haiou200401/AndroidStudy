package com.studyapi;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;

import com.studyapi.anim.SlidingViewActivity;
import com.studyapi.anim.ViewPagerActivity;
import com.studyapi.calc.ContainerClass;
import com.studyapi.calc.LruCacheTest;
import com.studyapi.calc.PerformanceActivity;
import com.studyapi.images.KantuModeActivity;
import com.studyapi.setting.SettingFontActivity;
import com.studyapi.view.TestImageActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initEvent();
    }

    private void initEvent() {
        findViewById(R.id.id_test_calc_ContainerClass).setOnClickListener(this);
        findViewById(R.id.id_test_calc_LruCache).setOnClickListener(this);
        findViewById(R.id.id_test_view_testimage).setOnClickListener(this);
        findViewById(R.id.id_test_anima_viewpager).setOnClickListener(this);
        findViewById(R.id.id_test_anima_slidingview).setOnClickListener(this);
        findViewById(R.id.id_test_setting_font).setOnClickListener(this);
        findViewById(R.id.id_test_performance).setOnClickListener(this);
        findViewById(R.id.id_test_image_viewer).setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {

        Intent intent=new Intent();
        switch(v.getId()) {
            case R.id.id_test_calc_ContainerClass:
                ContainerClass.instance().StartTest();
                break;
            case R.id.id_test_calc_LruCache:
                LruCacheTest.instance().TestStart();
                break;
            case R.id.id_test_performance:
                intent.setClass(MainActivity.this, PerformanceActivity.class);
                startActivity(intent);
                break;
            case R.id.id_test_anima_viewpager:
                intent.setClass(MainActivity.this, ViewPagerActivity.class);
                startActivity(intent);
                break;
            case R.id.id_test_anima_slidingview:
                intent.setClass(MainActivity.this, SlidingViewActivity.class);
                startActivity(intent);
                break;
            case R.id.id_test_setting_font:
                intent.setClass(MainActivity.this, SettingFontActivity.class);
                startActivity(intent);
                break;
            case R.id.id_test_view_testimage:
                intent.setClass(MainActivity.this, TestImageActivity.class);
                startActivity(intent);
                break;
            case R.id.id_test_image_viewer:
                intent.setClass(MainActivity.this, KantuModeActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
