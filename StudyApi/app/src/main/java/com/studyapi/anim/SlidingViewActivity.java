package com.studyapi.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.ScaleAnimation;
import android.view.animation.Transformation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;

import com.studyapi.BaseActivity;
import com.studyapi.R;

import java.util.ArrayList;
import java.util.List;

/*
参考:
http://blog.csdn.net/lmj623565791/article/details/40411921/

 */

public class SlidingViewActivity extends BaseActivity implements View.OnClickListener{
    private int[] mImgIds = new int[]{
            R.mipmap.guide_image_1, R.mipmap.guide_image_2,R.mipmap.guide_image_3
    };
    private List<Bitmap> mBitmaps;

    private HistorySlidingView mSlidingView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_slidingview);
        Button btn = (Button)findViewById(R.id.id_select_next);
        btn.setOnClickListener(this);
        btn = (Button)findViewById(R.id.id_select_prev);
        btn.setOnClickListener(this);
        initData();
    }

    @Override
    public void onClick(View v) {
        boolean isBack = false;
        if (v.getId() == R.id.id_select_prev) {
            isBack = true;
        }
    }

    private void initData() {
        mBitmaps = new ArrayList<>();
        for(int i=0; i<mImgIds.length; i++) {
            Bitmap bit = BitmapFactory.decodeResource(getResources(), mImgIds[i]);
            mBitmaps.add(bit);
        }

        mSlidingView = (HistorySlidingView)findViewById(R.id.id_bitmap_sliding_view);
        mSlidingView.setLeftBitmap(mBitmaps.get(0));
        mSlidingView.setCenterBitmap(mBitmaps.get(1));
        mSlidingView.setRightBitmap(mBitmaps.get(2));
    }

}
