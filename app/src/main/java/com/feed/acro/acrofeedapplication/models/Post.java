package com.feed.acro.acrofeedapplication.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;
import com.google.firebase.database.IgnoreExtraProperties;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@IgnoreExtraProperties
public class Post implements Parcelable{

    public String uid;
    public ImageUploadInfo imageUploadInfo = new ImageUploadInfo();

    public Post() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class)
    }

    public Post(String uid,ImageUploadInfo imageUploadInfo) {
        this.uid = uid;
        this.imageUploadInfo = imageUploadInfo;
    }

 /*   @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("uid", uid);
        result.put("author", author);
        result.put("year", year);
        result.put("body", body);
        result.put("postingTo", postingTo);
        result.put("imagePath", imagePath);


        return result;
    }*/


    protected Post(Parcel in) {
        uid = in.readString();
        imageUploadInfo = in.readParcelable(ImageUploadInfo.class.getClassLoader());
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
        }
    };

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public ImageUploadInfo getImageUploadInfo() {
        return imageUploadInfo;
    }

    public void setImageUploadInfo(ImageUploadInfo imageUploadInfo) {
        this.imageUploadInfo = imageUploadInfo;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(uid);
        parcel.writeParcelable(imageUploadInfo, i);
    }
}