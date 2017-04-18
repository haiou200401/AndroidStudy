package com.hzy.un7zip;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.yiming.lib7z.Un7Zip;

import java.io.File;
import java.io.FilenameFilter;

public class MainActivity extends AppCompatActivity {

    private String filePath;
    private String outPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //outPath = Environment.getExternalStorageDirectory().getPath() + File.separator + "extracted";
        outPath = getExternalCacheDir().getAbsolutePath();
        findViewById(R.id.button_choose_file).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("*/*");
                intent.addCategory(Intent.CATEGORY_OPENABLE);
                startActivityForResult(intent, 1);
            }
        });

        findViewById(R.id.button_extract).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(filePath)) {
                    //Toast.makeText(MainActivity.this, "Please select 7z file first!", Toast.LENGTH_SHORT).show();
                    setFilePath();

                }

                startExtractFile();
            }
        });
    }

    private String get7zfile(File targetDir) {
        String[] files = targetDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File file, String filename) {
                return filename.startsWith("libqwv_");
            }
        });

        if (null != files) {
            return new File(targetDir, files[0]).getAbsolutePath();
        }

        return "";
    }

    private void setFilePath() {
        ApplicationInfo applicationInfo = getApplicationInfo();
        File libDir = new File(applicationInfo.nativeLibraryDir);
        filePath = get7zfile(libDir);

    }

    private void startExtractFile() {
        new Thread() {
            @Override
            public void run() {
                File outDir = new File(outPath);
                if (!outDir.exists()) {
                    outDir.mkdir();
                }
                File inFile = new File(filePath);
                if (!inFile.exists()) {
                    Log.e("gqg", "no 7z file");
                }

                Un7Zip.extract7z(filePath, outPath);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "extracted to: " + outPath, Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            Uri uri = data.getData();
            String[] proj = {MediaStore.Images.Media.DATA};
            Cursor actualimagecursor = managedQuery(uri, proj, null, null, null);
            int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            filePath = actualimagecursor.getString(actual_image_column_index);
            Toast.makeText(MainActivity.this, "choose file:" + filePath, Toast.LENGTH_SHORT).show();
        }
    }

}
