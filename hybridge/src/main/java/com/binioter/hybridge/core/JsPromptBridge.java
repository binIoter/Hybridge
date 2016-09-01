package com.binioter.hybridge.core;

import android.text.TextUtils;
import android.webkit.JsPromptResult;
import java.util.ArrayList;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * 创建时间: 2016/08/02  <br>
 * 作者: binIoter <br>
 * 描述:解析js传过来的数据
 */
public class JsPromptBridge {
  //js与native交互的标识，用于过滤非法调用
  //推荐使用公司名或缩写作为前缀 eg:lj_protocol
  private static final String KEY_PROTOCOL = "lj_protocol";
  private static final String KEY_METHOD_NAME = "methodName";
  private static final String KEY_PARAM = "param";
  private ArrayList<JsPromptInterface> jsInterfaceList = new ArrayList<JsPromptInterface>();

  public void addJSPromptInterface(JsPromptInterface jsInterface) {
    if (jsInterface != null) {
      jsInterfaceList.add(jsInterface);
    }
  }

  public void removeJSPromptInterface(JsPromptInterface jsInterface) {
    if (jsInterface != null) {
      jsInterfaceList.remove(jsInterface);
    }
  }

  public void removeAllJSPromptInterface() {
    jsInterfaceList.clear();
  }

  public boolean parseJSON(String json, JsPromptResult result) {
    if (TextUtils.isEmpty(json)) {
      return false;
    }
    try {
      JSONObject obj = new JSONObject(json);
      String methodName = obj.optString(KEY_METHOD_NAME);
      String params = obj.optString(KEY_PARAM);
      if (!obj.has(KEY_PROTOCOL) || TextUtils.isEmpty(methodName) ||
          TextUtils.isEmpty(params)) {
        //调用格式非法，hybridge不处理此调用
        return false;
      }
      return processListener(methodName, params, result);
    } catch (JSONException e) {
    }
    return false;
  }

  private boolean processListener(String methodName, String params, JsPromptResult result) {
    int listenerCount = jsInterfaceList == null ? 0 : jsInterfaceList.size();
    boolean processResult = false;
    if (listenerCount > 0) {
      for (JsPromptInterface jsInterface : jsInterfaceList) {
        if (jsInterface != null) {
          processResult = jsInterface.dojsinterface(methodName, params, result);
          // 如果处理了的话，那么就返回true，表示处理成功
          if (processResult == true) {
            return true;
          }
        }
      }
    }
    return false;
  }
}
