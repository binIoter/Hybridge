package com.binioter.hybridge.extra;

import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.binioter.hybridge.R;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 创建时间: 2016/08/03  <br>
 * 作者: binIoter <br>
 * 描述:BaseWebViewActivity对应的视图View
 */
public class WebActivityView {

  protected View mRoot = null;
  private BaseWebViewActivity mActivity;
  private LinearLayout mWebViewContainer;
  private TextView mWebViewCrashTip;
  private OnClickListener mNoDataViewListener;
  private UIHandler mUiHandler;
  private ProgressBar mProgressBar = null;
  private boolean mShowProgress;
  Timer mTimer;

  private class UIHandler extends Handler {
    @Override public void handleMessage(Message msg) {
      // TODO Auto-generated method stub
      if (mTimer != null) {
        mTimer.cancel();
        mTimer = null;
      }
      mProgressBar.setVisibility(View.GONE);
    }
  }

  public WebActivityView(BaseWebViewActivity activity) {
    mActivity = activity;
    initUI();
  }

  private void initUI() {
    mUiHandler = new UIHandler();
    mActivity.setContentView(R.layout.base_webview_activity);
    mRoot = mActivity.findViewById(R.id.root_view);
    mWebViewContainer = (LinearLayout) mActivity.findViewById(R.id.webview_container);
    mWebViewCrashTip = (TextView) mActivity.findViewById(R.id.webview_crash_tip);
    mProgressBar = (ProgressBar) mActivity.findViewById(R.id.webview_progress);
  }

  public boolean initWebView() {
    try {
      mWebViewContainer.addView(mActivity.createWebView());
      hideCrashTip();
      return true;
    } catch (Exception ex) {
      showCrashTip();
      return false;
    }
  }

  /**
   * 设置progress长度
   */
  public void setProgressbarSize() {
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
    if (mProgressBar.getProgress() < 100) {
      mTimer = new Timer();
      mTimer.schedule(new TimerTask() {
        @Override public void run() {
          if (mProgressBar.getProgress() < 100) {
            mProgressBar.setProgress(mProgressBar.getProgress() + 1);
          } else {
            mUiHandler.sendEmptyMessage(0);
          }
        }
      }, 0, 10);
    }
  }

  /**
   * 开始progress
   */
  public void startLoading() {
    if (!mShowProgress) return;
    if (mTimer != null) {
      mTimer.cancel();
      mTimer = null;
    }
    mProgressBar.setVisibility(View.VISIBLE);
    mProgressBar.setProgress(1);
    mTimer = new Timer();
    mTimer.schedule(new TimerTask() {
      @Override public void run() {
        if (mProgressBar.getProgress() < 80) {
          mProgressBar.setProgress(mProgressBar.getProgress() + 1);
        }
      }
    }, 0, 10);
  }

  public void showProgressBar() {
    mProgressBar.setVisibility(View.VISIBLE);
  }

  public void hideProgressBar() {
    mProgressBar.setVisibility(View.GONE);
  }

  public void showCrashTip() {
    mWebViewCrashTip.setVisibility(View.VISIBLE);
  }

  public void hideCrashTip() {
    mWebViewCrashTip.setVisibility(View.GONE);
  }

  public void hideNoDataView() {
  }

  public void showNoDataView() {

  }

  public void setOnNoDataViewClick(OnClickListener listener) {
    mNoDataViewListener = listener;
  }

  /**
   * 释放内存引用，避免内存泄露 只能用于onDestroy方法中
   */
  public void release() {

    if (mWebViewContainer != null) {
      mWebViewContainer.removeAllViews();
    }
  }
}
