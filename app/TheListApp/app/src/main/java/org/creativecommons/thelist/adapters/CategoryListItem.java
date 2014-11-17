package org.creativecommons.thelist.adapters;

/**
 * Created by damaris on 2014-11-14.
 */
public class CategoryListItem {
    private String categoryName;
    private int categoryID;

    public CategoryListItem() {
    }

    public CategoryListItem(String name, int id) {
        this.categoryName = name;
        this.categoryID = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String name) {
        this.categoryName = name;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int id) {
        this.categoryID = id;
    }
}
