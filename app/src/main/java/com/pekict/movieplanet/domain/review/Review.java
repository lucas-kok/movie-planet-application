package com.pekict.movieplanet.domain.review;

import android.os.Parcel;
import android.os.Parcelable;

public class Review implements Parcelable {
    private String avatar_path;
    private String author;
    private String content;
    private String created_at;

    public Review(String avatar_path, String author, String content, String created_at) {
        this.avatar_path = avatar_path;
        this.author = author;
        this.content = content;
        this.created_at = created_at;
    }

    protected Review(Parcel in) {
        avatar_path = in.readString();
        author = in.readString();
        content = in.readString();
        created_at = in.readString();
    }

    public static final Creator<Review> CREATOR = new Creator<Review>() {
        @Override
        public Review createFromParcel(Parcel in) {
            return new Review(in);
        }

        @Override
        public Review[] newArray(int size) {
            return new Review[size];
        }
    };

    public String getAvatar_path;

    public String getAuthor() {
        return author;
    }

    public String getContent() {
        return content;
    }

    public String getCreated_at() {
        return created_at.substring(0, 10);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(avatar_path);
        parcel.writeString(author);
        parcel.writeString(content);
        parcel.writeString(created_at);
    }
}
