package com.yiming.myapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.yiming.myapplication.R;

/**
 * Created by gaoqingguang on 2017/3/25.
 */

public class CustomView extends FrameLayout implements View.OnClickListener{

    private int mScrollRangeY = 0;
    private int mScrollY = 0;

    private AwContentsScroller mFastScroller;

    public CustomView(Context context) {
        this(context, null);
    }

    public CustomView(Context context, AttributeSet attributeSet) {
        this(context, attributeSet, android.R.attr.webViewStyle);
    }
    public CustomView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setWillNotDraw(false);

        mFastScroller = new AwContentsScroller(this.getContext(), this);
        //mFastScroller.setEnabled(true);
        //mFastScroller.setAlwaysShow(true);
        mFastScroller.updateScrollRange(1500*4);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        initView();
    }

    private void initView() {
        findViewById(R.id.id_up).setOnClickListener(this);
        findViewById(R.id.id_down).setOnClickListener(this);
        findViewById(R.id.id_max).setOnClickListener(this);
        findViewById(R.id.id_min).setOnClickListener(this);

        //mFastScroller.updateScrollRange(getHeight() * 6);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (mFastScroller != null) {
            mFastScroller.onScrollChanged(l, t, oldl, oldt);
        }
    }

    @Override
    public void setScrollBarStyle(int style) {
        super.setScrollBarStyle(style);
        if (mFastScroller != null) {
            //mFastScroller.setScrollBarStyle(style);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mFastScroller != null) {
            mFastScroller.onSizeChanged(w, h, oldw, oldh);
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (mFastScroller != null) {
            boolean intercepted = mFastScroller.onInterceptTouchEvent(ev);
            if (intercepted) {
                return true;
            }
        }

        return super.onInterceptTouchEvent(ev);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (mFastScroller != null) {
            boolean intercepted = mFastScroller.onTouchEvent(event);
            if (intercepted) {
                return true;
            }
        }

        return super.onTouchEvent(event);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.id_up: {
                mScrollY -= 30;
                scrollTo(0, mScrollY);
            }
            break;
            case R.id.id_down: {
                mScrollY += 30;
                scrollTo(0, mScrollY);
            }
            break;
            case R.id.id_max: {
                mScrollRangeY += 50;
            }
            break;
            case R.id.id_min: {
                mScrollRangeY -= 50;
            }
            break;
        }

        //overScrollBy(0, 0, 0, mScrollY, 0, mScrollRangeY, 0, 0, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Rect rc = new Rect(100, 100, 200, 500);
        Paint paint = new Paint();
        paint.setColor(Color.GREEN);
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(rc, paint);

        if (mFastScroller != null) {
            mFastScroller.draw(canvas);
//            final int scrollY = mScrollY;
//            if (scrollY != 0) {
//                // Pin to the top/bottom during overscroll
//                int restoreCount = canvas.save();
//                canvas.translate(0, (float) scrollY);
//                mFastScroller.draw(canvas);
//                canvas.restoreToCount(restoreCount);
//            } else {
//                mFastScroller.draw(canvas);
//            }
        }
    }

}
