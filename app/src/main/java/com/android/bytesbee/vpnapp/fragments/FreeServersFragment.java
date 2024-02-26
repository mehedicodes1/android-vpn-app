package com.android.bytesbee.vpnapp.fragments;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.bytesbee.vpnapp.activity.MainActivity;
import com.android.bytesbee.vpnapp.adapter.ServerListAdapter;
import com.android.bytesbee.vpnapp.constants.IConstants;
import com.android.bytesbee.vpnapp.managers.SessionManager;
import com.android.bytesbee.vpnapp.models.Server;
import com.android.bytesbee.vpnapp.utils.Utils;
import com.android.bytesbee.vpnapp.R;
import com.google.gson.Gson;

public class FreeServersFragment extends Fragment {
    private RecyclerView recyclerView;

    public FreeServersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_free_servers, container, false);
        init(view);
        initData();
        return view;
    }

    public void init(View view) {
        recyclerView = view.findViewById(R.id.recyclerView);
    }

    public void initData() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false));
        recyclerView.setHasFixedSize(true);
        setRecyclerView();
    }


    /**
     * sets items
     */
    @SuppressLint("NotifyDataSetChanged")
    public void setRecyclerView() {
        ServerListAdapter serverListAdapter = new ServerListAdapter(getActivity(), MainActivity.items, server -> {
            Utils.sout(server.getCountry());
            SessionManager.get().saveServer(server);
            Utils.showIntAds(getActivity(), myHandler);
        });
        recyclerView.setAdapter(serverListAdapter);
        serverListAdapter.notifyDataSetChanged();
    }

    private final Handler myHandler = new Handler(message -> {
        Server server = SessionManager.get().getServer();
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        intent.putExtra(IConstants.BUNDLE_KEY_SERVER, new Gson().toJson(server));
        startActivity(intent);
        return true;
    });
}