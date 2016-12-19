package com.studyapi.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

public class ThemeModeTextView extends TextView  {

    private int attr_drawable = -1;
    private int attr_textColor = -1;

    public ThemeModeTextView(Context context) {
        super(context);
    }

    public ThemeModeTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.attr_drawable = getBackgroundAttibute(attrs);
        this.attr_textColor = getTextColorAttribute(attrs);
    }

    public ThemeModeTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.attr_drawable = getBackgroundAttibute(attrs);
        this.attr_textColor = getTextColorAttribute(attrs);
    }


    public int getBackgroundAttibute(AttributeSet attr) {
        return getAttributeValue(attr, android.R.attr.background);
    }
    public int getTextColorAttribute(AttributeSet attr) {
        return getAttributeValue(attr, android.R.attr.textColor);
    }

    public int getAttributeValue(AttributeSet attr, int paramInt) {
        int value = -1;
        int count = attr.getAttributeCount();
        for (int i = 0; i < count; i++) {
            if (attr.getAttributeNameResource(i) == paramInt) {
                String str = attr.getAttributeValue(i);
                if (null != str && str.startsWith("?")) {
                    value = Integer.valueOf(str.substring(1, str.length()));
                    return value;
                }
            }
        }
        return value;
    }

}
