package com.binioter.hybridge.core;

import android.content.Context;
import android.webkit.JsPromptResult;

/**
 * 创建时间: 2016/7/30  <br>
 * 作者: binIoter <br>
 * 描述:通用的JavascriptInterface
 */
public class CommonJsBridge implements JsPromptInterface {
  private final String TAG = getClass().getSimpleName();
  private Context mContext;

  public CommonJsBridge(Context context) {
    mContext = context;
  }

  @Override public boolean dojsinterface(String methodName, String param, JsPromptResult result) {
    //只处理通用方法，其他的交由子类实现
    return false;
  }
}
