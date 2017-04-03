package com.wzc.lrsdemo.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.wzc.lrsdemo.R;
import com.wzc.lrsdemo.base.BaseFragment;
import com.wzc.lrsdemo.data.RoleBuild;
import com.wzc.lrsdemo.data.RoleData;
import com.wzc.lrsdemo.utils.GlideUtil;
import com.wzc.lrsdemo.widget.AutoScrollViewPager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/3/7.
 */

public class RankingFragment extends BaseFragment {
    private AppCompatActivity activity;
    private View root;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (AppCompatActivity) getActivity();
        root = inflater.inflate(R.layout.fragment_ranking, container, false);
        init();
        return root;
    }

    private void init() {
        Toolbar toolbar = (Toolbar) root.findViewById(R.id.toolbar);
        toolbar.inflateMenu(R.menu.fragment_ranking);
        toolbar.setTitle("排行榜");


        AutoScrollViewPager autoScrollViewPager = (AutoScrollViewPager) root.findViewById(R.id.ranking_vp);
        List<ImageView> imgList = new ArrayList<ImageView>();
        ImageView iv0 = new ImageView(activity);
        iv0.setImageResource(R.mipmap.img_banner_0);
        iv0.setScaleType(ImageView.ScaleType.FIT_XY);
        imgList.add(iv0);
        ImageView iv1 = new ImageView(activity);
        iv1.setImageResource(R.mipmap.img_banner_1);
        iv1.setScaleType(ImageView.ScaleType.FIT_XY);
        imgList.add(iv1);
        ImageView iv2 = new ImageView(activity);
        iv2.setImageResource(R.mipmap.img_banner_2);
        iv2.setScaleType(ImageView.ScaleType.FIT_XY);
        imgList.add(iv2);
        autoScrollViewPager.setupData(imgList);

        autoScrollViewPager.setupIndicator((RelativeLayout) root.findViewById(R.id.ranking_indicator_v));

        List<RoleData> lvList = new ArrayList<RoleData>();
        List<RoleData> popularityList = new ArrayList<RoleData>();
        for (int i = 0; i < 10; i++) {
            lvList.add(RoleBuild.build(i));
            popularityList.add(RoleBuild.build(i));
        }

        RecyclerView rank_lv_rv = (RecyclerView) root.findViewById(R.id.rank_lv_rv);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(activity);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        rank_lv_rv.setLayoutManager(linearLayoutManager);
        rank_lv_rv.setAdapter(new RankingRVAdapter(lvList));

        RecyclerView rank_popularity_rv = (RecyclerView) root.findViewById(R.id.rank_popularity_rv);
        LinearLayoutManager linearLayoutManager2 = new LinearLayoutManager(activity);
        linearLayoutManager2.setOrientation(LinearLayoutManager.HORIZONTAL);
        rank_popularity_rv.setLayoutManager(linearLayoutManager2);
        rank_popularity_rv.setAdapter(new RankingRVAdapter(popularityList));


    }

    class RankingRVAdapter extends RecyclerView.Adapter<RankingRVAdapter.ViewHolder> {
        private List<RoleData> list;

        public RankingRVAdapter(List<RoleData> list) {
            this.list = list;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(activity).inflate(R.layout.item_ranking, parent, false);
            ViewHolder holder = new ViewHolder(v);

            holder.head_iv = (ImageView) v.findViewById(R.id.item_ranking_head_iv);
            holder.crown_iv = (ImageView) v.findViewById(R.id.item_ranking_crown_iv);
            holder.tv = (TextView) v.findViewById(R.id.item_ranking_tv);
            //给布局设置点击和长点击监听
//                view.setOnClickListener(this);
//                view.setOnLongClickListener(this);
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            RoleData roleData = list.get(position);
            GlideUtil.into(activity, roleData.getHeadImgUrl(), holder.head_iv, GlideUtil.CIRCLE);
            holder.tv.setText(roleData.getNickName());
            if (position == 0) {
                holder.crown_iv.setImageResource(R.mipmap.ic_rank_crown_1st);
            } else if (position == 1) {
                holder.crown_iv.setImageResource(R.mipmap.ic_rank_crown_2nd);
            } else if (position == 2) {
                holder.crown_iv.setImageResource(R.mipmap.ic_rank_crown_3rd);
            } else {
                holder.crown_iv.setImageDrawable(null);
            }
        }

        @Override
        public int getItemCount() {
            return list != null ? list.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ImageView head_iv;
            ImageView crown_iv;
            TextView tv;

            public ViewHolder(View itemView) {
                super(itemView);
            }
        }
    }


}
