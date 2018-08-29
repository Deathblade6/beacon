package com.example.deathblade.beaconurl;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;

public class Website extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.webview);
        Bundle bundle = getIntent().getExtras();
        String url = bundle.getString("URL");
        Log.i("Url2=",url);
        WebView wb = findViewById(R.id.web);
        wb.getSettings().setJavaScriptEnabled(true);
        wb.loadUrl(url);
    }
}
