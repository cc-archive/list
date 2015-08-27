package org.creativecommons.thelist.models;


import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Photos implements Parcelable {
    public ArrayList<Photo> photos;
    public int nextPage;

    public Photos(){

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeList(photos);
        parcel.writeInt(nextPage);
    }

    protected Photos(Parcel in) {
        this.photos = in.createTypedArrayList(Photo.CREATOR);
        this.nextPage = (Integer) in.readValue(Integer.class.getClassLoader());
    }

    public static final Creator<Photos> CREATOR = new Creator<Photos>() {
        public Photos createFromParcel(Parcel source) {
            return new Photos(source);
        }

        public Photos[] newArray(int size) {
            return new Photos[size];
        }
    };

} //Photo
