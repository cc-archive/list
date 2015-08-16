package org.creativecommons.thelist.models;

import android.os.Parcel;
import android.os.Parcelable;

public class Photo implements Parcelable {
    public String id;
    public String title;
    public String username;
    public String itemid;
    public String category;
    public int likes;
    public String makerid;
    public String makername;
    public String description;
    public String url;
    public String approved;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(this.id);
        parcel.writeString(this.title);
        parcel.writeString(this.username);
        parcel.writeString(this.itemid);
        parcel.writeString(this.category);
        parcel.writeInt(this.likes);
        parcel.writeString(this.makerid);
        parcel.writeString(this.makername);
        parcel.writeString(this.description);
        parcel.writeString(this.url);
        parcel.writeString(this.approved);
    }

    public Photo(){
    }

    protected Photo(Parcel in) {
        this.id = in.readString();
        this.title = in.readString();
        this.username = in.readString();
        this.itemid = in.readString();
        this.category = in.readString();
        this.likes = in.readInt();
        this.makerid = in.readString();
        this.makername = in.readString();
        this.description = in.readString();
        this.url = in.readString();
        this.approved = in.readString();
    }

    public static final Parcelable.Creator<Photo> CREATOR = new Parcelable.Creator<Photo>() {
        public Photo createFromParcel(Parcel in) {
            return new Photo(in);
        }

        public Photo[] newArray(int size) {
            return new Photo[size];
        }
    };

} //Item
