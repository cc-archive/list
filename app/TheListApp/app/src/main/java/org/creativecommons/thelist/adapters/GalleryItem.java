package org.creativecommons.thelist.adapters;

import android.os.Parcel;
import android.os.Parcelable;

public class GalleryItem implements Parcelable {
    private String itemName;
    private String makerName;
    private String itemID;
    private String url;
    private boolean error;
    private boolean progress;

    public GalleryItem() {
    }

    public GalleryItem(String id, String itemName, String maker, String url) {
        this.itemID = id;
        this.itemName = itemName;
        this.url = url;
        this.makerName = maker;
        this.error =  false;
        this.progress = false;
    }

    public void setItemName(String itemName) {
        this.itemName = itemName;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemID(String itemID) {
        this.itemID = itemID;
    }

    public String getItemID() {
        return itemID;
    }

    public void setMakerName(String makerName){
        this.makerName = makerName;
    }

    public String getMakerName(){
        return makerName;
    }

    public void setUrl(String uri) {
        this.url = uri;
    }

    public String getUrl(){
        return url;
    }

    public void setError(boolean error){
        this.error = error;
    }

    public boolean getError(){
        return error;
    }

    public void setProgress(boolean progress){
        this.progress = progress;
    }

    public boolean getProgress(){
        return progress;
    }

    protected GalleryItem(Parcel in) {
        itemName = in.readString();
        makerName = in.readString();
        itemID = in.readString();
        url = in.readString();
        error = in.readByte() != 0x00;
        progress = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(itemName);
        dest.writeString(makerName);
        dest.writeString(itemID);
        dest.writeString(url);
        dest.writeByte((byte) (error ? 0x01 : 0x00));
        dest.writeByte((byte) (progress ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<GalleryItem> CREATOR = new Parcelable.Creator<GalleryItem>() {
        @Override
        public GalleryItem createFromParcel(Parcel in) {
            return new GalleryItem(in);
        }

        @Override
        public GalleryItem[] newArray(int size) {
            return new GalleryItem[size];
        }
    };
}
