# Hybridge
新一代js与native交互框架，具有安全，简单，高可扩展性，在很多大厂有过类似实践
# demo 演示
> ![image]( https://github.com/binIoter/Hybridge/blob/master/app/src/main/assets/demo.gif)</p>

# 1.什么是hybridge？
> * hybridge是native与h5混合开发的桥梁，通过webview作为载体来展示H5内容
native进行交互，从而实现混合开发,hybridge具有安全，兼容性好，方便扩展等优点

#2.为什么会是hybridge？
>* 4.2版本之前的addjavascriptInterface接口引起的漏洞，可能导致恶意网页通过Js方法遍历刚刚通过addjavascriptInterface注入进来的类的所有方法从中获取到getClass方法，然后通过反射获取到Runtime对象，进而调用 Runtime对象的exec方法执行一些操作，恶意的Js代码如下：
<pre>
function execute(cmdArgs) {
    for (var obj in window){
      if ("getClass" in window[ obj]){
        alert(obj);
        return window[obj].getClass()
            .forName("java.lang.Runtime")
            .getMethod("getRuntime", null)
            .invoke(null, null)
            .exec(cmdArgs);
      }
    }
  }
</pre>
为了避免这个漏洞，即需要限制Js代码能够调用到的Native方法，官方于是在从4.2开始的版本可以通过为可以被Js调用的方法添加@JavascriptInterface注解来解决，而之前的版本虽然不能通过这种方法解决，但是可以使用Js的prompt方法进行解决，只不过需要和前端协商好一套公共的协议，除此之外，为了避免WebView加载任意url，也需要对url进行白名单检测，因此，基于prompt通道的安全，兼容性强的hybridge框架诞生了。

# 3.为什么是prompt？
> * 前端的同学都了解alert，confirm，prompt三个常用方法，这三个方法都能达到js与native交互的目的，区别在于，alert无返回值，confirm只有true和false两种返回结果，prompt可以返回字符串，另外对于前端来说prompt很少被使用，因此prompt是作为js与native传输最好的选择。

# 4.hybridge有哪些功能？

> * a.native调用js
> * b.js调用native可以携带返回值
> * c.url白名单检测，避免加载的非法的url
> * d.限制js能够调用的native方法
>* e.cookie管理
>* f.自定义javascript inteface，根据不同业务模块单独处理，便于分团队开发

# 5.hybridge如何使用？
>* a.引入hybridge module到自己的项目中,并添加依赖
>* b.BaseWebView 的init方法，改变useragent，推荐用自己的公司名＋版本号
<pre>StringBuilder uaBuilder = new StringBuilder();
    uaBuilder.append(getSettings().getUserAgentString());
    //推荐用公司名
    uaBuilder.append("github/");
    uaBuilder.append(Utils.getVersionName(mContext));
    </pre>
>* c.在JsPromptBridge类中定义好交互协议
<pre>//js与native交互的标识，用于过滤非法调用
  //推荐使用公司名或缩写作为前缀 eg:bd_protocol
  private static final String KEY_PROTOCOL = "bd_protocol";
  private static final String KEY_METHOD_NAME = "methodName";
  private static final String KEY_PARAM = "param";</pre>
>* d.在BaseWebViewActivity里的refresh方法中打开代码注释,为了对url进行白名单检查
<pre>//加载本地测试使用
    //if (URLUtil.isNetworkUrl(mUrl)) {
    showProgressBar();
    //try {
    //url白名单校验
    //  if( new URL(mUrl).getHost().equals("m.github.io")){
    //    loadUrl(mUrl);
    //  }
    //} catch (MalformedURLException e) {
    //  e.printStackTrace();
    //}
    loadUrl(mUrl);
    //}</pre>
>*  e.在BrowserUtil里的initCookie方法里添加初始化cookie的代码
<pre>
   //初始化Cookie，主要是在WebView中用到
  public static void initCookie(Context context) {
    // TODO: 16/8/30 初始化cookie
  }</pre>

>* f.调用BrowseUtil的startWebActivity方法开启一个新的h5页面
<pre>BrowserUtil.startWebActivity(MainActivity.this, "file:///android_asset/demo.html",
            MyWebViewActivity.class);
>* g.将MyJsBridge copy到自己的module，在dojsinterface里处理和js交互的逻辑
<pre>@Override
  public boolean dojsinterface(String methodName, String param, final JsPromptResult result) {
    //将返回值作为参数传给js
    result.confirm("result_ok");
    //以下是我们的业务代码
    if (TextUtils.isEmpty(methodName) || mContext == null) {
      return true;
    }
    //titleBar相关单独维护
    if (methodName.startsWith("bar_")) {
      initTitleBar(methodName, param,
          (TopTitleBar) ((Activity) mContext).findViewById(R.id.title_bar));
    } else {
      //具体业务相关这里处理
      switch (methodName) {
        case "toast":
          Toast.makeText(mContext, "我是native的toast", Toast.LENGTH_SHORT).show();
          break;
        case "delayToast":
          new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override public void run() {
              Toast.makeText(mContext, "我是延时的native的toast", Toast.LENGTH_SHORT).show();
            }
          }, 3000);
          break;
        case "openNewPage":
          BrowserUtil.startWebActivity(mContext, "file:///android_asset/demo.html", MyWebViewActivity.class);
          break;
        case "finish":
          if (mContext instanceof Activity) {
            ((Activity) mContext).finish();
          }
          break;
        default:
          break;
      }
    }
    return true;
  }</pre>
# 联系我
>* 使用过程中有任何问题请邮件或qq联系我
>* 邮件：bin_iot@163.com
>* QQ：15465688686

## License

    Copyright 2016 binIoter

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.
  
