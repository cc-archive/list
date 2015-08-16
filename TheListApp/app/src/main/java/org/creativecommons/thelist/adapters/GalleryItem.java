/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons Corporation

   This program is free software: you can redistribute it and/or modify
   it under the terms of either the GNU Affero General Public License or
   the GNU General Public License as published by the
   Free Software Foundation, either version 3 of the Licenses, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.

   If not, see <http://www.gnu.org/licenses/>.
*/

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
