package com.binioter.hybridge.extra;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.view.View;

/**
 * 创建时间: 2016/7/30  <br>
 * 作者: binIoter <br>
 * 描述: 工具类
 */
public class Utils {

  /**
   * 判断是否有网络连接
   */
  public static boolean isNetworkConnected(Context context) {
    if (context != null) {
      ConnectivityManager mConnectivityManager =
          (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
      NetworkInfo mNetworkInfo = mConnectivityManager.getActiveNetworkInfo();
      if (mNetworkInfo != null) {
        return mNetworkInfo.isAvailable();
      }
    }
    return false;
  }

  /**
   * 判断当前的Activity能否显示Dialog或者PopupWindown
   */
  public static final boolean isActivityCanShowDialogOrPopupWindow(Activity activity) {
    if (activity == null) {
      return false;
    }
    if (isActivityFinishing(activity)) {
      return false;
    }
    if (isTokenValid(activity.getWindow().getDecorView())) {
      return true;
    }
    if (!activity.getWindow().isActive()) {
      return true;
    }
    return false;
  }

  private static final boolean isTokenValid(View view) {
    if (view != null) {
      IBinder binder = view.getWindowToken();
      if (binder != null) {
        try {
          if (binder.isBinderAlive() && binder.pingBinder()) {
            return true;
          }
        } catch (Exception e) {

        }
      }
    }
    return false;
  }

  private static final boolean isActivityFinishing(Context context) {
    if (context instanceof Activity) {
      return ((Activity) context).isFinishing();
    }
    return true;
  }

  public static String getVersionName(Context context) {
    // 获取packagemanager的实例
    PackageManager packageManager = context.getPackageManager();
    // getPackageName()是你当前类的包名，0代表是获取版本信息
    PackageInfo packInfo = null;
    try {
      packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);

      return packInfo.versionName;
    } catch (PackageManager.NameNotFoundException e) {
      e.printStackTrace();
    }
    return null;
  }

  /**
   * activity是否可以显示dialog或者popwindow
   */
  public static boolean canShow(Context context) {
    return (context instanceof Activity) && isActivityCanShowDialogOrPopupWindow(
        (Activity) context);
  }
}