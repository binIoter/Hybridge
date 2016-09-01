package com.binioter.hybridge.extra;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;
import com.binioter.hybridge.R;
import com.binioter.hybridge.core.JsPromptBridge;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;

/**
 * 创建时间: 2016/08/02  <br>
 * 作者: binIoter <br>
 * 描述:WebViewActivity的基类，封装一显示样式和交互方式以及通用逻辑
 * 外部使用BrowserHelper提供的startWebActivity方法调用
 */
public abstract class BaseWebViewActivity extends Activity implements Handler.Callback {

  public static final String TAG = BaseWebViewActivity.class.getSimpleName();
  public static final int URL_NOT_FOUND_ERROR_CODE = -2;
  public static final int URL_LOAD_TIME_OUT = 10 * 1000;
  public static final int TIME_OUT_MSG_CODE = 555;
  public final Handler mHandler = new Handler(this);
  protected WebActivityView mView;

  protected String mUrl;
  protected String mUrlTitle;

  /**
   * 是否需要更新cookie
   */
  protected boolean mIsUpdateCookie;
  /**
   * 是否需要写入cookie
   */
  protected boolean mNeedCookie;
  /**
   * 是否启用JS
   */
  protected boolean mEnableJs;

  /**
   * 是否显示NavigationBar
   */
  protected boolean mIsShowNavBar;
  /**
   * 接受从外界动态绑定javascriptInterface，可以绑定多个
   */
  private HashMap<String, JavascriptInterface> mJsInterfaces = null;
  private Timer mTimer;

  public boolean handleMessage(Message msg) {
    if (msg.what == TIME_OUT_MSG_CODE) {
      onReceivedError(URL_NOT_FOUND_ERROR_CODE);
      return true;
    }
    return false;
  }

  @Override public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    initWebView();

    mView = new WebActivityView(this);
    initData();
    mView.initWebView();
    mView.setOnNoDataViewClick(new View.OnClickListener() {
      @Override public void onClick(View v) {
        if (!Utils.isNetworkConnected(getApplicationContext())) {
          Toast.makeText(BaseWebViewActivity.this,
              getApplicationContext().getString(R.string.net_error), Toast.LENGTH_SHORT).show();
          return;
        }
        mView.hideNoDataView();
        refresh();
      }
    });
    refresh();
  }

  @Override protected void onNewIntent(Intent intent) {
    super.onNewIntent(intent);
    mUrl = intent.getStringExtra(WebViewActivityConfig.TAG_URL);
    refresh();
  }

  private void initData() {
    Intent intent = getIntent();
    if (intent == null) {
      return;
    }
    mUrlTitle = intent.getStringExtra(WebViewActivityConfig.TAG_TITLE);
    mUrl = intent.getStringExtra(WebViewActivityConfig.TAG_URL);
    //加载本地html注释掉这段代码
    //if (mUrl != null && !mUrl.startsWith("http://") && !mUrl.startsWith("https://")) {
    //  mUrl = "http://".concat(mUrl);
    //}
    mNeedCookie = intent.getBooleanExtra(WebViewActivityConfig.TAG_COOKIE, false);
    mIsUpdateCookie = intent.getBooleanExtra(WebViewActivityConfig.TAG_COOKIE_UPDATE, false);
    mEnableJs = intent.getBooleanExtra(WebViewActivityConfig.TAG_ENABLE_JS, false);
    mIsShowNavBar = intent.getBooleanExtra(WebViewActivityConfig.TAG_TITLE_BAR, true);

    if (TextUtils.isEmpty(mUrl)) {
      return;
    }
  }

  @Override protected void onResume() {
    super.onResume();
    if (getWebView() == null) {
      return;
    }

    if (getWebView() != null && isNeedUpdateCookie()) {
      initCookie();
      return;
    }
  }

  @Override protected void onPause() {
    super.onPause();
    if (getWebView() == null) {
      return;
    }
  }

  @Override protected void onStop() {
    super.onStop();
  }

  @Override protected void onDestroy() {
    if (getWebView() != null) {
      getWebView().setVisibility(View.GONE);
    }

    webViewDestory();

    if (mView != null) {
      mView.release();
    }

    super.onDestroy();
  }

  @Override public void finish() {
    // 不需要回传数据
    setResult(RESULT_OK);

    super.finish();
  }

  /**
   * 刷新WebView，重新加载数据
   */
  public void refresh() {
    hideCrashTip();
    //加载本地测试使用
    //if (URLUtil.isNetworkUrl(mUrl)) {
    showProgressBar();
    //try {
    //url白名单校验
    //  if( new URL(mUrl).getHost().equals("m.taobao.com")){
    //    loadUrl(mUrl);
    //  }
    //} catch (MalformedURLException e) {
    //  e.printStackTrace();
    //}
    loadUrl(mUrl);
    //}
  }

  /**
   * 刷新WebView，重新加载数据
   */
  public void refreshIgnoreFormat() {
    hideCrashTip();
    showProgressBar();
    loadUrl(mUrl);
  }

  /**
   * 调用WebView本身的一些方法，有视频音频播放的情况下，必须加这个
   */
  protected void callHiddenWebViewMethod(String name) {
    if (getWebView() != null) {
      try {
        Method method = WebView.class.getMethod(name);
        method.invoke(getWebView());
      } catch (Exception ex) {
        Log.e(TAG, ex.getMessage());
      }
    }
  }

  @Override public void onBackPressed() {
    super.onBackPressed();
    webViewGoBack();
  }

  @Override public boolean onKeyDown(int keyCode, KeyEvent event) {
    if (keyCode == KeyEvent.KEYCODE_BACK) {
      if (webViewGoBack()) {
        return true;
      }
    }
    return super.onKeyDown(keyCode, event);
  }

  /**
   * 创建WebView，子类实现
   *
   * @return WebView
   */
  public abstract View createWebView();

  public abstract View getWebView();

  public abstract void addJavascriptInterface(Object obj, String interfaceName);

  public abstract void initWebView();

  public abstract void loadUrl(String url);

  public abstract void webViewDestory();

  public abstract boolean webViewGoBack();

  public abstract void initCookie();

  public abstract void onReceivedError(int errorCode);

  public void showProgressBar() {
    mView.showProgressBar();
  }

  public void hideProgressBar() {
    mView.hideProgressBar();
  }

  public void showCrashTip() {
    mView.showCrashTip();
  }

  public void hideCrashTip() {
    mView.hideCrashTip();
  }

  public void hideNoDataView() {
    mView.hideNoDataView();
  }

  public void showNoDataView() {
    mView.showNoDataView();
  }

  /**
   * 给WebView增加js interface，供FE调用
   */
  @android.webkit.JavascriptInterface protected void addJavascriptInterface() {
    if (mJsInterfaces == null) {
      mJsInterfaces = new HashMap<String, JavascriptInterface>();
    }
    // 添加一个通用的js interface接口：CommonJsBridge
    if (!mJsInterfaces.containsKey("CommonJsBridge")) {
      mJsInterfaces.put("CommonJsBridge", new JavascriptInterface() {

        @Override public Object createJsInterface(Context context) {
          return new JsPromptBridge();
        }
      });
    }

    // 增加javascript接口的支持
    Iterator<String> it = mJsInterfaces.keySet().iterator();
    while (it.hasNext()) {
      String key = it.next();
      Object jsInterface = mJsInterfaces.get(key).createJsInterface(this.getApplicationContext());
      addJavascriptInterface(jsInterface, key);
    }
  }

  /**
   * 是否需要更新cookie
   */
  private boolean isNeedUpdateCookie() {
    // TODO: 16/9/1 根据自身业务逻辑处理cookie更新情况
    return mIsUpdateCookie;
  }

  public interface JavascriptInterface {

    public Object createJsInterface(Context context);
  }
}
