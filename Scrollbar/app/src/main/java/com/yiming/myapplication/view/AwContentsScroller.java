package com.yiming.myapplication.view;

import android.graphics.Color;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.SystemClock;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;

import java.lang.reflect.Method;

/**
 * Helper class for ViewGroup to draw and control the Fast Scroll thumb
 */
class AwContentsScroller {
    private static final String TAG = "AwContentsScroller";

    private final float THUMB_MARGIN_MIN_DIP = 2f;
    private final float THUMB_MARGIN_LEFT_DIP = 6f;
    private final float THUMB_WIDTH_DIP = 28.0f + THUMB_MARGIN_LEFT_DIP + THUMB_MARGIN_MIN_DIP; // dp
    private final float THUMB_HEIGHT_DIP = 46.0f + THUMB_MARGIN_MIN_DIP * 2; // dp


    private final int THUMB_COLOR_BG_NORMAL = Color.argb(64, 168, 168, 168);
    private final int THUMB_COLOR_BG_PRESS = Color.argb(132, 168, 168, 168);
    private final int THUMB_COLOR_ARROWS_NORMAL = Color.argb(218, 146, 146, 146);
    private final int THUMB_COLOR_ARROWS_PRESS = Color.argb(218, 246, 246, 246);

    // Minimum number of pages to justify showing a fast scroll thumb
    private static int MIN_PAGES = 4;
    // Scroll thumb not showing
    private static final int STATE_NONE = 0;
    // Not implemented yet - fade-in transition
    private static final int STATE_ENTER = 1;
    // Scroll thumb visible and moving along with the scrollbar
    private static final int STATE_VISIBLE = 2;
    // Scroll thumb being dragged by user
    private static final int STATE_DRAGGING = 3;
    // Scroll thumb fading out due to inactivity timeout
    private static final int STATE_EXIT = 4;

    int mThumbH;
    int mThumbW;
    int mThumbY;
    Rect mThumbBound;
    int mThumbAlpha;

    int mThumbMarginMin;
    int mThumbMarginLeft;

    ViewGroup mViewGroup;
    boolean mScrollCompleted;
    private boolean mLongList;

    private ScrollFade mScrollFade;

    private int mState;

    private Handler mHandler = new Handler();

    private boolean mChangedBounds;
    private boolean mAlwaysShow;

    float mInitialTouchY;
    boolean mPendingDrag;
    private int mScaledTouchSlop;

    private int mScrollRange = 0;
    private int mPressOffsetInThumb; //todo:gqg;


    private static final int FADE_TIMEOUT = 1500;
    private static final int PENDING_DRAG_DELAY = 180;

    private final Runnable mDeferStartDrag = new Runnable() {
        public void run() {
            if (mViewGroup.isAttachedToWindow()) {
                beginDrag();

                final int viewHeight = mViewGroup.getHeight();
                // Jitter
                int newThumbY = (int) mInitialTouchY - mThumbH + 10;
                if (newThumbY < 0) {
                    newThumbY = 0;
                } else if (newThumbY + mThumbH > viewHeight) {
                    newThumbY = viewHeight - mThumbH;
                }
                mThumbY = newThumbY;
                scrollTo((float) mThumbY / (viewHeight - mThumbH));
            }

            mPendingDrag = false;
        }
    };

    public AwContentsScroller(Context context, ViewGroup viewGroup) {
        mViewGroup = viewGroup;
        init(context);
    }

    public void setAlwaysShow(boolean alwaysShow) {
        mAlwaysShow = alwaysShow;
        if (alwaysShow) {
            mHandler.removeCallbacks(mScrollFade);
            setState(STATE_VISIBLE);
        } else if (mState == STATE_VISIBLE) {
            mHandler.postDelayed(mScrollFade, FADE_TIMEOUT);
        }
    }

    public boolean isAlwaysShowEnabled() {
        return mAlwaysShow;
    }

    public int getWidth() {
        return mThumbW;
    }

    public void setState(int state) {
        switch (state) {
            case STATE_NONE:
                mHandler.removeCallbacks(mScrollFade);
                mViewGroup.invalidate();
                break;
            case STATE_VISIBLE:
                if (mState != STATE_VISIBLE) { // Optimization
                    resetThumbPos();
                }
                // Fall through
            case STATE_DRAGGING:
                mHandler.removeCallbacks(mScrollFade);
                break;
            case STATE_EXIT:
                onlyInvalidateThumb();
                break;
        }
        mState = state;
    }

    public int getState() {
        return mState;
    }

    private void resetThumbPos() {
        final int viewWidth = mViewGroup.getWidth();
        // Bounds are always top right. Y coordinate get's translated during draw
        mThumbBound.set(viewWidth - mThumbW, 0, viewWidth, mThumbH);
        mThumbAlpha = ScrollFade.ALPHA_MAX;
    }

    private void initThumbDrawable(Context context) {
        final float scale = context.getResources().getDisplayMetrics().density;
        mThumbW = (int)(THUMB_WIDTH_DIP * scale + 0.5f);
        mThumbH = (int)(THUMB_HEIGHT_DIP * scale + 0.5f);

        mThumbMarginMin = (int)(THUMB_MARGIN_MIN_DIP * scale + 0.5f);
        mThumbMarginLeft = (int)(THUMB_MARGIN_LEFT_DIP * scale + 0.5f);
        mChangedBounds = true;

        mThumbBound = new Rect();
    }

    private void init(Context context) {
        initThumbDrawable(context);

        mScrollCompleted = true;
        mScrollFade = new ScrollFade();

        // to show mOverlayDrawable properly
        if (mViewGroup.getWidth() > 0 && mViewGroup.getHeight() > 0) {
            onSizeChanged(mViewGroup.getWidth(), mViewGroup.getHeight(), 0, 0);
        }

        mState = STATE_NONE;

        mScaledTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
    }

    void stop() {
        setState(STATE_NONE);
    }

    boolean isVisible() {
        return !(mState == STATE_NONE);
    }

    private void onlyInvalidateThumb() {
        int viewWidth = mViewGroup.getWidth();
        int scrollY = mViewGroup.getScrollY();
        int y = mThumbY + scrollY;
        mViewGroup.invalidate(viewWidth - mThumbW, y, viewWidth, y + mThumbH);
    }

    public void draw(Canvas canvas) {

        if (mState == STATE_NONE) {
            // No need to draw anything
            return;
        }

        int scrollY = mViewGroup.getScrollY();
        canvas.translate(0, scrollY);
        drawScrollBar(canvas);
        canvas.translate(0, -scrollY);
    }

    void drawScrollBar(Canvas canvas) {

        final int y = mThumbY;
        final int viewWidth = mViewGroup.getWidth();
        final AwContentsScroller.ScrollFade scrollFade = mScrollFade;

        int alpha = -1;
        if (mState == STATE_EXIT) {
            alpha = scrollFade.getAlpha();
            if (alpha < ScrollFade.ALPHA_MAX / 2) {
                mThumbAlpha = alpha * 2;
            }
            int left = viewWidth - (mThumbW * alpha) / ScrollFade.ALPHA_MAX;
            mThumbBound.set(left, 0, left + mThumbW, mThumbH);
            mChangedBounds = true;
        }

        Rect bound = mThumbBound;

        canvas.translate(0, y);
        RectF rectF = new RectF(bound.left + mThumbMarginLeft, bound.top + mThumbMarginMin,
                bound.right - mThumbMarginMin, bound.bottom - mThumbMarginMin);
        drawThumb(canvas, rectF, mThumbAlpha);

        canvas.translate(0, -y);

        if (mState == STATE_EXIT) {
            if (alpha == 0) { // Done with exit
                setState(STATE_NONE);
            } else {
                onlyInvalidateThumb();
            }
        }
    }

    void drawThumb(Canvas canvas, final RectF boundf, int alpha) {
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setAntiAlias(true);
        paint.setAlpha(alpha);

        if (STATE_DRAGGING == mState) {
            paint.setColor(THUMB_COLOR_BG_PRESS);
        } else {
            paint.setColor(THUMB_COLOR_BG_NORMAL);
        }

        // draw thumb background.
        float radus = boundf.width()/8.0f; //5.0f;
        canvas.drawRoundRect(boundf, radus, radus, paint);


        // begin draw arrows & lines.
        if (STATE_DRAGGING == mState) {
            paint.setColor(THUMB_COLOR_ARROWS_PRESS);
        } else {
            paint.setColor(THUMB_COLOR_ARROWS_NORMAL);
        }

        float oneThird = boundf.height() / 3.0f;
        float xChenter = boundf.width() / 2.0f + boundf.left;
        float arrowsHeight = (oneThird / 5.0f);
        float arrowsWidth = arrowsHeight * 1.6f;
        float arrowsLeft = (boundf.width() - arrowsWidth) / 2.0f + boundf.left;
        float arrowsRight = arrowsLeft + arrowsWidth;

        // up arrows
        Path path = new Path();
        path.moveTo(xChenter, oneThird - arrowsHeight + boundf.top); // top point.
        path.lineTo(arrowsLeft, oneThird + boundf.top);
        path.lineTo(arrowsRight, oneThird + boundf.top);
        path.close();

        // down arrows
        path.moveTo(xChenter, oneThird * 2 + arrowsHeight + boundf.top);
        path.lineTo(arrowsLeft, oneThird * 2 + boundf.top);
        path.lineTo(arrowsRight, oneThird * 2 + boundf.top);
        path.close();

        // stroke arrows
        canvas.drawPath(path, paint);

        // center line
        float lineWidth = oneThird / 8.0f;
        float lineLength = boundf.width() * 0.42f;
        float lineLeft = (boundf.width() - lineLength) / 2.0f + boundf.left;
        float lineRight = lineLeft + lineLength;
        float line1Y = oneThird + oneThird / 3.0f + boundf.top;
        float line2Y = oneThird + oneThird / 3.0f * 2.0f + boundf.top;
        paint.setStrokeWidth(lineWidth);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeCap(Paint.Cap.ROUND);
        canvas.drawLine(lineLeft, line1Y, lineRight, line1Y, paint);
        canvas.drawLine(lineLeft, line2Y, lineRight, line2Y, paint);
    }


    void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mThumbBound != null) {
            mThumbBound.set(w - mThumbW, 0, w, mThumbH);
        }
    }

    private int computeThumbY(int scrollY) {
        if (mScrollRange <= 0)
            return 0;

        int viewHeight = mViewGroup.getHeight();
        int thumbY = (int)(scrollY / (float)mScrollRange * (viewHeight - mThumbH));
        return thumbY;
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt) {
        if (mState != STATE_DRAGGING) {
            mThumbY = computeThumbY(t);
            if (mChangedBounds) {
                resetThumbPos();
                mChangedBounds = false;
            }
        }
        if (mAlwaysShow) {
            mLongList = true;
        }

        mScrollCompleted = true;

        if (mState != STATE_DRAGGING) {
            setState(STATE_VISIBLE);
            if (!mAlwaysShow) {
                mHandler.postDelayed(mScrollFade, FADE_TIMEOUT);
            }
        }
    }

    void scrollTo(float position) {
        mScrollCompleted = true;
        int scrollTo = (int)(position * mScrollRange);
        mViewGroup.scrollTo(0, scrollTo);

    }

    private void cancelFling() {
        // Cancel the list fling
        MotionEvent cancelFling = MotionEvent.obtain(0, 0, MotionEvent.ACTION_CANCEL, 0, 0, 0);
        mViewGroup.onTouchEvent(cancelFling);
        cancelFling.recycle();
    }

    void cancelPendingDrag() {
        mViewGroup.removeCallbacks(mDeferStartDrag);
        mPendingDrag = false;
    }

    void startPendingDrag() {
        mPendingDrag = true;
        mViewGroup.postDelayed(mDeferStartDrag, PENDING_DRAG_DELAY);
    }

    void beginDrag() {
        setState(STATE_DRAGGING);
        if (mViewGroup != null) {
            mViewGroup.requestDisallowInterceptTouchEvent(true);
        }

        cancelFling();
    }

    private boolean isInScrollingContainer() {
        try {
            // gloam:
            Method isInScrollingContainerMethod = View.class.getMethod("isInScrollingContainer");
            return (Boolean)isInScrollingContainerMethod.invoke(mViewGroup);
        } catch (Exception exp) {
            exp.printStackTrace();
        }
        return false;
    }

    boolean onInterceptTouchEvent(MotionEvent ev) {
        switch (ev.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (mState > STATE_NONE && isPointInside(ev.getX(), ev.getY())) {
                    if (!isInScrollingContainer()) {
                        beginDrag();
                        return true;
                    }
                    mInitialTouchY = ev.getY();
                    startPendingDrag();
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                cancelPendingDrag();
                break;
        }
        return false;
    }

    boolean onTouchEvent(MotionEvent me) {
        if (mState == STATE_NONE) {
            return false;
        }

        final int action = me.getAction();

        if (action == MotionEvent.ACTION_DOWN) {
            if (isPointInside(me.getX(), me.getY())) {
                if (!isInScrollingContainer()) {
                    beginDrag();
                    return true;
                }
                mInitialTouchY = me.getY();
                startPendingDrag();
            }
        } else if (action == MotionEvent.ACTION_UP) { // don't add ACTION_CANCEL here
            if (mPendingDrag) {
                // Allow a tap to scroll.
                beginDrag();

                final int viewHeight = mViewGroup.getHeight();
                // Jitter
                int newThumbY = (int) me.getY() - mThumbH + 10;
                if (newThumbY < 0) {
                    newThumbY = 0;
                } else if (newThumbY + mThumbH > viewHeight) {
                    newThumbY = viewHeight - mThumbH;
                }
                mThumbY = newThumbY;
                scrollTo((float) mThumbY / (viewHeight - mThumbH));

                cancelPendingDrag();
                // Will hit the STATE_DRAGGING check below
            }
            if (mState == STATE_DRAGGING) {
                if (mViewGroup != null) {
                    // ViewGroup does the right thing already, but there might
                    // be other classes that don't properly reset on touch-up,
                    // so do this explicitly just in case.
                    mViewGroup.requestDisallowInterceptTouchEvent(false);
                    //mViewGroup.reportScrollStateChange(OnScrollListener.SCROLL_STATE_IDLE);
                }
                setState(STATE_VISIBLE);
                final Handler handler = mHandler;
                handler.removeCallbacks(mScrollFade);
                if (!mAlwaysShow) {
                    handler.postDelayed(mScrollFade, 1000);
                }

                mViewGroup.invalidate();
                return true;
            }
        } else if (action == MotionEvent.ACTION_MOVE) {
            if (mPendingDrag) {
                final float y = me.getY();
                if (Math.abs(y - mInitialTouchY) > mScaledTouchSlop) {
                    setState(STATE_DRAGGING);
                    if (mViewGroup != null) {
                        mViewGroup.requestDisallowInterceptTouchEvent(true);
                        //mViewGroup.reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
                    }

                    cancelFling();
                    cancelPendingDrag();
                    // Will hit the STATE_DRAGGING check below
                }
            }
            if (mState == STATE_DRAGGING) {
                final int viewHeight = mViewGroup.getHeight();
                // Jitter
                //int newThumbY = (int) me.getY() - mThumbH + 10;
                int newThumbY = (int)me.getY() - mPressOffsetInThumb;
                if (newThumbY < 0) {
                    newThumbY = 0;
                } else if (newThumbY + mThumbH > viewHeight) {
                    newThumbY = viewHeight - mThumbH;
                }
                if (Math.abs(mThumbY - newThumbY) < 2) {
                    return true;
                }
                mThumbY = newThumbY;
                // If the previous scrollTo is still pending
                if (mScrollCompleted) {
                    scrollTo((float) mThumbY / (viewHeight - mThumbH));
                }
                return true;
            }
        } else if (action == MotionEvent.ACTION_CANCEL) {
            cancelPendingDrag();
        }
        return false;
    }

    boolean isPointInside(float x, float y) {
        boolean inTrack = x > mViewGroup.getWidth() - mThumbW;;

        // Allow taps in the track to start moving.
        boolean inside = inTrack && (y >= mThumbY && y <= mThumbY + mThumbH);
        mPressOffsetInThumb = (int)y - mThumbY;
        return inside;
    }

    // updateLongList
    public void updateScrollRange(int scrollRange) {
        if (mScrollRange != scrollRange) {
            mScrollRange = scrollRange;
        }
    }

    public class ScrollFade implements Runnable {
        long mStartTime;
        long mFadeDuration;
        static final int ALPHA_MAX = 208;
        static final long FADE_DURATION = 200;

        void startFade() {
            mFadeDuration = FADE_DURATION;
            mStartTime = SystemClock.uptimeMillis();
            setState(STATE_EXIT);
        }

        int getAlpha() {
            if (getState() != STATE_EXIT) {
                return ALPHA_MAX;
            }
            int alpha;
            long now = SystemClock.uptimeMillis();
            if (now > mStartTime + mFadeDuration) {
                alpha = 0;
            } else {
                alpha = (int) (ALPHA_MAX - ((now - mStartTime) * ALPHA_MAX) / mFadeDuration);
            }
            return alpha;
        }

        public void run() {
            if (getState() != STATE_EXIT) {
                startFade();
                return;
            }

            if (getAlpha() > 0) {
                mViewGroup.invalidate();
            } else {
                setState(STATE_NONE);
            }
        }
    }
}
