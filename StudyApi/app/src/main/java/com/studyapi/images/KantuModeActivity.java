package com.studyapi.images;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.studyapi.BaseActivity;
import com.studyapi.R;
import com.studyapi.images.view.GestureImageView;
import com.studyapi.images.view.ImageViewer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class KantuModeActivity extends BaseActivity  implements View.OnClickListener, ImageViewer.ImagerViewerListener{
    private List<GestureImageView> mImageViews;
    private List<Bitmap> mImages;
    ImageViewer mImageViewer;
    protected int[] mImgIds = new int[]{
            R.mipmap.guide_image_2, R.mipmap.image_viewer_1, R.mipmap.guide_image_3, R.mipmap.image_viewer_1, R.mipmap.guide_image_2, R.mipmap.image_viewer_1
    };

    HashMap<String, Bitmap> mImagesMap;
    Map<String, Bitmap> images2;

    View mBarLayoutView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        //setContentView(R.layout.image_viewer);

        initData();

        mImageViewer = (ImageViewer)findViewById(R.id.id_image_viewer);
        //mImageViewer.setPageMargin(80);
        mImageViewer.setAdapter(new ImageViewer.ImageViewerAdapter() {
            public int getCount() {
                return mImgIds.length;
            }

            public Bitmap instantiateBitmap(int index) {
                return mImages.get(index);
            }

            public void destroyBitmap(int index) {
            }
        });

/*
        mImageViewer.setAdapter(new PagerAdapter() {
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
                return view.equals(object);
            }
        });
*/

        mImageViewer.setListener(this);
    }

    private void initView() {

    }

    private void initData() {
        mImageViews = new ArrayList<>();
        mImages = new ArrayList<Bitmap>();
        for(int i=0; i<mImgIds.length; i++) {
            Bitmap bitmap = BitmapFactory. decodeResource(this.getResources(), mImgIds[i]);
            mImages.add(bitmap);

            GestureImageView iv = new GestureImageView(this);
            iv.setImageResource(mImgIds[i]);
            iv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
            //iv.setScaleType(ImageView.ScaleType.FIT_START);
            iv.setId(mImgIds[i]);
            mImageViews.add(iv);
        }
    }

    @Override
    public void onClick(View v) {
        Log.e("gqg:2", "ddd");
    }

    @Override
    public void onDownload(int index) {

    }
    @Override
    public void onShare(int index) {

    }
    @Override
    public void onShowInfo(int index) {

    }
    @Override
    public void onDownloadAll() {

    }
    @Override
    public void onExit() {
        finish();
    }
}
