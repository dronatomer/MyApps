package com.ramayan.mycom.Adaptors;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ramayan.mycom.Home.MainActivity;
import com.ramayan.mycom.Model.MediaMetaData;
import com.ramayan.mycom.R;

import java.util.ArrayList;
import java.util.List;


public class HorizontalMainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int VIEW_POSITION_REST = 1;
    private static final int VIEW_POSITION_ZERO = 0;
    private Context mContext;
    private com.dailymotion.android.interfaces.OnRadioSelected onRadioSelected;
    private List<MediaMetaData> metaDataList = new ArrayList<>();



    public HorizontalMainAdapter(MainActivity homeActivity, ArrayList<MediaMetaData> metaDataList) {
        this.mContext = homeActivity;
        this.onRadioSelected = homeActivity;
        this.metaDataList = metaDataList;
      //  this.youTubeVideoList = youTubeVideoList;
       // youTubeRecentlyWatched = new ArrayList<>();
    }


//    public void notifyData(){
//        RecyclerView.ViewHolder viewHolder;
//        viewHolder.
//    }

    class ViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        ImageView ivImage;
        final TextView radioName;

        ViewHolder(View view) {
            super(view);
            ivImage = (ImageView) view.findViewById(R.id.iv_circular_image);
            radioName = (TextView) view.findViewById(R.id.radio_name);
            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            HorizontalMainAdapter.this.onRadioSelected.onRadioSelected(v, metaDataList.get(getAdapterPosition()), getAdapterPosition());
        }
    }


    @NonNull
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int itemType) {
        View viewLayout = LayoutInflater.from(this.mContext).inflate(R.layout.item_circular_image, viewGroup, false);
        RecyclerView.ViewHolder view = new ViewHolder(viewLayout);
        return (view);
    }

    RequestOptions options = new RequestOptions()
            .fitCenter()
            .placeholder(R.drawable.box)
            .error(R.drawable.box)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .priority(Priority.HIGH);

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, final int i) {
        final ViewHolder Holder = (ViewHolder) viewHolder;

        Holder.radioName.setText(metaDataList.get(i).getMediaTitle());
        Glide.with(this.mContext).setDefaultRequestOptions(options).load(metaDataList.get(i).getmediaImage()).into(Holder.ivImage);

//        Holder.tvMore.setOnClickListener(new OnClickListener() {
//            @Override
//            public void onClick(View view) {
//
//                Intent intent = new Intent(mContext, VerticleListActivity.class);
//                intent.putExtra(AppConstants.PLIST_VERTICLE, (Serializable) youTubeRecentlyWatched);
//                mContext.startActivity(intent);
//
//            }
//        });
    }

    public int getItemCount() {
        if(metaDataList!=null)
        return metaDataList.size();
        return 0;
    }

}
