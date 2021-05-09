package com.ramayan.mycom.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ramayan.mycom.Home.MainActivity;
import com.ramayan.mycom.Model.Videos;
import com.ramayan.mycom.R;
import com.ramayan.mycom.interfaces.OnItemSelected;

import java.util.List;

public class HomeAdapter extends RecyclerView.Adapter<HomeAdapter.ViewHolder> {

    private Context context;
    private List<Videos> playlists;
    private Context mContext;
    private OnItemSelected onItemSelected;

    private int position = 0;

    public HomeAdapter(Context context, List<Videos> playlists, MainActivity homeActivity) {
        super();
        this.context = context;
        this.playlists = playlists;
    }


    public void notifyAdapter() {
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.playlist_item, null);
        return new ViewHolder(v);
    }

    RequestOptions options = new RequestOptions()
            .centerCrop()
            .placeholder(R.drawable.default_image)
            .error(R.drawable.default_image)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH);

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {

        final Videos playlist = playlists.get(position);
        Glide.with(context).setDefaultRequestOptions(options).load(playlist.getThumbnail_url()).into(holder.ivImage);
        holder.tvName.setText(playlist.getTitle());
        holder.itemView.setTag(playlist);
    }

    @Override
    public int getItemCount() {
        return (null != playlists ? playlists.size() : 0);
    }

    public  void setOnClickListner(OnItemSelected onClickListner){
        this.onItemSelected=onClickListner;
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView ivImage;
        final TextView tvName;

        ViewHolder(View view) {
            super(view);
            ivImage = (ImageView) view.findViewById(R.id.iv_list_item);
            tvName = (TextView) view.findViewById(R.id.tv_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            HomeAdapter.this.onItemSelected.onVideoSelected(playlists.get(getAdapterPosition()), getAdapterPosition());
        }
    }


}