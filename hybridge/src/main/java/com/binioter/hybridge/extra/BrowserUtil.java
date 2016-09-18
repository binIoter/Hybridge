package com.binioter.hybridge.extra;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * 创建时间: 2016/08/27  <br>
 * 作者: binIoter <br>
 * 描述: 外部调起webActivity工具类
 */
public class BrowserUtil {

  /**
   * 启动WebView
   */
  public static void startWebActivity(Context context, String url, Class cls) {
    Intent intent = new Intent(context, cls);
    intent.putExtra(WebViewActivityConfig.TAG_URL, url);
    context.startActivity(intent);
  }

  /**
   * 打开外部浏览器
   */
  public static void startExternWebActivity(Context context, String url) {
    try {
      Intent intent = new Intent(Intent.ACTION_VIEW);
      intent.setData(Uri.parse(url));
      if ((context instanceof Activity) == false) {
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      }
      context.startActivity(intent);
    } catch (Exception e) {
    }
  }


}