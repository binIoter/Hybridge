package com.binioter.hybridge.core;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.http.SslError;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import com.binioter.hybridge.extra.Utils;

/**
 * 创建时间: 2016/08/29  <br>
 * 作者: binIoter <br>
 * 描述: webview基类，对系统webview进行封装
 */
public class BaseWebView extends WebView {

  private WebViewClient mWebViewClient;
  private OnLoadUrlListener mOnLoadUrlListener = null;
  private Context mContext = null;
  private OnPageStartedListener mOnPageStartedListener = null;
  private OnPageFinishedListener mOnPageFinishedListener = null;
  private OnReceivedErrorListener mOnReceivedErrorListener = null;
  private OnJsPromptCallback jsCallback;

  public BaseWebView(Context context) {
    super(context);
    mContext = context;
    init();
  }

  public BaseWebView(Context context, AttributeSet attrs) {
    super(context, attrs);
    mContext = context;
    init();
  }

  public void setDownloadEnabled(boolean isEnabled) {
    if (isEnabled) {
      setOnLoadUrlListener(null);
    }
  }

  public void setOnJsPromptCallback(OnJsPromptCallback jsCallback) {
    this.jsCallback = jsCallback;
  }

  @SuppressLint("SetJavaScriptEnabled") public void init() {
    getSettings().setJavaScriptEnabled(true);
    getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
    StringBuilder uaBuilder = new StringBuilder();
    uaBuilder.append(getSettings().getUserAgentString());
    //推荐用公司名
    uaBuilder.append("github/");
    uaBuilder.append(Utils.getVersionName(mContext));
    getSettings().setUserAgentString(uaBuilder.toString());
    mWebViewClient = new MyWebViewClient();
    setWebViewClient(mWebViewClient);
    setWebChromeClient(new SafeDialogWebChromeClient());
    //移除默认内置接口,防止远程代码执行漏洞攻击
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
      removeJavascriptInterface("searchBoxJavaBridge_");
      removeJavascriptInterface("accessibility");
      removeJavascriptInterface("accessibilityTraversal");
    }
    setOnLongClickListener(new WebView.OnLongClickListener() {
      @Override public boolean onLongClick(View arg0) {
        return true;
      }
    });
  }

  public void setOnLoadUrlListener(OnLoadUrlListener src) {
    this.mOnLoadUrlListener = src;
  }

  public void setOnPageStartedListener(OnPageStartedListener listener) {
    mOnPageStartedListener = listener;
  }

  public void setOnPageFinishedListener(OnPageFinishedListener listener) {
    mOnPageFinishedListener = listener;
  }

  public void setOnReceivedErrorListener(OnReceivedErrorListener listener) {
    mOnReceivedErrorListener = listener;
  }

  public interface OnLoadUrlListener {
    public boolean shouldOverrideUrlLoading(WebView view, String url);
  }

  public interface OnPageStartedListener {
    void onPageStarted(WebView webView, String url);
  }

  public interface OnPageFinishedListener {
    void onPageFinished(WebView webView, String url);
  }

  public interface OnReceivedErrorListener {
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error);
  }

  private class SafeDialogWebChromeClient extends WebChromeClient {
    @Override public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
      result.confirm();
      return !Utils.canShow(getContext()) || super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
      result.confirm();
      return !Utils.canShow(getContext()) || super.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
      result.confirm();
      return !Utils.canShow(getContext()) || super.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
        JsPromptResult result) {
      if (jsCallback != null) {
        boolean dealMessage = jsCallback.onJsPrompt(message, result);
        if (dealMessage) {
          return true;
        }
      }
      return !Utils.canShow(getContext()) || super.onJsPrompt(view, url, message, defaultValue,
          result);
    }
  }

  public class MyWebViewClient extends WebViewClient {

    @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);

      if (mOnPageStartedListener != null) {
        mOnPageStartedListener.onPageStarted(view, url);
      }
    }

    @Override public void onLoadResource(WebView view, String url) {
      super.onLoadResource(view, url);
    }

    @Override public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);
      if (mOnPageFinishedListener != null) {
        mOnPageFinishedListener.onPageFinished(view, url);
      }
    }

    @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (mOnLoadUrlListener == null) {
        return super.shouldOverrideUrlLoading(view, url);
      } else {
        return mOnLoadUrlListener.shouldOverrideUrlLoading(view, url);
      }
    }

    @Override
    public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
      super.onReceivedError(view, request, error);
      if (mOnReceivedErrorListener != null) {
        mOnReceivedErrorListener.onReceivedError(view, request, error);
      }
    }

    @SuppressLint("Override")
    public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
      if (null != handler) {
        handler.proceed();
      }
    }
  }
}
