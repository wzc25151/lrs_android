package com.wzc.lrsdemo.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.wzc.lrsdemo.base.App;
import com.wzc.lrsdemo.R;
import com.wzc.lrsdemo.base.BaseFragment;

/**
 * Created by Administrator on 2017/3/7.
 */

public class MessageFragment extends BaseFragment {
    private AppCompatActivity activity;
    private View root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        root = inflater.inflate(R.layout.fragment_message, container, false);
        init();
        return root;
    }

    private void init() {
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.fragment_message);
        toolbar.setTitle("消息");

        toolbar.findViewById(R.id.menu_message_search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("search");
            }
        });
        toolbar.findViewById(R.id.menu_message_contacts).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showToast("contacts");
            }
        });
    }

}
