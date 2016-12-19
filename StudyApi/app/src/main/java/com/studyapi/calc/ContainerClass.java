package com.studyapi.calc;

import android.util.Log;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Vector;

/**
 * Created by gaoqingguang on 2016/2/24.
 */
public class ContainerClass {
    private static ContainerClass mInstance;

    public static ContainerClass instance() {
        if (null == mInstance) {
            mInstance = new ContainerClass();
        }
        return mInstance;
    }

    class Student {
        public Student(String name, int age) {
            mName = name;
            mAge = age;
        }
        public String mName;
        public int mAge;
    }

    private void TestVector() {
        Vector<Student> ls = new Vector<>(3);
        ls.add(new Student("gqg", 13));
        ls.add(new Student("gqg1", 11));
        ls.add(new Student("gqg2", 12));
        ls.add(new Student("gqg3", 13));
        ls.remove(2);

        ls.isEmpty();
    }

    private void TestArrayList() {
        List<Student> ls = new ArrayList<>(3);
        ls.add(new Student("gqg", 13));
        ls.add(new Student("gqg1", 11));
        ls.add(new Student("gqg2", 12));
        ls.add(new Student("gqg3", 13));
        ls.remove(2);
        ls.isEmpty();
    }

    public static <E> HashSet<E> newHashSet(E... elements) {
        HashSet<E> set = new HashSet<E>(elements.length);
        Collections.addAll(set, elements);
        return set;
    }
    private static final HashSet<String> ACCEPTED_SCHEMES = newHashSet(
            "about", "data", "file", "http", "https", "inline", "javascript");
    private static boolean isAcceptedScheme(URI uri) {
        return ACCEPTED_SCHEMES.contains(uri.getScheme());
    }

    public static URI createURIFromUrl(String url) throws URISyntaxException {
        URI oUri = null;
        try {
            oUri = new URI(url);
        } catch (URISyntaxException e) {
            try {
                URL oUrl = new URL(url);
                oUri = new URI(oUrl.getProtocol(), oUrl.getUserInfo(), oUrl.getHost(), oUrl.getPort(), oUrl.getPath(), oUrl.getQuery(), null);
            } catch (MalformedURLException me) {
                throw  e;
            } catch (URISyntaxException uri_se) {
                throw  e;
            }
        }

        return oUri;
    }

    private void TestURI() {
        String uri = "http://openmobile.qq.com/api/check2?page=qzshare.html&loginpage=loginindex.html&logintype=qzone&title=%E5%A4%A7%E5%A4%B4%E8%91%B1%E8%91%B1%E6%AD%A3%E5%9C%A8%E7%9B%B4%E6%92%AD%E3%80%8A#%E6%96%B0%E4%BA%BA%E6%B1%82%E5%85%B3%E6%B3%A8#%20#%E6%88%91%E6%98%AF%E5%B0%8F%E9%B2%9C%E8%82%89#%20#%E9%80%97%E6%AF%94#%E3%80%8B%EF%BC%8Cta%E7%9A%84%E8%8A%B1%E6%A4%92ID:35851220%EF%BC%8C%E5%BF%AB%E6%9D%A5%E5%85%B3%E6%B3%A8%E5%90%A7-%E8%8A%B1%E6%A4%92%E7%9B%B4%E6%92%AD,%E7%BE%8E%E9%A2%9C%E7%9B%B4%E6%92%AD,%E7%96%AF%E7%8B%82%E5%8D%96%E8%90%8C&summary=%E4%BD%A0%E8%A1%8C%E4%BD%A0%E5%BE%97%E7%91%9F%EF%BC%8C%E4%B8%8D%E8%A1%8C%E7%9C%8B%E7%9B%B4%E6%92%AD%EF%BC%81%F0%9F%8C%B1%E5%A4%A7%E5%A4%B4%E8%91%B1%E8%91%B1%F0%9F%8C%B1%E6%AD%A3%E5%9C%A8%E7%9B%B4%E6%92%AD%EF%BC%8C%E6%9C%89%E7%82%B9%E6%84%8F%E6%80%9D%EF%BC%81&url=http%3A%2F%2Fwww.huajiao.com%2Fl%2F17300725&desc=&imageUrl=http%3A%2F%2Fimage.huajiao.com%2Ff50d182a2327620cfd16c2124886c06d.jpg&site=&sid=&referer=http%3A%2F%2Fh.huajiao.com%2Fl%2Fon%3Fliveid%3D17300725";
        //String uri = "http://openmobile.qq.com/api/check2?page=qzshare.html&loginpage=loginindex.html&logintype=qzone&title=%E5%A4%A7%E5%A4%B4%E8%91%B1%E8%91%B1%E6%AD%A3%E5%9C%A8%E7%9B%B4%E6%92%AD%E3%80%8A#%E6%96%B0%E4%BA%BA%E6%B1%82%E5%85%B3%E6%B3%A8#%20#%E6%88%91%E6%98%AF%E5%B0%8F%E9%B2%9C%E8%82%89#%20#%E9%80%97%E6%AF%94#%E3%80%8B%EF%BC%8Cta%E7%9A%84%E8%8A%B1%E6%A4%92ID:35851220%EF%BC%8C%E5%BF%AB%E6%9D%A5%E5%85%B3%E6%B3%A8%E5%90%A7-%E8%8A%B1%E6%A4%92%E7%9B%B4%E6%92%AD,%E7%BE%8E%E9%A2%9C%E7%9B%B4%E6%92%AD,%E7%96%AF%E7%8B%82%E5%8D%96%E8%90%8C&summary=%E4%BD%A0%E8%A1%8C%E4%BD%A0%E5%BE%97%E7%91%9F%EF%BC%8C%E4%B8%8D%E8%A1%8C%E7%9C%8B%E7%9B%B4%E6%92%AD%EF%BC%81%F0%9F%8C%B1%E5%A4%A7%E5%A4%B4%E8%91%B1%E8%91%B1%F0%9F%8C%B1%E6%AD%A3%E5%9C%A8%E7%9B%B4%E6%92%AD%EF%BC%8C%E6%9C%89%E7%82%B9%E6%84%8F%E6%80%9D%EF%BC%81&url=http%3A%2F%2Fwww";
        //String uri = "http://openmobile.qq.com/api/check2?page=qzshare.html&loginpage=loginindex.html&logintype=qzone&title=%E5%A4%A7%E5%A4%B4%E8%91%B1%E8%91%B1%E6%AD%A3%E5%9C%A8%E7%9B%B4%E6%92%AD%E3%80%8A#%E6%96%B0%E4%BA%BA%E6%B1%82%E5%85%B3%E6%B3%A8#%20#%E6%88%91%E6%98%AF%E5%B0%8F%E9%B2%9C%E8%82%89#%20#%E9%80%97%E6%AF%94#%E3%80%8B%EF%BC%8Cta%E7%9A%84%E8%8A%B1%E6%A4%92ID:35851220%EF%BC%8C%E5%BF%AB%E6%9D%A5%E5%85%B3%E6%B3%A8%E5%90%A7-%E8%8A%B1%E6%A4%92%E7%9B%B4%E6%92%AD,%E7%BE%8E%E9%A2%9C%E7%9B%B4%E6%92%AD,%E7%96%AF%E7%8B%82%E5%8D%96%E8%90%8C&summary=%E4%BD%A0%E8%A1%8C%E4%BD%A0%";
        //String uri = "http://openmobile.qq.com/api/check2?page=qzshare.html&loginpage=loginindex.html&logintype=qzone&title=%E5%A4%A7%E5%A4%B4%E8%91%B1%E8%91%B1%E6%AD%A3%E5%9C%A8%E7%9B%B4%E6%92%AD%E3%80%8A#%E6%96%B0%E4%BA%BA%E6%B1%82%E5%85%B3%E6%B3%A8#%20#%E6%88%91%E6%98%AF%E5%B0%8F%E9%B2%9C%E8%82%89#%20#%E9%80%97%E6%AF%94#%E3%80%8B%EF%BC%8Cta%E7%9A%84%E8%8A%B1%E6%A4%92I";
        //String uri = "http://openmobile.qq.com/api/check2?page=qzshare.html&loginpage=loginindex.html&logintype=qzone&title=%E5%A4%A7%E5%A4%B4%E8%91%B1%E8%91%B1%E6%AD%A3%E5%9C%A8%E7%9B%";
        //String uri = "http://openmobile.qq.com/api/check2?page=qzshare.html&loginpage=loginin";
        //String uri = "http://openmobile.qq.com/api/check2?page=qzshare.html&loginpage=loginindex.html&logintype=qzone&title=%E5%A4%A7%E5%A4%B4%E8%91%B1%E8%91%B1%E6%AD%A3%E5%9C%A8%E7%9B%5";
        boolean result = false;
        URI oUri = null;
        try {
            oUri = createURIFromUrl(uri);
            result = isAcceptedScheme(oUri);
        } catch (URISyntaxException e) {
            result = false;
            Log.e("gqg:uri", e.toString());
        }

        if (null != oUri) {
            Log.e("gqg:uri", String.valueOf(result) + oUri.toString());
        }
    }

    public void StartTest() {
        TestURI();
        TestVector();
        TestArrayList();
    }

}
