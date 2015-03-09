/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons

   This program is free software: you can redistribute it and/or modify
   it under the terms of the GNU Affero General Public License as published by
   the Free Software Foundation, either version 3 of the License, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
   GNU Affero General Public License for more details.

   You should have received a copy of the GNU Affero General Public License
   along with this program.  If not, see <http://www.gnu.org/licenses/>.

*/

package org.creativecommons.thelist.adapters;

public class CategoryListItem {
    private String categoryName;
    private int categoryID;
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

    public void setCategoryChecked(boolean bol){
        this.categoryChecked = bol;
    }
}
