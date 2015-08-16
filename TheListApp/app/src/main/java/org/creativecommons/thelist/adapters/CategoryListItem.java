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

public class CategoryListItem {

    private String categoryName;
    private int categoryID;
    private String categoryColour;
    private boolean categoryChecked;

    public CategoryListItem() {
    }

    public CategoryListItem(String title, int id, boolean bol) {
        this.categoryName = title;
        this.categoryID = id;
        this.categoryChecked = bol;
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

    public boolean getCategoryChecked(){
        return categoryChecked;

    }

    public String getCategoryColour(){
        return categoryColour;
    }

    public void setCategoryColour(String hexValue){
        this.categoryColour = hexValue;
    }

    public void setCategoryChecked(boolean bol){
        this.categoryChecked = bol;
    }
}
