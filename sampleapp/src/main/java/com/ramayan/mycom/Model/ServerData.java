package com.ramayan.mycom.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class ServerData implements Serializable {

    @SerializedName("plist_id")
    @Expose
    String plist_id;
    @SerializedName("jsondata")
    @Expose
    List<Server> thumnailList;
    @SerializedName("bhakti_json")
    @Expose
    ArrayList<MediaMetaData> bhakti_json;
    @SerializedName("reverse")
    @Expose
    boolean reverse;

    public ArrayList<MediaMetaData> getBhakti_json() {
        return bhakti_json;
    }

    public void setBhakti_json(ArrayList<MediaMetaData> bhakti_json) {
        this.bhakti_json = bhakti_json;
    }

    public boolean isReverse() {
        return reverse;
    }

    public void setReverse(boolean reverse) {
        this.reverse = reverse;
    }

    public List<Server> getThumnailList() {
        return thumnailList;
    }

    public void setThumnailList(List<Server> thumnailList) {
        this.thumnailList = thumnailList;
    }

    public String getPlist_id() {
        return plist_id;
    }

    public void setPlist_id(String plist_id) {
        this.plist_id = plist_id;
    }
}
