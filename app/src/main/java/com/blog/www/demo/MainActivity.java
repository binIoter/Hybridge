package com.blog.www.demo;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import com.binioter.hybridge.extra.BrowserUtil;

public class MainActivity extends Activity {

  private Button button;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    button = (Button) findViewById(R.id.btn);
    button.setOnClickListener(new View.OnClickListener() {
      @Override public void onClick(View view) {
        BrowserUtil.startWebActivity(MainActivity.this, "file:///android_asset/demo.html",
            MyWebViewActivity.class);
      }
    });
  }
}
