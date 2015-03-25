package com.chekoff.hackafeforum;

import android.graphics.Bitmap;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Plamen on 21.03.2015.
 */
public class Topics {
    String userAvatarURL;
    String username;
    String topicTitle;
    int viewsCount;
    int repliesCount;
    String lastPostedAt;
    String slug;
    int topicID;
    String userLastPosterAvatarURL;
    Bitmap userAvatar;
    String userLastPoster;
    Bitmap userLastPosterAvatar;

    String topicImageURL;
    Bitmap topicImageBitmap;
    Categories category;

    public String getPostDescription() {
        return postDescription;
    }

    public void setPostDescription(String postDescription) {
        this.postDescription = postDescription;
    }

    String postDescription;

    public Topics() {
    }

    public Topics(String userAvatarURL,
                  String username,
                  String topicTitle,
                  int viewsCount,
                  int repliesCount,
                  String lastPostedAt,
                  String slug,
                  int topicID,
                  Bitmap userAvatar,
                  String userLastPoster,
                  String userLastPosterAvatarURL,
                  Bitmap userLastPosterAvatar,
                  String topicImageURL,
                  Bitmap topicImageBitmap,
                  Categories category) {
        this.userAvatarURL = userAvatarURL;
        this.username = username;
        this.topicTitle = topicTitle;
        this.viewsCount = viewsCount;
        this.repliesCount = repliesCount;
        this.slug = slug;
        this.topicID = topicID;
        this.userAvatar = userAvatar;
        this.userLastPoster = userLastPoster;
        this.userLastPosterAvatar = userLastPosterAvatar;
        this.userLastPosterAvatarURL = userLastPosterAvatarURL;

        this.topicImageURL = topicImageURL;
        this.topicImageBitmap = topicImageBitmap;

        this.postDescription = "";

        this.category = category;

        //formatting date
        SimpleDateFormat fromFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        SimpleDateFormat toFormat = new SimpleDateFormat("dd-MM-yy");
        try {
            Date strToDate = fromFormat.parse(lastPostedAt);
            this.lastPostedAt = toFormat.format(strToDate);
        } catch (ParseException e) {
            e.printStackTrace();
            this.lastPostedAt = "";
        }
    }

    public String getUserAvatarURL() {
        return userAvatarURL;
    }

    public void setUserAvatarURL(String userAvatarURL) {
        this.userAvatarURL = userAvatarURL;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getTopicTitle() {
        return topicTitle;
    }

    public void setTopicTitle(String topicTitle) {
        this.topicTitle = topicTitle;
    }

    public int getViewsCount() {
        return viewsCount;
    }

    public int getRepliesCount() {
        return repliesCount;
    }

    public String getLastPostedAt() {
        return lastPostedAt;
    }

    public String getSlug() {
        return slug;
    }

    public int getTopicID() {
        return topicID;
    }

    public Bitmap getUserAvatar() {
        return userAvatar;
    }

    public String getUserLastPoster() {
        return userLastPoster;
    }

    public Bitmap getUserLastPosterAvatar() {
        return userLastPosterAvatar;
    }

    public String getUserLastPosterAvatarURL() {
        return userLastPosterAvatarURL;
    }

    public String getTopicImageURL() {
        return topicImageURL;
    }

    public Bitmap getTopicImageBitmap() {
        return topicImageBitmap;
    }

    public Categories getCategory() {
        return category;
    }
}
