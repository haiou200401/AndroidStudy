package com.yiming.jnitest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.os.Environment;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;

/**
 * Created by gaoqingguang on 2016/12/13.
 */

public class CustomView extends View {
    public CustomView(Context context) {
        super(context);
        init(context);
    }

    public CustomView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        int version = Build.VERSION.SDK_INT;
        initFromJNI(version);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        invalidate();
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        Paint paint = new Paint();
        paint.setColor(Color.RED);
        Rect rc = new Rect(100, 100, 200, 200);
        canvas.drawRect(rc, paint);

        String str = stringFromJNI();
        canvas.drawText(str, 300, 300, paint);


        Bitmap bitmap = Bitmap.createBitmap(300, 300, Bitmap.Config.ARGB_8888);
        Canvas canvas2 = new Canvas(bitmap);
        canvas2.saveLayer(50, 60, 150, 160, paint, Canvas.ALL_SAVE_FLAG);
        drawCanvasJNI(canvas2);

        //drawCanvasJNI(canvas);
        int ddd = 0;
    }

    public void callNative() {
        //File rootDir = this.getContext().getExternalCacheDir();
        File rootDir = Environment.getExternalStorageDirectory();

        if (rootDir.isDirectory()) {
            File newFile = new File(rootDir, "javafile.txt");
            try {
                newFile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (newFile.exists()) {
                Log.e("gqg", "ddddd");
            }

        }
        String path = this.getContext().getExternalCacheDir().getAbsolutePath();
        //String path = this.getContext().getCacheDir().getAbsolutePath();
        path += "/myfile.txt";
        nativeCallNative(path);
    }










    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
    public native void initFromJNI(int android_version);
    public native String stringFromJNI();
    public native String drawCanvasJNI(Canvas canvas);
    public native void nativeCallNative(String path);
}
