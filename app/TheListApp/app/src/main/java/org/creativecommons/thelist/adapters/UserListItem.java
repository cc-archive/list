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

import android.content.Context;

import com.google.gson.annotations.Expose;

public class UserListItem {
    @Expose private String itemName, makerName, itemID, categoryID;

    @Expose private boolean error, progress;

    private Context mContext;

    @Expose public boolean completed = false;

    public UserListItem() {
    }

    public UserListItem(String id, String name, String maker) {
        this.itemID = id;
        this.itemName = name;
        this.makerName = maker;
        this.error =  false;
        this.progress = false;
    }

    public void setItemID(String id) {
        this.itemID = id;
    }

    public String getItemID() {
        return itemID;
    }

    public void setItemName(String name) {
        this.itemName = name;
    }

    public String getItemName() {
        return itemName;
    }

    public void setMakerName(String maker) {
        this.makerName = maker;
    }

    public String getMakerName() {
        return makerName;
    }

    public void setError(boolean bol){
        this.error = bol;
    }

    public boolean getError(){
        return error;
    }

    public void setProgress(boolean bol){
        this.progress = bol;
    }

    public boolean getProgress(){
        return progress;
    }

    public void setCategoryID(String id){
        categoryID = id;
    }

    public String getCategoryID(){
        return categoryID;

    }

    public void setContext(Context c) {
        mContext = c;
    }

} //UserListItem
