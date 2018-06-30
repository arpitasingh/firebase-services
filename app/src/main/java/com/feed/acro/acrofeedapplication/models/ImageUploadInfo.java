package com.feed.acro.acrofeedapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ImageUploadInfo implements Parcelable {

    public String description;

    public String image_url;

    public String year;
    public String name;
    public String postingTo;
    public String status;
    public String userId;

    public ImageUploadInfo() {

    }

    public ImageUploadInfo(String userId, String description, String image_url, String year, String name, String postingTo,
                           String status) {
        this.description = description;
        this.image_url = image_url;
        this.year = year;
        this.name = name;
        this.postingTo = postingTo;
        this.status = status;
        this.userId = userId;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("userId", userId);
        result.put("name", name);
        result.put("image_url", image_url);
        result.put("year", year);
        result.put("status", status);
        result.put("postingTo", postingTo);
        result.put("description", description);


        return result;
    }

    protected ImageUploadInfo(Parcel in) {
        description = in.readString();
        image_url = in.readString();
        year = in.readString();
        name = in.readString();
        postingTo = in.readString();
        status = in.readString();
        userId = in.readString();
    }

    public static final Creator<ImageUploadInfo> CREATOR = new Creator<ImageUploadInfo>() {
        @Override
        public ImageUploadInfo createFromParcel(Parcel in) {
            return new ImageUploadInfo(in);
        }

        @Override
        public ImageUploadInfo[] newArray(int size) {
            return new ImageUploadInfo[size];
        }
    };

    public String getImageURL() {
        return image_url;
    }


    public void setImageURL(String imageURL) {
        this.image_url = imageURL;
    }

    public String getYearText() {
        return year;
    }

    public void setYearText(String yearText) {
        this.year = yearText;
    }

    public String getNameText() {
        return name;
    }

    public void setNameText(String nameText) {
        this.name = nameText;
    }

    public String getPostTo() {
        return postingTo;
    }

    public void setPostTo(String postTo) {
        this.postingTo = postTo;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(description);
        parcel.writeString(image_url);
        parcel.writeString(year);
        parcel.writeString(name);
        parcel.writeString(postingTo);
        parcel.writeString(status);
        parcel.writeString(userId);
    }
}