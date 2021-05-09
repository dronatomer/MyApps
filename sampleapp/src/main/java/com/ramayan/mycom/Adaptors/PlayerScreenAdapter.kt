package com.ramayan.mycom.Adaptors

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.ramayan.mycom.Model.Videos
import com.ramayan.mycom.R


class PlayerScreenAdapter (val videoList: List<Videos>, var context : Context,var clickListener : (Videos, Int) -> Unit) : RecyclerView.Adapter<PlayerScreenAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayerScreenAdapter.ViewHolder {
        val v = LayoutInflater.from(parent.context).inflate(R.layout.player_list_items, parent, false)
        return ViewHolder(v)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_description.text = videoList.get(position).description
        holder.playlist_title.text = videoList.get(position).title
        if(videoList.get(position).thumbnail_url!=null) {
            Glide.with(context) //1
                    .load(videoList.get(position).thumbnail_url)
                    .into(holder.video_thumbnail)
        }
        holder?.row_item?.setOnClickListener { clickListener(videoList.get(position), position) }

    }

    override fun getItemCount(): Int {
        return videoList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var video_thumbnail = itemView.findViewById<ImageView>(R.id.video_thumbnail)
        var playlist_title = itemView.findViewById<TextView>(R.id.playlist_title)
        var tv_description = itemView.findViewById<TextView>(R.id.tv_description)
        var row_item = itemView.findViewById<LinearLayout>(R.id.row_item)
    }

}



