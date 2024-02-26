package com.android.bytesbee.vpnapp.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.viewpager2.widget.ViewPager2;

import com.android.bytesbee.vpnapp.managers.Screens;
import com.android.bytesbee.vpnapp.managers.SessionManager;
import com.android.bytesbee.vpnapp.models.Page;
import com.android.bytesbee.vpnapp.R;
import com.android.bytesbee.vpnapp.adapter.ViewPagerAdapter;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator3;

public class TakeToTourActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager2 viewPager;
    private Activity mActivity;
    private CircleIndicator3 mIndicator;
    private AppCompatButton btnGetStarted;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_to_tour);
        init();
        initData();
        initListeners();
    }

    public void init() {
        viewPager = findViewById(R.id.viewPager);
        mIndicator = findViewById(R.id.indicator);
        btnGetStarted = findViewById(R.id.btnGetStarted);
    }

    public void initData() {
        mActivity = this;
        displayPages(getMessage());
    }

    public void initListeners() {
        btnGetStarted.setOnClickListener(this);
    }

    public void displayPages(final List<Page> pages) {
        ViewPagerAdapter myCustomPagerAdapter = new ViewPagerAdapter(mActivity, pages);
        viewPager.setAdapter(myCustomPagerAdapter);
        mIndicator.setViewPager(viewPager);
    }

    public List<Page> getMessage() {
        List<Page> pages = new ArrayList<>();
        pages.add(new Page(R.raw.anim1, getResources().getString(R.string.strTourTitle1), getResources().getString(R.string.strTourMsg1)));
        pages.add(new Page(R.raw.anim2, getResources().getString(R.string.strTourTitle2), getResources().getString(R.string.strTourMsg2)));
        pages.add(new Page(R.raw.anim3, getResources().getString(R.string.strTourTitle3), getResources().getString(R.string.strTourMsg3)));
        return pages;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.btnGetStarted) {
            SessionManager.getInstance(this).setEntryDone();
            Screens.showClearTopScreen(mActivity, MainActivity.class);
        }
    }
}