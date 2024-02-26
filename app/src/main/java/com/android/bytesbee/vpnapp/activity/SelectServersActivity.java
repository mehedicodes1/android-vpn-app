package com.android.bytesbee.vpnapp.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.android.bytesbee.vpnapp.R;
import com.android.bytesbee.vpnapp.adapter.ServersPageAdapter;
import com.android.bytesbee.vpnapp.fragments.FreeServersFragment;

import java.util.ArrayList;
import java.util.List;

public class SelectServersActivity extends AppCompatActivity implements View.OnClickListener {
    private ViewPager2 viewPager;
    private ImageView imgBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_servers);
        init();
        initData();
        initListeners();
    }

    public void init() {
        viewPager = findViewById(R.id.viewPager);
        imgBack = findViewById(R.id.imgBack);
    }

    public void initData() {
        final List<Fragment> fragments = new ArrayList<>();
        fragments.add(new FreeServersFragment());
        final FragmentStateAdapter pagerAdapter = new ServersPageAdapter(this, fragments);
        viewPager.setAdapter(pagerAdapter);
    }

    public void initListeners() {
        imgBack.setOnClickListener(this);
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.imgBack) {
            onBackPressed();
        }
    }

}