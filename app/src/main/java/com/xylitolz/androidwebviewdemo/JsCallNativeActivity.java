package com.xylitolz.androidwebviewdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

/**
 * @author 小米Xylitol
 * @email xiaomi987@hotmail.com
 * @desc JS调用原生方法
 * @date 2018-05-08 13:20
 */
public class JsCallNativeActivity extends AppCompatActivity {

    private ViewGroup llWebViewContainer;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_js_call_native);
        initView();
    }

    private void initView() {
        llWebViewContainer = findViewById(R.id.ll_web_view_container);

        //使用弱引用，防止内存泄漏
        WeakReference<Context> weakContext = new WeakReference<>((Context)this);
        webView = new WebView(weakContext.get());

        llWebViewContainer.addView(webView);
        //设置WebView与JS交互的权限
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        //设置响应alert、confirm、Prompt方法
        webView.setWebChromeClient(new WebChromeClient() {
            @Override
            public boolean onJsAlert(WebView view, String url, String message, final JsResult result) {
                AlertDialog.Builder b = new AlertDialog.Builder(JsCallNativeActivity.this);
                b.setTitle("Alert");
                b.setMessage("JS通过alert调用原生，参数是"+message);
                b.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        result.confirm();
                    }
                });
                b.setCancelable(false);
                b.create().show();
                return true;
            }
        });

        //对WebView与JsInterface建立对象映射
        webView.addJavascriptInterface(new WebViewJsInterface(this),"android");

        //设置WebViewClient
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //判断Url
                if(TextUtils.isEmpty(url)) {
                    return false;
                }
                Uri uri = Uri.parse(url);
                if(uri.getScheme().equals("xylitolz")) {
                    //url为定义好的开头为xytlitolZ的协议，则通过拦截
                    if(uri.getAuthority().equals("toastHello")) {
                        //区分协议
                        String arg = uri.getQueryParameter("arg");
                        JsUtil.getInstance().toastHello(JsCallNativeActivity.this,arg);
                    }
                    return true;
                } else {
                    return false;
                }
            }
        });

        //载入网页
        webView.loadUrl("file:///android_asset/js_call_native.html");

    }

    @Override
    protected void onDestroy() {
        if( webView!=null) {
            // 如果先调用destroy()方法，则会命中if (isDestroyed()) return;这一行代码，需要先onDetachedFromWindow()，再
            // destory()
            ViewParent parent = webView.getParent();
            if (parent != null) {
                ((ViewGroup) parent).removeView(webView);
            }
            webView.stopLoading();
            // 退出时调用此方法，移除绑定的服务，否则某些特定系统会报错
            webView.getSettings().setJavaScriptEnabled(false);
            webView.clearHistory();
            webView.clearView();
            webView.removeAllViews();
            webView.destroy();
        }
        super.onDestroy();
    }

    public static void start(Context context) {
        Intent intent = new Intent(context,JsCallNativeActivity.class);
        context.startActivity(intent);
    }
}
