package com.studyapi.anim;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewConfigurationCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.Scroller;

public class HistorySlidingView extends ViewGroup {
    private Bitmap mLeftBitmap;
    private Bitmap mCenterBitmap;
    private Bitmap mRightBitmap;
    private Paint mBitmapPaint;

    private int mCurrentPage = 1; // 0, 1 or 2;
    private int mStartX;

    private static final int MIN_DISTANCE_FOR_FLING = 46; // dips
    private static final int MIN_FLING_VELOCITY = 400; // dips
    private static final int MAX_SETTLE_DURATION = 600; // ms
    private float mInitialMotionX;
    private float mLastMotionX;
    private float mOffsetX;

    private int mMinimumVelocity;
    private int mMaximumVelocity;
    private VelocityTracker mVelocityTracker;
    private int mActivePointerId = -1;
    private int mFlingDistance;

    private Scroller mScroller;
    public static final int SCROLL_STATE_IDLE = 0;
    public static final int SCROLL_STATE_DRAGGING = 1;
    public static final int SCROLL_STATE_SETTLING = 2;
    private int mScrollState = SCROLL_STATE_IDLE;

    private final Runnable mEndScrollRunnable = new Runnable() {
        public void run() {
            setScrollState(SCROLL_STATE_IDLE);
        }
    };
    private static final Interpolator sInterpolator = new Interpolator() {
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };

    public HistorySlidingView(Context context) {
        super(context);
        initHistorySlidingView(context);
    }
    public HistorySlidingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initHistorySlidingView(context);
    }

    public void setLeftBitmap(Bitmap bitmap) {
        mLeftBitmap = bitmap;
    }
    public void setCenterBitmap(Bitmap bitmap) {
        mCenterBitmap = bitmap;
    }
    public void setRightBitmap(Bitmap bitmap) {
        mRightBitmap = bitmap;
    }

    private void initHistorySlidingView(Context context) {
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        final float density = context.getResources().getDisplayMetrics().density;
        mFlingDistance = (int) (MIN_DISTANCE_FOR_FLING * density);
        mScroller = new Scroller(context, sInterpolator);

        final ViewConfiguration configuration = ViewConfiguration.get(context);
        mMinimumVelocity = (int) (MIN_FLING_VELOCITY * density);
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        setAlpha(0.2f);

        reset();
    }

    public void reset() {
        setCurrentPage(1);
        mStartX = getDefaultStart();
        if (!mScroller.isFinished())
            mScroller.abortAnimation();

        setScrollState(SCROLL_STATE_IDLE);
    }

    private void setScrollState(int newState) {
        if (mScrollState == newState) {
            return;
        }

        mScrollState = newState;
        dispatchOnScrollStateChanged(newState);
    }

    private void dispatchOnScrollStateChanged(int state) {
    }

    private int getDefaultStart() {
        switch (mCurrentPage) {
            case 0:
                return 0;
            case 2:
                return -getBitmapWidth()*2;
        }

        return -getBitmapWidth();
    }

    private int getBitmapWidth() {
        if (null != mLeftBitmap) {
            return mLeftBitmap.getWidth();
        }
        if (null != mCenterBitmap) {
            return mCenterBitmap.getWidth();
        }
        if (null != mRightBitmap) {
            return mRightBitmap.getWidth();
        }
        return getWidth();
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        reset();
    }

    @Override
    public void computeScroll() {
        if (!mScroller.isFinished() && mScroller.computeScrollOffset()) {
            int oldX = mStartX;
            int x = mScroller.getCurrX();
            if (oldX != x) {
                mStartX = x;
            }

            // Keep on drawing until the animation has finished.
            ViewCompat.postInvalidateOnAnimation(this);
            return;
        }

        // Done with scroll, clean up state.
        completeScroll(true);
    }

    private void completeScroll(boolean postEvents) {
        boolean needPopulate = mScrollState == SCROLL_STATE_SETTLING;
        if (needPopulate) {
            // Done with scroll, no longer want to cache view drawing.
            mScroller.abortAnimation();
            int oldX = mStartX;
            int x = mScroller.getCurrX();
            if (oldX != x) {
                mStartX = oldX;
            }
        }

        if (needPopulate) {
            if (postEvents) {
                ViewCompat.postOnAnimation(this, mEndScrollRunnable);
            } else {
                mEndScrollRunnable.run();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        int action = ev.getAction() & 255;
        int oldStartX = mStartX;

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mLastMotionX = mInitialMotionX = ev.getX();
                mActivePointerId = MotionEventCompat.getPointerId(ev, 0);
                mOffsetX = mLastMotionX - mStartX;
                mScroller.abortAnimation();
                setScrollState(SCROLL_STATE_DRAGGING);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                mLastMotionX = ev.getX();
                mStartX = (int)(mLastMotionX - mOffsetX);
                setScrollState(SCROLL_STATE_DRAGGING);
                break;
            }
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                final int totalDelta = (int) (mLastMotionX - mInitialMotionX);
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);
                int initialVelocity = (int) VelocityTrackerCompat.getXVelocity(
                        velocityTracker, mActivePointerId);
                int targetPosition = determineTargetStart(mCurrentPage, initialVelocity, totalDelta);
                smoothScrollTo(targetPosition, initialVelocity);
                break;
            }
        }

        if (oldStartX != mStartX) {
            invalidate();
        }

        return true;
    }

    private int determineTargetStart(int currentPage, int velocity, int deltaX) {
        int targetPage = 0;
        if (deltaX < -mFlingDistance) {
            // to right
            setCurrentPage(currentPage+1);
        } else if (deltaX > mFlingDistance) {
            // to left
            setCurrentPage(currentPage-1);
        } else {
            // reset.
        }
        return getDefaultStart();

/*
        if (Math.abs(deltaX) > mFlingDistance && Math.abs(velocity) > mMinimumVelocity) {
            targetPage = velocity > 0 ? currentPage : currentPage + 1;
        } else {
            final float truncator = currentPage >= mCurItem ? 0.4f : 0.6f;
            targetPage = (int) (currentPage + pageOffset + truncator);
        }

        if (mItems.size() > 0) {
            final ItemInfo firstItem = mItems.get(0);
            final ItemInfo lastItem = mItems.get(mItems.size() - 1);

            // Only let the user target pages we have items for
            targetPage = Math.max(firstItem.position, Math.min(targetPage, lastItem.position));
        }
*/

        //return targetPage;
    }

    private void setCurrentPage(int newPage) {
        if (mCurrentPage == newPage)
            return;
        if (newPage > 2 || newPage < 0)
            return;
        mCurrentPage = newPage;
    }

    // We want the duration of the page snap animation to be influenced by the distance that
    // the screen has to travel, however, we don't want this duration to be effected in a
    // purely linear fashion. Instead, we use this method to moderate the effect that the distance
    // of travel has on the overall snap duration.
    float distanceInfluenceForSnapDuration(float f) {
        f -= 0.5f; // center the values about 0.
        f *= 0.3f * Math.PI / 2.0f;
        return (float) Math.sin(f);
    }

    void smoothScrollTo(int x, int velocity) {
        int sx = mStartX;
        int dx = x - sx;
        if (dx == 0) {
            return;
        }

        final int width = getBitmapWidth(); //getClientWidth();
        final int halfWidth = width / 2;
        final float distanceRatio = Math.min(1f, 1.0f * Math.abs(dx) / width);
        final float distance = halfWidth + halfWidth *
                distanceInfluenceForSnapDuration(distanceRatio);

        int duration = 0;
        velocity = Math.abs(velocity);
        if (velocity > 0) {
            duration = 4 * Math.round(1000 * Math.abs(distance / velocity));
        } else {
            final float pageWidth = getBitmapWidth(); //width * mAdapter.getPageWidth(mCurItem);
            final float pageDelta = (float) Math.abs(dx) / (pageWidth);
            duration = (int) ((pageDelta + 1) * 100);
        }
        duration = Math.min(duration, MAX_SETTLE_DURATION);

        mScroller.startScroll(sx, 0, dx, 0, duration);
        ViewCompat.postInvalidateOnAnimation(this);
        setScrollState(SCROLL_STATE_SETTLING);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        drawContent(canvas);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawContent(canvas);
    }

    private void drawContent(Canvas canvas) {
        int bitmapWidth = getBitmapWidth();
        int viewHeight = getHeight();
        float left = mStartX;
        if (null != mLeftBitmap) {
            canvas.drawBitmap(mLeftBitmap, transformPosition(left), viewHeight - mLeftBitmap.getHeight(), mBitmapPaint);
        }

        left += bitmapWidth;
        if (null != mCenterBitmap) {
            canvas.drawBitmap(mCenterBitmap, transformPosition(left), viewHeight - mCenterBitmap.getHeight(), mBitmapPaint);
        }

        left += bitmapWidth;
        if (null != mRightBitmap) {
            canvas.drawBitmap(mRightBitmap, transformPosition(left), viewHeight - mCenterBitmap.getHeight(), mBitmapPaint);
        }
    }
    private float transformPosition(float left) {
        if (left < 0 && left > -getBitmapWidth())
            return left * 0.32f;
        return left;
    }

}