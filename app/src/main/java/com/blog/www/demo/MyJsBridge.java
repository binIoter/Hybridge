package com.blog.www.demo;

import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.webkit.JsPromptResult;
import android.widget.Toast;
import com.binioter.hybridge.core.CommonJsBridge;
import com.binioter.hybridge.extra.BrowserUtil;
import com.binioter.hybridge.extra.TopTitleBar;

/**
 * 创建时间: 2016/08/27 <br>
 * 作者: binIoter <br>
 * 描述: js调native的回调在这里处理
 */
public class MyJsBridge extends CommonJsBridge {
  private final String TAG = getClass().getSimpleName();
  private Context mContext;

  public MyJsBridge(Context context) {
    super(context);
    this.mContext = context;
  }

  @Override
  public boolean dojsinterface(String methodName, String param, final JsPromptResult result) {
    //将返回值作为参数传给js
    result.confirm("result_ok");
    //以下是我们的业务代码
    if (TextUtils.isEmpty(methodName) || mContext == null) {
      return true;
    }
    //titleBar相关单独维护
    if (methodName.startsWith("bar_")) {
      initTitleBar(methodName, param,
          (TopTitleBar) ((Activity) mContext).findViewById(R.id.title_bar));
    } else {
      //具体业务相关这里处理
      switch (methodName) {
        case "toast":
          Toast.makeText(mContext, "我是native的toast", Toast.LENGTH_SHORT).show();
          break;
        case "delayToast":
          new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override public void run() {
              Toast.makeText(mContext, "我是延时的native的toast", Toast.LENGTH_SHORT).show();
            }
          }, 3000);
          break;
        case "openNewPage":
          BrowserUtil.startWebActivity(mContext, "file:///android_asset/demo.html", MyWebViewActivity.class);
          break;
        case "finish":
          if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
          }
          break;
        default:
          break;
      }
    }
    return true;
  }

  /**
   * titleBar相关处理
   */
  private void initTitleBar(String methodName, String param, TopTitleBar titleBar) {
    if (titleBar != null) {
      switch (methodName) {
        case "bar_RightTitle":
          titleBar.addAction(new TopTitleBar.TextAction(param));
          break;
        case "bar_Title":
          titleBar.setTitle(param);
          break;
        default:
          break;
      }
    }
  }
}
