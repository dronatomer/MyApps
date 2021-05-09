package com.ramayan.mycom.Model;

import java.io.Serializable;

public class Server implements Serializable {

    private String ThumbnailUrl;

    public String getThumbnailUrl() {
        return ThumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        ThumbnailUrl = thumbnailUrl;
    }
}
