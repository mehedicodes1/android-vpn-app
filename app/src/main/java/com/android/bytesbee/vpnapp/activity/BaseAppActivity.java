package com.android.bytesbee.vpnapp.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.android.bytesbee.vpnapp.R;


/**
 * Created by BytesBee.
 *
 * @author BytesBee
 * @link http://bytesbee.com
 */

@SuppressLint("Registered")
public class BaseAppActivity extends AppCompatActivity {

    private ProgressDialog progress;
    public Activity mActivity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
    }

    private void initProgressDialog() {
        try {
            progress = new ProgressDialog(this);
            progress.setCancelable(false);
        } catch (Exception ignored) {

        }
    }

    public synchronized void showProgress() {
        if (progress == null) {
            initProgressDialog();
        }
        if (progress != null && !progress.isShowing()) {
            try {
                progress.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                progress.show();
                progress.setContentView(R.layout.dialog_progress);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    synchronized void hideProgress() {
        try {
            if (progress != null && progress.isShowing()) {
                progress.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
