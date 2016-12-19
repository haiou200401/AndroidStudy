package com.studyapi.images.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.studyapi.R;

/**
 * Created by gaoqingguang on 2016/7/12.
 */
public class ImageViewer extends FrameLayout implements View.OnClickListener{
    private Context mContext;
    private ViewPager mViewPager;
    private ImagerViewerListener mListener;
    private ImageViewerAdapter mImageViewerAdapter;

    private View mTitleBar, mBottomBar;
    private TextView mTitle;

    public interface ImagerViewerListener {
        public void onDownload(int index);
        public void onShare(int index);
        public void onShowInfo(int index);
        public void onDownloadAll();
        public void onExit();
    }

    public interface ImageViewerAdapter {
        public int getCount();
        public Bitmap instantiateBitmap(int index);
        public void destroyBitmap(int index);
    }

    public ImageViewer(Context context) {
        super(context);
        init(context);
    }

    public ImageViewer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init(context);
    }

    private void init(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        inflater.inflate(R.layout.image_viewer, this);

        mViewPager = (ViewPager) findViewById(R.id.id_view_pager);
        mViewPager.setPageMargin(80);

        mViewPager.setOnClickListener(this);
        findViewById(R.id.id_exit).setOnClickListener(this);
        findViewById(R.id.id_image_download).setOnClickListener(this);
        findViewById(R.id.id_image_share).setOnClickListener(this);
        findViewById(R.id.id_image_info).setOnClickListener(this);

        mTitleBar = findViewById(R.id.id_title_bar);
        mBottomBar = findViewById(R.id.id_bottom_bar);
        mTitle = (TextView) findViewById(R.id.id_title);

        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                updateTitle();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    public void setAdapter(ImageViewerAdapter imageViewerAdapter) {
        mImageViewerAdapter = imageViewerAdapter;
        if (null != mImageViewerAdapter) {
            mViewPager.setAdapter(new PagerAdapter() {
                @Override
                public int getCount() {
                    return mImageViewerAdapter.getCount();
                }

                @Override
                public Object instantiateItem(ViewGroup container, int position) {
                    Bitmap bitmap = mImageViewerAdapter.instantiateBitmap(position);
                    if (null != bitmap) {
                        GestureImageView giv = new GestureImageView(mContext);
                        giv.setOnClickListener(new OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                int toolbarVisibility = View.VISIBLE;
                                if (mTitleBar.getVisibility() == View.VISIBLE) {
                                    toolbarVisibility = View.INVISIBLE;
                                }
                                mTitleBar.setVisibility(toolbarVisibility);
                                mBottomBar.setVisibility(toolbarVisibility);
                            }
                        });
                        giv.setImageBitmap(bitmap);
                        giv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
                        container.addView(giv);
                        return giv;
                    }

                    return null;
                }

                @Override
                public void destroyItem(ViewGroup container, int position, Object object) {
                    if (object instanceof GestureImageView) {
                        container.removeView((View)object);
                    }
                    mImageViewerAdapter.destroyBitmap(position);
                }

                @Override
                public boolean isViewFromObject(View view, Object object) {
                    return view.equals(object);
                }
            });
        } else {
            mViewPager.setAdapter(null);
        }

        updateTitle();
    }

    public void setListener(ImagerViewerListener listener) {
        mListener = listener;
    }

    private void updateTitle() {
        if (mViewPager.getAdapter() == null)
            return;

        int count = mViewPager.getAdapter().getCount();
        int currentItem = mViewPager.getCurrentItem();
        String title = String.valueOf(currentItem+1) + "/" + String.valueOf(count);
        mTitle.setText(title);
    }

    @Override
    public void onClick(View v) {
        if (null == mListener)
            return;

        if (v.getId() == R.id.id_exit) {
            mListener.onExit();
        } else if (v.getId() == R.id.id_image_download) {
            mListener.onDownload(mViewPager.getCurrentItem());
        } else if (v.getId() == R.id.id_image_share) {
            mListener.onShare(mViewPager.getCurrentItem());
        } else if (v.getId() == R.id.id_image_info) {
            mListener.onShowInfo(mViewPager.getCurrentItem());
        }
    }
}
