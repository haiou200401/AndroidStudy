package com.yiming.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.AbsListView;
import android.widget.ListView;

import android.widget.FastScroller;
import com.yiming.myapplication.view.MyAdapter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    private ListView mListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mListView = (ListView)findViewById(R.id.id_listview);
        mListView.setFastScrollEnabled(true);
        List<Map<String, Object>> list=getData();
        mListView.setAdapter(new MyAdapter(this, list));

        setFastScroller();
    }

    private void setFastScroller() {
        try {
            Field mScroller;
            mScroller = AbsListView.class.getDeclaredField("mFastScroll");
            mScroller.setAccessible(true);

            FastScroller scroller = new FastScroller(mListView);
            mScroller.set(mListView, scroller);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
    }

    public List<Map<String, Object>> getData(){
        List<Map<String, Object>> list=new ArrayList<Map<String,Object>>();
        for (int i = 0; i < 50; i++) {
            Map<String, Object> map=new HashMap<String, Object>();
            map.put("image", R.mipmap.ic_launcher);
            map.put("title", "这是一个标题"+i);
            map.put("info", "这是一个详细信息"+i);
            list.add(map);
        }
        return list;
    }
}
