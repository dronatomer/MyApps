package com.ramayan.mycom.Adaptors;


import com.ramayan.mycom.Model.Server;
import com.ramayan.mycom.R;

import java.util.List;

import ss.com.bannerslider.adapters.SliderAdapter;
import ss.com.bannerslider.viewholder.ImageSlideViewHolder;

public class MainSliderAdapter extends SliderAdapter {

    private List<Server> bannerSliderList;

    @Override
    public int getItemCount() {
        return bannerSliderList.size();
    }

    public MainSliderAdapter(List<Server> bannerSliderList){
        this.bannerSliderList = bannerSliderList;
    }

    @Override
    public void onBindImageSlide(final int position, ImageSlideViewHolder viewHolder) {
//        viewHolder.bindImageSlide(R.drawable.drawer_header_bg);
        viewHolder.bindImageSlide(bannerSliderList.get(position).getThumbnailUrl());
    }
}
