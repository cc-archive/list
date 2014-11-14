package org.creativecommons.thelist.adapters;

/**
 * Created by damaris on 2014-11-12.
 */
public class MainListItem {
    private String itemName, makerName;

    public MainListItem() {
    }

    public MainListItem(String name, String maker) {
        this.itemName = name;
        this.makerName = maker;
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
