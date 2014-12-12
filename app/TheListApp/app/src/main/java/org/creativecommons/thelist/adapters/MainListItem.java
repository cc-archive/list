package org.creativecommons.thelist.adapters;

/**
 * Created by damaris on 2014-11-12.
 */
public class MainListItem {
    private String itemName, makerName;
    private String itemID;

    public MainListItem() {
    }

    public MainListItem(String id, String name, String maker) {
        this.itemID = itemID;
        this.itemName = name;
        this.makerName = maker;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemID(String id) {
        this.itemID = id;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String name) {
        this.itemName = name;
    }

    public String getMakerName() {
        return makerName;
    }

    public void setMakerName(String maker) {
        this.makerName = maker;
    }
}
