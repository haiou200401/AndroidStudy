package com.studyapi.anim;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
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

public class ViewPagerActivity extends BaseActivity implements View.OnClickListener{
    private CustomViewPager mViewpager;
    private int[] mImgIds = new int[]{
            R.mipmap.guide_image_2, R.mipmap.guide_image_1, R.mipmap.guide_image_3
    };
    private List<ImageView> mImageViews;

    private static final int ANIM_DURATION = 420;
    private float mPrevAnimationValue;
    private int mScrollPosition;
    private float mScrollPositionOffset;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_viewpager);
        Button btn = (Button)findViewById(R.id.id_select_next);
        btn.setOnClickListener(this);
        btn = (Button)findViewById(R.id.id_select_prev);
        btn.setOnClickListener(this);

        initData();

        mViewpager = (CustomViewPager)findViewById(R.id.id_viewpager);
        //mViewpager.setPageTransformer(true, new DepthPageTransformer());
        //mViewpager.setOffscreenPageLimit(3);
        mViewpager.setPageTransformer(false, new DepthPageTransformer2());
        mViewpager.setAdapter(new PagerAdapter() {
            @Override
            public int getCount() {
                return mImageViews.size();
            }

            @Override
            public Object instantiateItem(ViewGroup container, int position) {
                container.addView(mImageViews.get(position));
                return mImageViews.get(position);
            }

            @Override
            public void destroyItem(ViewGroup container, int position, Object object) {
                container.removeView(mImageViews.get(position));
            }

            @Override
            public boolean isViewFromObject(View view, Object object) {
                return view == object;
            }
        });

        mViewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                Log.e("gqg:", "onPageScrolled position=" + String.valueOf(position) + " ;positionOffset=" + String.valueOf(positionOffset) + " ; positionOffsetPixels=" + String.valueOf(positionOffsetPixels));
                mScrollPosition = position;
                mScrollPositionOffset = positionOffset;
            }

            @Override
            public void onPageSelected(int position) {
                Log.e("gqg:", "onPageSelected position=" + String.valueOf(position));
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if (ViewPager.SCROLL_STATE_SETTLING == state) {
                    mViewpager.post(new Runnable() {
                        @Override
                        public void run() {
                            if (0 == mScrollPosition && mScrollPositionOffset < 0.95) {
                                mViewpager.setCurrentItem(0, true);
                            } else if (1 == mScrollPosition && mScrollPositionOffset > 0.05) {
                                mViewpager.setCurrentItem(2, true);
                            }
                        }
                    });

                }
            }
        });


    }

    @Override
    public void onClick(View v) {

        int width = mViewpager.getWidth();
        int height = mViewpager.getHeight();

        Log.e("gqg", String.valueOf(width) + "h=" + String.valueOf(height));
        boolean isBack = false;
        if (v.getId() == R.id.id_select_prev) {
            isBack = true;
        }

        View lv = mImageViews.get(0);
        View rv = mImageViews.get(1);

        if (mViewpager.isFakeDragging()) {
            Log.e("gqg:3", "isFakeDragging()");
            return;
        }

        ValueAnimator va = null;
        if (isBack) {
            mPrevAnimationValue = 0.0f;
            va = ValueAnimator.ofFloat(mPrevAnimationValue, mViewpager.getWidth() - 5);
        } else {
            mPrevAnimationValue = mViewpager.getWidth();
            va = ValueAnimator.ofFloat(mPrevAnimationValue, 5f);
        }

        va.setDuration(ANIM_DURATION);
        va.setInterpolator(new DecelerateInterpolator(1.2f));
        mViewpager.beginFakeDrag();
        va.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                float inc = value - mPrevAnimationValue;
                mPrevAnimationValue = value;

                float ddd = animation.getAnimatedFraction();
                Log.e("gqg:2", "getAnimatedValue =" + String.valueOf(value) + "; getAnimatedFraction()=" + String.valueOf(ddd));
                if (mViewpager.isFakeDragging()) {
                    mViewpager.fakeDragBy(inc);
                    Log.e("gqg:23", "inc =" + String.valueOf(inc));
                }
            }
        });
        va.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                //mPrevAnimationValue = 0;
                //mViewpager.beginFakeDrag();
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                mViewpager.endFakeDrag();
            }

            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }
        });
        va.start();
    }

    private void initData() {
        mImageViews = new ArrayList<>();
        for(int i=0; i<mImgIds.length; i++) {
            ImageView iv = new ImageView(this);
            iv.setImageResource(mImgIds[i]);
            iv.setScaleType(ImageView.ScaleType.CENTER_CROP);
            //iv.setScaleType(ImageView.ScaleType.FIT_START);
            iv.setId(mImgIds[i]);
            mImageViews.add(iv);
        }
    }

    public class DepthPageTransformer2 implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            //Log.e("gqg:", "position=" + String.valueOf(position) + "; view id=" + String.valueOf(view.getId()));
            int pageWidth = view.getWidth();
            if (position < -1) {

            } else if (position <= 0) {
                view.setTranslationX(pageWidth*-position*0.7f);
                if (-1 == position)
                    view.setTranslationX(-1);
            } else if (position <= 1) {
                //view.setTranslationX(pageWidth*-position*0.7f);
                if (0 == position)
                    view.setTranslationX(0);
            } else {
            }
        }
    }

    public class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;

        public void transformPage(View view, float position) {
            //Log.e("gqg:", "position=" + String.valueOf(position) + "; view id=" + String.valueOf(view.getId()));
            int pageWidth = view.getWidth();
            if (position < -1) {
            } else if (position <= 0) {
            } else if (position <= 1) {
                view.setTranslationX(pageWidth*-position*0.7f);
                if (0 == position)
                    view.setTranslationX(0);
            } else {
            }

        }
    }
}
