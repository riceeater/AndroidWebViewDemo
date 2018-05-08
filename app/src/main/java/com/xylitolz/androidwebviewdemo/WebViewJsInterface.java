package com.xylitolz.androidwebviewdemo;

import android.content.Context;
import android.util.Log;
import android.webkit.JavascriptInterface;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * @author 小米Xylitol
 * @email xiaomi987@hotmail.com
 * @desc JS调用Android方法
 * @date 2018-05-08 10:49
 */
public class WebViewJsInterface {

    private static final String TAG = "WebViewJsInterface";

    private WeakReference<Context> weakReference;

    public WebViewJsInterface(Context context) {
        weakReference = new WeakReference<>(context);
    }

    @JavascriptInterface
    public String getPhoneBand() {
        String result = android.os.Build.BRAND;
        Toast.makeText(weakReference.get(),"JS called Android By addJavascriptInterface~",Toast.LENGTH_SHORT).show();
        return result;
    }
}
