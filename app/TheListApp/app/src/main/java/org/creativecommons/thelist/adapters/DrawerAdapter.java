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
    private LayoutInflater inflater;
    private List<DrawerItem> drawerItems; //Collections.emptyList()
    private static final int DEFAULT_VIEW = 1;
    public static final int ERROR_VIEW = 0;
    public static final int PROGRESS_VIEW = 2;

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
            case 0:
                holder.itemIcon.setImageResource(R.drawable.abc_btn_check_material);
                break;
            case 1:
                holder.itemIcon.setImageResource(R.drawable.ic_camera_alt_grey600_24dp);
                break;
            case 2:
                holder.itemIcon.setImageResource(R.drawable.ic_camera_alt_grey600_24dp);
                break;
            case 3:
                holder.itemIcon.setImageResource(R.drawable.ic_camera_alt_grey600_24dp);
                break;
            case 4:
                holder.itemIcon.setImageResource(R.drawable.ic_check_white_24dp);
                break;
            case 5:
                holder.itemIcon.setImageResource(R.drawable.ic_check_black_36dp);
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
