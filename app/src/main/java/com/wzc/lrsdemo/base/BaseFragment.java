package com.wzc.lrsdemo.base;

import android.support.v4.app.Fragment;

import com.wzc.lrsdemo.utils.AppUtil;
import com.wzc.lrsdemo.utils.GlideUtil;
import com.wzc.lrsdemo.utils.LogUtil;

/**
 * Created by Administrator on 2017/3/7.
 */

public class BaseFragment extends Fragment {

    public void showToast(String msg) {
        if (AppUtil.isSafe(getActivity())) {
            ((BaseActivity) getActivity()).showToast(msg);
        } else {
            LogUtil.e(this + "的 Activity 不安全\n" + msg);
        }
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName();
    }
}
