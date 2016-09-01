package com.binioter.hybridge.core;

import android.webkit.JsPromptResult;

/**
 * 创建时间: 2016/08/03  <br>
 * 作者: binIoter <br>
 * 描述:JSPromt回调
 */
public interface OnJsPromptCallback {
  boolean onJsPrompt(String message, JsPromptResult result);
}
