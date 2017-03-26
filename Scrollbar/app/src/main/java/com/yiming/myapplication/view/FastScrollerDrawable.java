package com.yiming.myapplication.view;

import android.os.SystemClock;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import com.yiming.myapplication.R;

import java.lang.reflect.Method;

/**
 * Helper class for ViewGroup to draw and control the Fast Scroll thumb
 */
class FastScrollerDrawable {
    private static final String TAG = "FastScrollerDrawable";

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

    private static final int[] PRESSED_STATES = new int[] {
            android.R.attr.state_pressed
    };

    private static final int[] DEFAULT_STATES = new int[0];

    private static final int[] ATTRS = new int[] {
            android.R.attr.fastScrollTextColor,
            android.R.attr.fastScrollThumbDrawable,
            android.R.attr.fastScrollTrackDrawable,
            android.R.attr.fastScrollPreviewBackgroundLeft,
            android.R.attr.fastScrollPreviewBackgroundRight,
            android.R.attr.fastScrollOverlayPosition
    };

    private static final int TEXT_COLOR = 0;
    private static final int THUMB_DRAWABLE = 1;
    private static final int TRACK_DRAWABLE = 2;
    private static final int PREVIEW_BACKGROUND_LEFT = 3;
    private static final int PREVIEW_BACKGROUND_RIGHT = 4;
    private static final int OVERLAY_POSITION = 5;

    private static final int OVERLAY_FLOATING = 0;
    private static final int OVERLAY_AT_THUMB = 1;

    private Drawable mThumbDrawable;
    private Drawable mTrackDrawable;

    int mThumbH;
    int mThumbW;
    int mThumbY;

    ViewGroup mViewGroup;
    boolean mScrollCompleted;
    private Paint mPaint;
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
            if (mViewGroup.isAttachedToWindow()) { //mViewGroup.mIsAttached) {
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

    public FastScrollerDrawable(Context context, ViewGroup listView) {
        mViewGroup = listView;
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

    private void refreshDrawableState() {
        int[] state = mState == STATE_DRAGGING ? PRESSED_STATES : DEFAULT_STATES;

        if (mThumbDrawable != null && mThumbDrawable.isStateful()) {
            mThumbDrawable.setState(state);
        }
        if (mTrackDrawable != null && mTrackDrawable.isStateful()) {
            mTrackDrawable.setState(state);
        }
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
        refreshDrawableState();
    }

    public int getState() {
        return mState;
    }

    private void resetThumbPos() {
        final int viewWidth = mViewGroup.getWidth();
        // Bounds are always top right. Y coordinate get's translated during draw
        mThumbDrawable.setBounds(viewWidth - mThumbW, 0, viewWidth, mThumbH);
        mThumbDrawable.setAlpha(ScrollFade.ALPHA_MAX);
    }

    private void useThumbDrawable(Context context, Drawable drawable) {
        mThumbDrawable = drawable;
//        if (drawable instanceof NinePatchDrawable) {
            mThumbW = context.getResources().getDimensionPixelSize(
                    R.dimen.fastscroll_thumb_width);
            mThumbH = context.getResources().getDimensionPixelSize(
                    R.dimen.fastscroll_thumb_height);
//        } else {
//            mThumbW = drawable.getIntrinsicWidth();
//            mThumbH = drawable.getIntrinsicHeight();
//        }
        mChangedBounds = true;
    }

    private void init(Context context) {
        // Get both the scrollbar states drawables
        TypedArray ta = context.getTheme().obtainStyledAttributes(ATTRS);
        useThumbDrawable(context, ta.getDrawable(THUMB_DRAWABLE));
        //mTrackDrawable = ta.getDrawable(TRACK_DRAWABLE);

        mScrollCompleted = true;
        mScrollFade = new ScrollFade();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        ColorStateList textColor = ta.getColorStateList(TEXT_COLOR);
        int textColorNormal = textColor.getDefaultColor();
        mPaint.setColor(textColorNormal);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // to show mOverlayDrawable properly
        if (mViewGroup.getWidth() > 0 && mViewGroup.getHeight() > 0) {
            onSizeChanged(mViewGroup.getWidth(), mViewGroup.getHeight(), 0, 0);
        }

        mState = STATE_NONE;
        refreshDrawableState();

        ta.recycle();

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

        canvas.save();
        int scrollY = mViewGroup.getScrollY();
        canvas.translate(0, scrollY);
        drawScrollBar(canvas);
        canvas.restore();

    }

    void drawScrollBar(Canvas canvas) {

        final int y = mThumbY;
        final int viewWidth = mViewGroup.getWidth();
        final FastScrollerDrawable.ScrollFade scrollFade = mScrollFade;

        int alpha = -1;
        if (mState == STATE_EXIT) {
            alpha = scrollFade.getAlpha();
            if (alpha < ScrollFade.ALPHA_MAX / 2) {
                mThumbDrawable.setAlpha(alpha * 2);
            }
            int left = viewWidth - (mThumbW * alpha) / ScrollFade.ALPHA_MAX;
            mThumbDrawable.setBounds(left, 0, left + mThumbW, mThumbH);
            mChangedBounds = true;
        }

        if (mTrackDrawable != null) {
            final Rect thumbBounds = mThumbDrawable.getBounds();
            final int left = thumbBounds.left;
            final int halfThumbHeight = (thumbBounds.bottom - thumbBounds.top) / 2;
            final int trackWidth = mTrackDrawable.getIntrinsicWidth();
            final int trackLeft = (left + mThumbW / 2) - trackWidth / 2;
            mTrackDrawable.setBounds(trackLeft, halfThumbHeight,
                    trackLeft + trackWidth, mViewGroup.getHeight() - halfThumbHeight);
            mTrackDrawable.draw(canvas);
        }

        canvas.translate(0, y);
        mThumbDrawable.draw(canvas);
        canvas.translate(0, -y);

        if (mState == STATE_EXIT) {
            if (alpha == 0) { // Done with exit
                setState(STATE_NONE);
            } else if (mTrackDrawable != null) {
                mViewGroup.invalidate(viewWidth - mThumbW, 0, viewWidth, mViewGroup.getHeight());
            } else {
                onlyInvalidateThumb();
            }
        }
    }

    void onSizeChanged(int w, int h, int oldw, int oldh) {
        if (mThumbDrawable != null) {
            mThumbDrawable.setBounds(w - mThumbW, 0, w, mThumbH);
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
            //mViewGroup.reportScrollStateChange(OnScrollListener.SCROLL_STATE_TOUCH_SCROLL);
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
        boolean inside = inTrack && (mTrackDrawable != null || y >= mThumbY && y <= mThumbY + mThumbH);
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
