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


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.creativecommons.thelist.R;

import java.util.List;

public class DrawerAdapter extends RecyclerView.Adapter<DrawerAdapter.DrawerViewHolder>  {
    public static final String TAG = DrawerAdapter.class.getSimpleName();

    private LayoutInflater inflater;
    private List<DrawerItem> drawerItems; //Collections.emptyList()

    public DrawerAdapter(Context context, List<DrawerItem> listItems) {
        this.drawerItems = listItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return drawerItems.size();
    }

    @Override
    public int getItemViewType(int position){
        //DrawerItem l = drawerItems.get(position);
        return 0;
    }

    @Override
    public DrawerViewHolder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.list_item_drawer, viewGroup, false);
        DrawerViewHolder holder = new DrawerViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder (DrawerViewHolder holder, int position) {
        //Getting Data for the row
        DrawerItem d = drawerItems.get(position);

        holder.itemTitle.setText(d.getItemName());

        switch(position){
            case 0: //The List
                holder.itemIcon.setImageResource(R.drawable.ic_view_list_grey600_24dp);
                break;
            case 1: //My Photos
                holder.itemIcon.setImageResource(R.drawable.ic_photo_grey600_24dp);
                break;
            case 2: //My Categories
                holder.itemIcon.setImageResource(R.drawable.ic_bookmark_grey600_24dp);
                break;
            case 3: //Request an Item
                holder.itemIcon.setImageResource(R.drawable.ic_add_to_photos_grey600_24dp);
                break;
            case 4: //About the App
                holder.itemIcon.setImageResource(R.drawable.ic_info_grey600_24dp);
                break;
            case 5: //Give Feedback
                holder.itemIcon.setImageResource(R.drawable.ic_question_answer_grey600_24dp);
                break;
            default:
                break;
        }

        holder.itemView.setTag(d);
    }

    public class DrawerViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        ImageView itemIcon;

        public DrawerViewHolder(View itemView) {
            super(itemView);
            itemIcon = (ImageView)itemView.findViewById(R.id.drawer_item_icon);
            itemTitle = (TextView)itemView.findViewById(R.id.drawer_item_title);
        }
    }

//    private LayoutInflater inflater;
//    private List<DrawerItem> drawerItems;
//
//    private static final int TYPE_HEADER = 0;
//    private static final int TYPE_ITEM = 1;
//
//    public DrawerAdapter(Context context, List<DrawerItem> data){
//        inflater = LayoutInflater.from(context);
//        this.drawerItems = data;
//    }
//
//
//    @Override
//    public DrawerItemHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
//        View view = inflater.inflate(R.layout.list_item_drawer, viewGroup, false);
//        DrawerItemHolder holder = new DrawerItemHolder(view);
//        return holder;
//    }
//
//    @Override
//    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
//        DrawerItem d = drawerItems.get(position);
//
//
//    }
//
//    @Override
//    public int getItemViewType(int position) {
////        if(position == 0){
////            return TYPE_HEADER;
////        }
////        else {
////            return TYPE_ITEM;
////        }
//        return 0;
//    }
//
//    @Override
//    public int getItemCount() {
//        return drawerItems.size();
//    }
//
//
//    public class DrawerItemHolder extends RecyclerView.ViewHolder {
//        TextView itemTitle;
//        ImageView itemIcon;
//
//        public DrawerItemHolder(View itemView) {
//            super(itemView);
//            itemTitle = (TextView) itemView.findViewById(R.id.drawer_item_title);
//            itemIcon = (ImageView) itemView.findViewById(R.id.drawer_item_icon);
//        }
//    }

} //DrawerAdapter
