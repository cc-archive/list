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
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.creativecommons.thelist.R;

import java.util.List;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.FeedViewholder> {
    private LayoutInflater inflater;
    private List<UserListItem> listItems; //Collections.emptyList()
    private static final int DEFAULT_VIEW = 1;
    public static final int ERROR_VIEW = 0;
    public static final int PROGRESS_VIEW = 2;

    public UserListAdapter(Context context, List<UserListItem> listItems) {
        this.listItems = listItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    @Override
    public int getItemViewType(int position){
        UserListItem l = listItems.get(position);

        if(l.getError()){
            return ERROR_VIEW;
        } else {
            return DEFAULT_VIEW;
        }
    }

    @Override
    public FeedViewholder onCreateViewHolder(ViewGroup viewGroup, int position) {
        View view = inflater.inflate(R.layout.list_item_main, viewGroup, false);
        FeedViewholder holder = new FeedViewholder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FeedViewholder holder, int position) {
        //Getting Data for the row
        UserListItem l = listItems.get(position);
        holder.itemView.setVisibility(View.VISIBLE);
        holder.nameLabel.setText(l.getItemName());
        //Log.v("NAME LABEL", String.valueOf(l.getItemName()));
        holder.makerLabel.setText("requested by " + l.getMakerName());

        //Set unique view for different ViewTypes
        switch(getItemViewType(position)){
            case ERROR_VIEW:
                holder.iconImageView.setImageResource(R.drawable.ic_error_red_24dp);
                break;
            case DEFAULT_VIEW:
                holder.iconImageView.setImageResource(R.drawable.ic_camera_alt_grey600_24dp);
                break;
//            case PROGRESS_VIEW:
//                //TODO: add progress drawable
//                holder.iconImageView.setImageResource(R.drawable.ic_camera_alt_black_36dp);
        }
        holder.itemView.setTag(l);
    }

    public class FeedViewholder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        ImageView iconImageView;
        TextView nameLabel;
        TextView makerLabel;

        public FeedViewholder(View itemView) {
            super(itemView);
            iconImageView = (ImageView)itemView.findViewById(R.id.camera_icon);
            nameLabel = (TextView)itemView.findViewById(R.id.list_item_name);
            makerLabel = (TextView)itemView.findViewById(R.id.list_item_maker);
            relativeLayout = (RelativeLayout)itemView.findViewById(R.id.full_list_item);
        }
    }
}
