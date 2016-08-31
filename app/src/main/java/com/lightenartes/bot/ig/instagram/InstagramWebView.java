package com.lightenartes.bot.ig.instagram;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.lightenartes.bot.ig.R;
import com.lightenartes.bot.ig.instagram.InstagramApp;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/24/15.
 */
public class InstagramWebView extends AppCompatActivity {

    private ProgressDialog mSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instagram_webview);

        mSpinner = new ProgressDialog(this);
        mSpinner.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mSpinner.setMessage("Loading...");

        Bundle extras = getIntent().getExtras();
        String mUrl = "";

        if(extras != null) {
            mUrl = extras.getString("url");
        }

        WebView myWebView = (WebView) findViewById(R.id.webview);
        myWebView.setVerticalScrollBarEnabled(false);
        myWebView.setHorizontalScrollBarEnabled(false);
        myWebView.getSettings().setJavaScriptEnabled(true);
        myWebView.loadUrl(mUrl);
        myWebView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(InstagramApp.mCallbackUrl)) {
                    String urls[] = url.split("=");
                    back(Activity.RESULT_OK, urls[1]);
                    return true;
                }
                return false;
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                back(Activity.RESULT_CANCELED, description);
            }

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                mSpinner.show();
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                mSpinner.dismiss();
            }
        });

    }

    private void back(int code, String message){
        Intent returnIntent = new Intent();
        returnIntent.putExtra("result", message);
        setResult(code, returnIntent);
        finish();
    }
}
