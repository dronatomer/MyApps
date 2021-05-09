package com.ramayan.mycom.interfaces;


import com.ramayan.mycom.Model.Videos;



public interface ItemEventsListener<Model> {
    void onShareClicked(String itemId);

    void onFavoriteClicked(Videos video, boolean isChecked);

    void onItemClick(Model model, int position); //handle click on a row (video or playlist)
}
