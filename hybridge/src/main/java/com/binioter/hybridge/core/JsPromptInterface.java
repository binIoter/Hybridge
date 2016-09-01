package com.binioter.hybridge.core;

import android.webkit.JsPromptResult;

/**
 * 创建时间: 2016/07/30  <br>
 * 作者: binIoter <br>
 * 描述:基础JSPromptInterface
 */
public interface JsPromptInterface {
  boolean dojsinterface(String methodName, String param, JsPromptResult result);
}
