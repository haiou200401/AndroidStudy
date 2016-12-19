package com.studyapi.calc;

import java.util.Vector;

/**
 * Created by gaoqingguang on 2016/2/24.
 */
public class LruCacheTest {
    private static LruCacheTest mInstance;

    public static LruCacheTest instance() {
        if (null == mInstance) {
            mInstance = new LruCacheTest();
        }
        return mInstance;
    }

    private LruCacheTest() {

    }



    public void TestStart() {

    }
}
