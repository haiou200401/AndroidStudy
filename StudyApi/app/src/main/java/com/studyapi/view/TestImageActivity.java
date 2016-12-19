package com.studyapi.view;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.studyapi.BaseActivity;
import com.studyapi.R;

import java.io.InputStream;

public class TestImageActivity extends BaseActivity implements View.OnClickListener{
    final String TAG = "gqg:i";
    ImageView mImageView1;
    ImageView mImageView2;

    AssetManager assetManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_image);
        findViewById(R.id.id_testimage_start).setOnClickListener(this);
        mImageView1 = (ImageView)findViewById(R.id.id_test_image_imageview1);
        mImageView2 = (ImageView)findViewById(R.id.id_test_image_imageview2);

        assetManager = this.getAssets();
    }

    @Override
    public void onClick(View v) {
        long t1, t2, t;
        try {
            InputStream in;
            Bitmap bm;

            in = assetManager.open("images/splash_comp.png");
            t1 = System.currentTimeMillis();
            bm = BitmapFactory.decodeStream(in);
            t2 = System.currentTimeMillis();
            Log.e(TAG, "splash_comp decodeStream time = " + String.valueOf(t2 - t1) + "  ;w = " + String.valueOf(bm.getWidth()));

            in = assetManager.open("images/splash_org.png");
            t1 = System.currentTimeMillis();
            bm = BitmapFactory.decodeStream(in);
            t2 = System.currentTimeMillis();
            Log.e(TAG, "splash_org decodeStream time = " + String.valueOf(t2 - t1) + "  ;w = " + String.valueOf(bm.getWidth()));

            in = assetManager.open("images/splash_comp.png");
            t1 = System.currentTimeMillis();
            bm = BitmapFactory.decodeStream(in);
            t2 = System.currentTimeMillis();
            Log.e(TAG, "splash_comp decodeStream time = " + String.valueOf(t2 - t1) + "  ;w = " + String.valueOf(bm.getWidth()));

            in = assetManager.open("images/splash_org.png");
            t1 = System.currentTimeMillis();
            bm = BitmapFactory.decodeStream(in);
            t2 = System.currentTimeMillis();
            Log.e(TAG, "splash_org decodeStream time = " + String.valueOf(t2 - t1) + "  ;w = " + String.valueOf(bm.getWidth()));


            mImageView1.setImageBitmap(bm);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
