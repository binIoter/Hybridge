package com.binioter.hybridge.extra;

/**
 * 创建时间: 2016/09/18 14:51 <br>
 * 作者: zhangbin <br>
 * 描述: js与native交互协议
 */
public class ProtocolBean {
  //推荐使用公司名或缩写作为前缀 eg:bd_protocol
  private String protocol;
  private String methodName;
  private String param;

  public ProtocolBean(String protocol, String methodName, String param) {
    this.protocol = protocol;
    this.methodName = methodName;
    this.param = param;
  }

  public String getProtocol() {
    return protocol;
  }

  public void setProtocol(String protocol) {
    this.protocol = protocol;
  }

  public String getMethodName() {
    return methodName;
  }

  public void setMethodName(String methodName) {
    this.methodName = methodName;
  }

  public String getParam() {
    return param;
  }

  public void setParam(String param) {
    this.param = param;
  }
}
