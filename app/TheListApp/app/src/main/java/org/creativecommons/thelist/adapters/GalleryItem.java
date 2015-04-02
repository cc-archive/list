package org.creativecommons.thelist.adapters;

public class GalleryItem {
    private String itemName, makerName, itemID, url;
    private boolean error, progress;

    public GalleryItem() {
    }

    public GalleryItem(String id, String itemName, String maker, String url) {
        this.itemID = itemID;
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

} //GalleryItem
