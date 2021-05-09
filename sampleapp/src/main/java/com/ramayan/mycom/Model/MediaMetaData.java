package com.ramayan.mycom.Model;
import android.os.Parcel;
import android.os.Parcelable;

import java.io.Serializable;

public class MediaMetaData implements Serializable {
    private String mediaId;
    private String mediaUrl;
    private String mediaTitle;
    private String mediaDescription;
    private String mediaAlbum;
    private String mediaComposer;
    private String type;
    private String mediaImage;
    private int playState;
    private boolean fav;




    public MediaMetaData() {
    }


    public boolean isFav() {
        return fav;
    }

    public void setFav(boolean fav) {
        this.fav = fav;
    }
    public int describeContents() {
        return 0;
    }

    public String getMediaId() {
        return this.mediaId;
    }

    public void setMediaId(String mediaId) {
        this.mediaId = mediaId;
    }

    public String getMediaUrl() {
        return this.mediaUrl;
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
    }

    public String getMediaTitle() {
        return this.mediaTitle;
    }

    public void setMediaTitle(String mediaTitle) {
        this.mediaTitle = mediaTitle;
    }

    public String getMediaDescription() {
        return mediaDescription;
    }

    public void setMediaDescription(String mediaDescription) {
        this.mediaDescription = mediaDescription;
    }

    public String getMediaAlbum() {
        return this.mediaAlbum;
    }

    public void setMediaAlbum(String mediaAlbum) {
        this.mediaAlbum = mediaAlbum;
    }

    public String getMediaComposer() {
        return this.mediaComposer;
    }

    public void setMediaComposer(String mediaComposer) {
        this.mediaComposer = mediaComposer;
    }

    public String gettype() {
        return this.type;
    }

    public void settype(String type) {
        this.type = type;
    }

    public String getmediaImage() {
        return this.mediaImage;
    }

    public void setmediaImage(String mediaImage) {
        this.mediaImage = mediaImage;
    }

    public int getPlayState() {
        return this.playState;
    }

    public void setPlayState(int playState) {
        this.playState = playState;
    }
}
