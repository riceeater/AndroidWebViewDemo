package com.xylitolz.androidwebviewdemo;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.JsResult;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.FrameLayout;

import java.lang.ref.WeakReference;

/**
 * @author 小米Xylitol
 * @email xiaomi987@hotmail.com
 * @desc Android原生调用JS方法演示
 * @date 2018-05-08 11:10
 */
public class NativeCallJsActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btnLoadUrl;
    private Button btnEvaluate;
    private Button btnBoth;
    private ViewGroup llWebViewContainer;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_native_call_js);
        initView();
    }

    private void initView() {
        btnLoadUrl = findViewById(R.id.btn_load_url);
        btnEvaluate = findViewById(R.id.btn_evaluate);
        btnBoth = findViewById(R.id.btn_both);
        llWebViewContainer = findViewById(R.id.ll_web_view_container);

        btnLoadUrl.setOnClickListener(this);
        btnEvaluate.setOnClickListener(this);
        btnBoth.setOnClickListener(this);
        //使用弱引用，防止内存泄漏
        WeakReference<Context> weakContext = new WeakReference<>((Context)this);
        webView = new WebView(weakContext.get());

        llWebViewContainer.addView(webView);
        //设置WebView与JS交互的权限
        WebSettings settings = webView.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setJavaScriptCanOpenWindowsAutomatically(true);

        //载入网页
        webView.loadUrl("file:///android_asset/native_call_js.html");

    }

    @Override
    public void onClick(View v) {
        final int version = Build.VERSION.SDK_INT;
        switch (v.getId()) {
            case R.id.btn_load_url:
                webView.loadUrl("javascript:changeImage()");
                break;
            case R.id.btn_evaluate:
                if (version >= Build.VERSION_CODES.KITKAT) {
                    webView.evaluateJavascript("javascript:changeImage()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //获取返回值，如果存在
                        }
                    });
                }
                break;
            case R.id.btn_both:
                if (version < Build.VERSION_CODES.KITKAT) {
                    webView.loadUrl("javascript:changeImage()");
                } else {
                    webView.evaluateJavascript("javascript:changeImage()", new ValueCallback<String>() {
                        @Override
                        public void onReceiveValue(String value) {
                            //获取返回值，如果存在
                        }
                    });
                }
                break;
        }
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
        Intent intent = new Intent(context,NativeCallJsActivity.class);
        context.startActivity(intent);
    }
}
