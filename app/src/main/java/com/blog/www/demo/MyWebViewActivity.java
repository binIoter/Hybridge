package com.blog.www.demo;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.DownloadListener;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.URLUtil;
import android.webkit.WebChromeClient;
import android.webkit.WebStorage;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import com.binioter.hybridge.core.BaseWebView;
import com.binioter.hybridge.core.JsPromptBridge;
import com.binioter.hybridge.core.JsPromptInterface;
import com.binioter.hybridge.core.OnJsPromptCallback;
import com.binioter.hybridge.extra.BaseWebViewActivity;
import com.binioter.hybridge.extra.ProtocolBean;
import com.binioter.hybridge.extra.Utils;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * 创建时间: 2016/08/02  <br>
 * 作者: binIoter <br>
 * 描述: 通用WebActivity，支持设置cookie、自定义javascript interface
 */
public class MyWebViewActivity extends BaseWebViewActivity {
  private static final String KEY_PROTOCOL = "bd_protocol";
  private static final String KEY_METHOD_NAME = "methodName";
  private static final String KEY_PARAM = "param";
  private static final String SHOST = "m.github.io";
  protected BaseWebView mWebView = null;
  private static final ProtocolBean protocolBean =
      new ProtocolBean(KEY_PROTOCOL, KEY_METHOD_NAME, KEY_PARAM);
  private JsPromptBridge jsBridge;

  private OnJsPromptCallback jsCallback = new OnJsPromptCallback() {

    @Override public boolean onJsPrompt(String message, JsPromptResult result) {
      if (jsBridge != null) {
        return jsBridge.parseJSON(message, result);
      }
      return false;
    }
  };

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    jsBridge = new JsPromptBridge(protocolBean);
    jsBridge.addJSPromptInterface(new MyJsBridge(this));
    //可以根据不同业务或者模块addJSPromptInterface，这样方便团队分开开发
    //jsBridge.addJSPromptInterface(new MyJsBridge2(this));
    if (mNeedCookie) {
      initCookie();
    }
  }

  @Override protected void onResume() {
    super.onResume();
    if (getWebView() == null) {
      return;
    }
    callHiddenWebViewMethod("onResume");
  }

  @Override protected void onPause() {
    super.onPause();
    callHiddenWebViewMethod("onPause");
  }

  public void addJsPromptInterface(JsPromptInterface jsPromptInterface) {
    if (jsPromptInterface != null) {
      jsBridge.addJSPromptInterface(jsPromptInterface);
    }
  }

  public void removePromptInterface(JsPromptInterface jsPromptInterface) {
    if (jsPromptInterface != null) {
      jsBridge.removeJSPromptInterface(jsPromptInterface);
    }
  }

  @Override public View createWebView() {
    if (mWebView == null) {
      mWebView = new BaseWebView(this);

      mWebView.getSettings().setJavaScriptEnabled(true);
      mWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
      mWebView.getSettings().setAllowFileAccess(true);
      mWebView.getSettings().setDatabaseEnabled(true);
      mWebView.getSettings().setDomStorageEnabled(true);
      mWebView.setDownloadEnabled(true);
      mWebView.getSettings().setSupportZoom(true);
      mWebView.getSettings().setBuiltInZoomControls(true);
      mWebView.getSettings().setUseWideViewPort(true);
      // 设置H5 localstorage相关
      mWebView.getSettings()
          .setDatabasePath(
              getApplicationContext().getDir("databases", Context.MODE_PRIVATE).getAbsolutePath());
      mWebView.setHorizontalScrollBarEnabled(false);
      mWebView.setHorizontalScrollbarOverlay(false);
      mWebView.setInitialScale(100);
      mWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
      ViewGroup.LayoutParams webViewLayoutParams =
          new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
              ViewGroup.LayoutParams.FILL_PARENT);
      mWebView.setLayoutParams(webViewLayoutParams);
      mWebView.setWebViewClient(new MyWebViewClient());
      mWebView.setDownloadListener(new WebViewDownLoadListener());
      MyWebChromeClient client = new MyWebChromeClient(this);
      client.setOnJsPromptCallback(jsCallback);
      mWebView.setWebChromeClient(client);
      // 增加javascript接口的支持
      if (mEnableJs) {
        addJavascriptInterface(protocolBean);
      }
    }
    return mWebView;
  }

  @SuppressLint("JavascriptInterface") @Override
  public void addJavascriptInterface(Object obj, String interfaceName) {
    if (mWebView != null) {
      mWebView.addJavascriptInterface(obj, interfaceName);
    }
  }

  @Override public View getWebView() {
    return mWebView;
  }

  @Override public void loadUrl(String url) {
    if (mWebView != null) {
      mWebView.loadUrl(url);
    }
  }

  /**
   * 刷新WebView，重新加载数据
   */
  protected void refresh() {
    hideCrashTip();
    if (URLUtil.isNetworkUrl(mUrl)) {
      showProgressBar();
      try {
        //url白名单校验
        if (new URL(mUrl).getHost().equals(SHOST)) {
          loadUrl(mUrl);
        }
      } catch (MalformedURLException e) {
        e.printStackTrace();
      }
      loadUrl(mUrl);//如果运行demo请将除了本行代码注释掉
    }
  }

  @Override public void webViewDestory() {
    if (jsBridge != null) {
      jsBridge.removeAllJSPromptInterface();
    }
    if (mWebView != null) {
      mWebView.getSettings().setBuiltInZoomControls(true);
      mWebView.setVisibility(View.GONE);
    }
  }

  @Override public boolean webViewGoBack() {
    if (mWebView == null) {
      return false;
    }
    if (mWebView.canGoBack()) {
      mWebView.goBack();
      return true;
    }
    return false;
  }

  @Override public void initCookie() {
    // TODO: 16/8/30 初始化cookie
  }

  @Override public void onReceivedError(int errorCode) {
    if (mWebView != null) {
      mUrl = mWebView.getUrl();
      mWebView.stopLoading();
    }
    hideProgressBar();
    showNoDataView();
  }

  @Override public void initWebView() {
    Log.d("initwebview", "initwebview");
  }

  @Override public void unregisterReceiver(BroadcastReceiver receiver) {
    try {
      super.unregisterReceiver(receiver);
    } catch (Throwable e) {
    }
  }

  /**
   * 是否需要更新cookie
   */
  protected boolean isNeedUpdateCookie() {
    // TODO: 16/9/1 根据自身业务逻辑处理cookie更新情况
    return mIsUpdateCookie;
  }

  private static class MyWebChromeClient extends WebChromeClient {

    private BaseWebViewActivity mActivity;
    private OnJsPromptCallback callback;

    public MyWebChromeClient(BaseWebViewActivity activity) {
      mActivity = activity;
    }

    public void setOnJsPromptCallback(OnJsPromptCallback callback) {
      this.callback = callback;
    }

    @Override public void onExceededDatabaseQuota(String url, String databaseIdentifier, long quota,
        long estimatedDatabaseSize, long totalQuota, WebStorage.QuotaUpdater quotaUpdater) {
      super.onExceededDatabaseQuota(url, databaseIdentifier, quota, estimatedDatabaseSize,
          totalQuota, quotaUpdater);
      quotaUpdater.updateQuota(estimatedDatabaseSize * 2);
    }

    public View getVideoLoadingProgressView() {
      FrameLayout frameLayout = new FrameLayout(mActivity);
      frameLayout.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT,
          ViewGroup.LayoutParams.FILL_PARENT));
      return frameLayout;
    }

    @Override public boolean onJsAlert(WebView view, String url, String message, JsResult result) {
      result.confirm();
      return !Utils.canShow(mActivity) || super.onJsAlert(view, url, message, result);
    }

    @Override
    public boolean onJsBeforeUnload(WebView view, String url, String message, JsResult result) {
      result.confirm();
      return !Utils.canShow(mActivity) || super.onJsBeforeUnload(view, url, message, result);
    }

    @Override
    public boolean onJsConfirm(WebView view, String url, String message, JsResult result) {
      result.confirm();
      return !Utils.canShow(mActivity) || super.onJsConfirm(view, url, message, result);
    }

    @Override
    public boolean onJsPrompt(WebView view, String url, String message, String defaultValue,
        JsPromptResult result) {
      if (callback != null) {
        boolean dealMessage = callback.onJsPrompt(message, result);
        if (dealMessage) {
          return true;
        }
      }
      return !Utils.canShow(mActivity) || super.onJsPrompt(view, url, message, defaultValue,
          result);
    }
  }

  private class MyWebViewClient extends WebViewClient {
    @Override public void onPageFinished(WebView view, String url) {
      super.onPageFinished(view, url);

      if (mWebView == null) {
        return;
      }

      mUrl = url;
      if (TextUtils.isEmpty(mUrlTitle)) {
        mUrlTitle = mWebView.getTitle();
      }
      mView.setProgressbarSize();
      // hideProgressBar();
    }

    @Override public void onReceivedError(WebView view, int errorCode, String description,
        String failingUrl) {
      super.onReceivedError(view, errorCode, description, failingUrl);

      if (mWebView == null) {
        return;
      }
      mWebView.stopLoading();
      mView.showCrashTip();
      MyWebViewActivity.this.onReceivedError(errorCode);
    }

    @Override public void onPageStarted(WebView view, String url, Bitmap favicon) {
      super.onPageStarted(view, url, favicon);
      if (mWebView == null) {
        return;
      }
      mUrl = url;
      mView.startLoading();
      //  showProgressBar();
    }

    @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
      if (TextUtils.isEmpty(url)) {
        return false;
      }
      //todo 可以添加通用URL拦截

      // 非通用url规则，则用当前webview直接打开
      mUrl = url;
      refresh();
      return true;
    }
  }

  private class WebViewDownLoadListener implements DownloadListener {

    @Override public void onDownloadStart(String url, String userAgent, String contentDisposition,
        String mimetype, long contentLength) {
      if (TextUtils.isEmpty(url)) {
        return;
      }
      Uri uri = Uri.parse(url);
      Intent intent = new Intent(Intent.ACTION_VIEW, uri);
      startActivity(intent);
    }
  }
}
