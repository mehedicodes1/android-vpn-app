package com.android.bytesbee.vpnapp.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebView;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.bytesbee.vpnapp.constants.IConstants;
import com.android.bytesbee.vpnapp.R;
import com.android.bytesbee.vpnapp.utils.Utils;

public class TermsOfServicesActivity extends AppCompatActivity implements View.OnClickListener {
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_of_services);
        init();
        initData();
    }

    public void init() {
        webView = findViewById(R.id.webView);
        final ImageView imgBack = findViewById(R.id.imgBack);
        imgBack.setOnClickListener(this);
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void initData() {
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setAllowFileAccess(true);
        Utils.setThemeToWebView(this, webView);
        webView.loadUrl(IConstants.PATH_ASSET_FOLDER + IConstants.TERMS_OF_USE);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.imgBack) {
            onBackPressed();
        }
    }
}