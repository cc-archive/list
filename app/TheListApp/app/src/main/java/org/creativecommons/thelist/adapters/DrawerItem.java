package org.creativecommons.thelist.adapters;

public class DrawerItem {
    private String itemName;
    private int icon;

    public DrawerItem() {
    }

    public DrawerItem(String name, int icon) {
        this.itemName = name;
        this.icon = icon;
    }

    public String getItemName() {
        return itemName;
    }

    public void setItemName(String name) {
        this.itemName = name;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }


} //DrawerItem
