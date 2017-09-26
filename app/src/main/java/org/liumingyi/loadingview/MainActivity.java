package org.liumingyi.loadingview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class MainActivity extends AppCompatActivity {

  LoadingView loadingView;

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    loadingView = (LoadingView) findViewById(R.id.loading_view);
  }

  public void startLoading(View view) {
    loadingView.loading();
  }

  public void stopLoading(View view) {
    loadingView.stop();
  }
}
