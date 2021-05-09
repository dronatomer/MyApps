package com.ramayan.mycom.Utils;

import android.content.Context;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.bumptech.glide.request.RequestOptions;
import com.ramayan.mycom.R;

import ss.com.bannerslider.ImageLoadingService;

public class GlideImageLoadingService implements ImageLoadingService {
    public Context context;

    public GlideImageLoadingService(Context context) {
        this.context = context;
    }

    @Override
    public void loadImage(String url, ImageView imageView) {
        RequestOptions requestOption =
                new RequestOptions().placeholder(R.drawable.box).
                        error(R.drawable.box)
                        .diskCacheStrategy(DiskCacheStrategy.DATA);
        Glide.with(context).load(url)
                .apply(requestOption)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    @Override
    public void loadImage(int resource, ImageView imageView) {
        RequestOptions requestOption =
                new RequestOptions().placeholder(R.drawable.box).
                        error(R.drawable.box)
                        .diskCacheStrategy(DiskCacheStrategy.DATA);
        Glide.with(context).load(resource)
                .apply(requestOption)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }

    @Override
    public void loadImage(String url, int placeHolder, int errorDrawable, ImageView imageView) {
        RequestOptions requestOption =
                new RequestOptions().placeholder(placeHolder).
                        error(errorDrawable)
                        .diskCacheStrategy(DiskCacheStrategy.DATA);
        Glide.with(context).load(url)
                .apply(requestOption)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(imageView);
    }
}
