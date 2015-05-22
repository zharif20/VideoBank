package com.socbox.zharif.videobank;

/**
 * Created by zharif20 on 5/7/15.
 */


/**
 * Store the information of video
 */
public class Video {
    private int videoId;
    private String videoTitle;
    private String descriptionTitle;

    public Video (int videoId, String videoTitle, String descriptionTitle) {
        super();
        this.videoId            = videoId;
        this.videoTitle         = videoTitle;
        this.descriptionTitle   = descriptionTitle;
    }

    public int getVideoId(){
        return videoId;
    }

    public String getVideoTitle(){
        return videoTitle;
    }

    public String getDescriptionTitle(){
        return descriptionTitle;
    }
}
