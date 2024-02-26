package com.android.bytesbee.vpnapp.activity;

import static com.android.bytesbee.vpnapp.constants.IConstants.ALL;
import static com.android.bytesbee.vpnapp.constants.IConstants.ALL_APPS;
import static com.android.bytesbee.vpnapp.constants.IConstants.ONLY_SELECT;
import static com.android.bytesbee.vpnapp.constants.IConstants.SELECT_NOT;
import static com.android.bytesbee.vpnapp.constants.IConstants.SYSTEM;
import static com.android.bytesbee.vpnapp.constants.IConstants.USER;
import static com.android.bytesbee.vpnapp.constants.IConstants.ZERO;
import static com.android.bytesbee.vpnapp.managers.SessionManager.KEY_ONLY_SELECT;
import static com.android.bytesbee.vpnapp.managers.SessionManager.KEY_SELECT_NOT;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.RadioGroup;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.android.bytesbee.vpnapp.async.BaseTask;
import com.android.bytesbee.vpnapp.async.TaskRunner;
import com.android.bytesbee.vpnapp.utils.CountAnimationTextView;
import com.android.bytesbee.vpnapp.R;
import com.android.bytesbee.vpnapp.adapter.AppListAdapter;
import com.android.bytesbee.vpnapp.managers.SessionManager;
import com.android.bytesbee.vpnapp.models.AppListModel;
import com.android.bytesbee.vpnapp.utils.Utils;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.radiobutton.MaterialRadioButton;
import com.l4digital.fastscroll.FastScrollRecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class InstalledAppActivity extends BaseAppActivity implements AppListAdapter.SwitchListener {

    private ImageView imgBack;
    private ArrayList<AppListModel> appListModels, myListModels;
    private AppListAdapter appListAdapter;
    private ChipGroup chipGroup;
    private CountAnimationTextView countAnimationTextView;
    private RadioGroup radioGroup;
    private MaterialRadioButton rdoAll, rdoSelectedNo, rdoOnlySelect;
    private HashMap<String, String> hashSelectedNo = new HashMap<>();
    private HashMap<String, String> hashOnlySelect = new HashMap<>();
    private int initialValue = 0;
    private int rdoSelectedValue = ALL;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_installed_apps);
        initViews();
        initListener();
        showInitialAppList();
    }

    private void initViews() {
        appListModels = new ArrayList<>();
        myListModels = new ArrayList<>();
        final Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        appListAdapter = new AppListAdapter(mActivity, myListModels, this);
        FastScrollRecyclerView mRecyclerView = findViewById(R.id.recyclerViewApp);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mActivity));
        mRecyclerView.setAdapter(appListAdapter);
        chipGroup = findViewById(R.id.chipGroup);
        countAnimationTextView = findViewById(R.id.countAnimationTextView);
        radioGroup = findViewById(R.id.radioGroup);
        rdoAll = findViewById(R.id.chkAll);
        rdoSelectedNo = findViewById(R.id.chkSelectedNot);
        rdoOnlySelect = findViewById(R.id.chkOnlySelected);
        imgBack = findViewById(R.id.imgBack);

    }

    private void initListener() {
        imgBack.setOnClickListener(view -> onBackPressed());

        chipGroup.setOnCheckedStateChangeListener((group, checkedIds) -> {
            if (group.getCheckedChipId() == R.id.chipUser) {
                rdoSelectedValue = USER;
            } else if (group.getCheckedChipId() == R.id.chipSystem) {
                rdoSelectedValue = SYSTEM;
            } else {
                rdoSelectedValue = ALL;
            }
            showAppList();
        });

        radioGroup.setOnCheckedChangeListener((radioGroup, i) -> {
            if (radioGroup.getCheckedRadioButtonId() == R.id.chkAll) {
                SessionManager.get().setConnectTunnel(ALL_APPS);
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.chkSelectedNot) {
                SessionManager.get().setConnectTunnel(SELECT_NOT);
            } else if (radioGroup.getCheckedRadioButtonId() == R.id.chkOnlySelected) {
                SessionManager.get().setConnectTunnel(ONLY_SELECT);
            }
            showAppList();
        });

        final int selectedItem = SessionManager.get().getConnectTunnel();
        if (selectedItem == ALL_APPS) {
            rdoAll.setChecked(true);
        } else if (selectedItem == SELECT_NOT) {
            rdoSelectedNo.setChecked(true);
        } else if (selectedItem == ONLY_SELECT) {
            rdoOnlySelect.setChecked(true);
        }
    }

    private void showInitialAppList() {
        initialValue = appListModels.size();
        appListModels.clear();
        BaseTask b = new BaseTask() {
            @Override
            public void setUiForLoading() {
                super.setUiForLoading();
                showProgress();
            }

            @Override
            public Object call() {
                try {
                    appListModels = getInitialInstalledApps();
                } catch (Exception e) {
                    Utils.getErrors(e);
                }
                return null;
            }

            @Override
            public void setDataAfterLoading(Object result) {
                hideProgress();
                showAppList();
            }
        };
        final TaskRunner runner = new TaskRunner();
        runner.executeAsync(b);
    }

    private void showAppList() {
        initialValue = myListModels.size();
        myListModels.clear();
        hashSelectedNo = SessionManager.get().getHashMap(KEY_SELECT_NOT);
        hashOnlySelect = SessionManager.get().getHashMap(KEY_ONLY_SELECT);
        myListModels = getSavedInstalledApps();
        appListAdapter.clearListData();
        appListAdapter.setListData(myListModels);
        appListAdapter.notifyItemRangeChanged(0, myListModels.size());
        countAnimationTextView.setAnimationDuration(2000).countAnimation(initialValue, myListModels.size());

    }

    private ArrayList<AppListModel> getSavedInstalledApps() {
        int value = SessionManager.get().getConnectTunnel();
        for (int i = ZERO; i < appListModels.size(); i++) {
            AppListModel app = appListModels.get(i);
            if (value == ALL_APPS) {
                app.setSelected(true);
            } else {
                if (value == SELECT_NOT) {
//                    if (hashSelectedNo != null)
                    app.setSelected((hashSelectedNo != null && hashSelectedNo.containsKey(app.getAppPackageName())));
                } else {
//                    if (hashOnlySelect != null)
                    app.setSelected((hashOnlySelect != null && hashOnlySelect.containsKey(app.getAppPackageName())));
                }
            }
            if (rdoSelectedValue == USER) {
                if (app.isSystemApp()) {
                    continue;
                }
                myListModels.add(app);
            } else if (rdoSelectedValue == SYSTEM) {
                if (app.isSystemApp()) {
                    myListModels.add(app);
                }
            } else {
                myListModels.add(app);
            }

        }
        Collections.sort(myListModels, (s1, s2) -> (s1.getAppName().toLowerCase()).compareTo(s2.getAppName().toLowerCase()));
        return myListModels;
    }

    @Override
    public void onListener(String packageName) {
        try {
            final int selectedItem = SessionManager.get().getConnectTunnel();
            if (selectedItem == SELECT_NOT) {
                hashSelectedNo.put(packageName, packageName);
                SessionManager.get().saveHashMap(KEY_SELECT_NOT, hashSelectedNo);
            } else if (selectedItem == ONLY_SELECT) {
                hashOnlySelect.put(packageName, packageName);
                SessionManager.get().saveHashMap(KEY_ONLY_SELECT, hashOnlySelect);
            }
//            Utils.sout("Put Package: " + selectedItem + " >>> " + packageName);
        } catch (Exception e) {
            Utils.getErrors(e);
        }
    }

    @Override
    public void offListener(String packageName) {
        try {
            final int selectedItem = SessionManager.get().getConnectTunnel();
            if (selectedItem == SELECT_NOT) {
                hashSelectedNo.remove(packageName);
                SessionManager.get().saveHashMap(KEY_SELECT_NOT, hashSelectedNo);
            } else if (selectedItem == ONLY_SELECT) {
                hashOnlySelect.remove(packageName);
                SessionManager.get().saveHashMap(KEY_ONLY_SELECT, hashOnlySelect);
            }
        } catch (Exception ignored) {
        }
    }

    private ArrayList<AppListModel> getInitialInstalledApps() {
        List<PackageInfo> packs = getPackageManager().getInstalledPackages(PackageManager.GET_ACTIVITIES);
        for (int i = ZERO; i < packs.size(); i++) {
            PackageInfo p = packs.get(i);
            String appName = p.applicationInfo.loadLabel(getPackageManager()).toString();
            Drawable icon = p.applicationInfo.loadIcon(getPackageManager());
            String packages = p.applicationInfo.packageName;
            AppListModel app = new AppListModel(appName, icon, packages, true);
            app.setSystemApp(isSystemPackage(p));
            appListModels.add(app);
        }

        Collections.sort(appListModels, (s1, s2) -> (s1.getAppName().toLowerCase()).compareTo(s2.getAppName().toLowerCase()));
        return appListModels;
    }

    private boolean isSystemPackage(PackageInfo pkgInfo) {
        return (pkgInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        finish();
        return super.onOptionsItemSelected(item);

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_left);
    }
}
