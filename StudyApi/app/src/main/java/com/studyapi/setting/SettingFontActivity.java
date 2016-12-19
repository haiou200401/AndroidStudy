package com.studyapi.setting;

import android.app.Activity;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.studyapi.BaseActivity;
import com.studyapi.R;

import org.xmlpull.v1.XmlPullParserException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.CharBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Vector;

public class SettingFontActivity extends BaseActivity implements View.OnClickListener{
    final String TAG = "gqg:setting";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting_font);

        findViewById(R.id.id_button_setting_font).setOnClickListener(this);
        //Android.appwidget.action.APPWIDGET_UPDATE

        //final TypedArray ta = this.obtainStyledAttributes(resId, android.R.styleable.TextAppearance);
        Log.e(TAG, "onCreate");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        Log.e(TAG, newConfig.toString());
    }

    public boolean isExist(String path) {
        File file = new File(path);
        if (file.exists()) {
            boolean isFile = file.isFile();
            boolean isDir = file.isDirectory();
            Log.e(TAG, String.valueOf(isFile) + String.valueOf(isDir));
            return true;
        }
        return false;
    }

    public String getQiKuFontFile() {
        File dir = new File("/data/fonts/");
        if (dir.exists()) {
            File[] subFiles = dir.listFiles();
            for (int index = 0; index < subFiles.length; index++) {
                if (subFiles[index].isFile()) {
                    String filename = subFiles[index].getName();
                    if (filename.trim().toLowerCase().endsWith(".ttf")) {
                        return filename;
                    }
                }
            }
        }

        return "";
    }


    @Override
    public void onClick(View v) {
        Button btn = (Button)v;

        String qikuFontFile = getQiKuFontFile();
        Log.e(TAG, qikuFontFile);

        Log.e(TAG, "Build.BRAND=" + Build.BRAND);
        Log.e(TAG, Build.BRAND);
        Log.e(TAG, Build.MODEL);
        Log.e(TAG, Build.DEVICE);


        isExist("/data/system/theme");
        isExist("/data/system/drm.log");
        boolean isexist = isExist("/data/system/theme/fonts");
        Log.e(TAG, String.valueOf(isexist));

        //String fontFeature = btn.getFontFeatureSettings();
        //Log.e(TAG, fontFeature);
        Typeface tf = btn.getTypeface();
        if (null != tf) {
            Log.e(TAG, tf.toString());
        }
        Typeface dtf = Typeface.DEFAULT;
        boolean equals2 = dtf.equals(btn);
        Log.e(TAG, String.valueOf(equals2));

        try {
            Field[] field = dtf.getClass().getDeclaredFields();
            for(int j=0 ; j<field.length ; j++){     //遍历所有属性
                String name = field[j].getName();    //获取属性的名字

                System.out.println("attribute name:"+name);
                String type = field[j].getGenericType().toString();    //获取属性的类型
                if(type.equals("class java.lang.String")){   //如果type是类类型，则前面包含"class "，后面跟类名
                    if (!field[j].isAccessible()) {
                        field[j].setAccessible(true);
                    }
                    Object obj = field[j].get(Typeface.DEFAULT);
                    String path = (String)obj;
                    Log.e(TAG, path + "name = " + name);
/*
                    Method m = dtf.getClass().getMethod("get"+name);
                    String value = (String) m.invoke(dtf);    //调用getter方法获取属性值
                    if(value != null){
                        System.out.println("attribute value:"+value);
                    }
*/
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // 三星s6
            Field field = Typeface.class.getDeclaredField("FlipFontPath");
            if (null != field) {
                String type = field.getGenericType().toString();
                if(type.equals("class java.lang.String")) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    String path = (String) field.get(Typeface.DEFAULT);
                    // 默认字体 path == "default"
                    Log.e(TAG, path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // 荣耀6
            Field field = Typeface.class.getDeclaredField("THEME_FONT_PATH");
            if (null != field) {
                String type = field.getGenericType().toString();
                if(type.equals("class java.lang.String")) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    String path = (String) field.get(Typeface.DEFAULT);
                    // 默认字体 path = "/data/skin/fonts/"
                    Log.e(TAG, path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            // 奇酷
            Field field = Typeface.class.getDeclaredField("pathDefault_current");
            if (null != field) {
                String type = field.getGenericType().toString();
                if(type.equals("class java.lang.String")) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    String path = (String) field.get(Typeface.DEFAULT);
                    // 默认字体 path = "/data/skin/fonts/"
                    Log.e(TAG, "pathDefault_current");
                    Log.e(TAG, path);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        //getSystemFonts();


        Log.e(TAG, dtf.toString());
    }
    private static File getSystemFontConfigLocation() {
        return new File("/system/etc/");
    }
    private static FontFamily makeFamilyFromParsed(FontListParser.Family family) {
        FontFamily fontFamily = new FontFamily(family.lang, family.variant);
        for (FontListParser.Font font : family.fonts) {
            fontFamily.addFontWeightStyle(font.fontName, font.weight, font.isItalic);
        }
        return fontFamily;
    }
    private void getSystemFonts() {
        File systemFontConfigLocation = getSystemFontConfigLocation();
        //File configFilename = new File(systemFontConfigLocation, "fonts.xml");
        File configFilename = new File(systemFontConfigLocation, "fallback_fonts.xml");
        //File configFilename = new File(systemFontConfigLocation, "system_fonts.xml");
        try {
            FileInputStream fontsIn = new FileInputStream(configFilename);
            InputStreamReader read = new InputStreamReader(fontsIn, "utf-8");//考虑到编码格式
            BufferedReader bufferedReader = new BufferedReader(read);
            String strAll = new String();
            String lineTxt = null;
            while((lineTxt = bufferedReader.readLine()) != null){
                System.out.println(lineTxt);
                strAll += lineTxt;
            }
            read.close();
            Log.e(TAG, strAll);


/*
            FontListParser.Config fontConfig = FontListParser.parse(fontsIn);

            List<FontFamily> familyList = new ArrayList<FontFamily>();
            // Note that the default typeface is always present in the fallback list;
            // this is an enhancement from pre-Minikin behavior.
            for (int i = 0; i < fontConfig.families.size(); i++) {
                FontListParser.Family f = fontConfig.families.get(i);
                if (i == 0 || f.name == null) {
                    familyList.add(makeFamilyFromParsed(f));
                }
            }
*/
/*
            sFallbackFonts = familyList.toArray(new FontFamily[familyList.size()]);
            setDefault(Typeface.createFromFamilies(sFallbackFonts));

            Map<String, Typeface> systemFonts = new HashMap<String, Typeface>();
            for (int i = 0; i < fontConfig.families.size(); i++) {
                Typeface typeface;
                FontListParser.Family f = fontConfig.families.get(i);
                if (f.name != null) {
                    if (i == 0) {
                        // The first entry is the default typeface; no sense in
                        // duplicating the corresponding FontFamily.
                        typeface = sDefaultTypeface;
                    } else {
                        FontFamily fontFamily = makeFamilyFromParsed(f);
                        FontFamily[] families = { fontFamily };
                        typeface = Typeface.createFromFamiliesWithDefault(families);
                    }
                    systemFonts.put(f.name, typeface);
                }
            }
            for (FontListParser.Alias alias : fontConfig.aliases) {
                Typeface base = systemFonts.get(alias.toName);
                Typeface newFace = base;
                int weight = alias.weight;
                if (weight != 400) {
                    newFace = new Typeface(nativeCreateWeightAlias(base.native_instance, weight));
                }
                systemFonts.put(alias.name, newFace);
            }
            sSystemFontMap = systemFonts;
*/

        } catch (RuntimeException e) {
            Log.w(TAG, "Didn't create default family (most likely, non-Minikin build)", e);
            // TODO: normal in non-Minikin case, remove or make error when Minikin-only
        } catch (FileNotFoundException e) {
            Log.e(TAG, "Error opening " + configFilename);
        } catch (IOException e) {
            Log.e(TAG, "Error reading " + configFilename);
        }
    }
}
