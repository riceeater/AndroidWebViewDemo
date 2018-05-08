package com.xylitolz.androidwebviewdemo;

import android.content.Context;
import android.widget.Toast;

import java.lang.ref.WeakReference;

/**
 * @author 小米Xylitol
 * @email xiaomi987@hotmail.com
 * @desc js使用webViewClient调用原生方法帮助类
 * @date 2018-05-08 14:09
 */
public class JsUtil {

    public static JsUtil getInstance() {
        return JsUtilHolder.util;
    }

    public void toastHello(Context context,String arg) {
        WeakReference<Context> weakReference = new WeakReference<>(context);
        Toast.makeText(weakReference.get(),"Hello,JS called Android!Arg is"+arg,Toast.LENGTH_SHORT).show();
    }

    private JsUtil() {}

    private static class JsUtilHolder {
        private static JsUtil util = new JsUtil();
    }
}
