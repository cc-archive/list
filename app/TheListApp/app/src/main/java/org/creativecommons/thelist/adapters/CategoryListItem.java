package org.creativecommons.thelist.adapters;

/**
 * Created by damaris on 2014-11-14.
 */
public class CategoryListItem {
    private String categoryName;
    private int categoryID;

    public CategoryListItem() {
    }

    public CategoryListItem(String title, int id) {
        this.categoryName = title;
        this.categoryID = id;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String title) {
        this.categoryName = title;
    }

    public int getCategoryID() {
        return categoryID;
    }

    public void setCategoryID(int id) {
        this.categoryID = id;
    }
}
