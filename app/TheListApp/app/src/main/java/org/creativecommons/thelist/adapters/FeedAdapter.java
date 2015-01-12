/* The List powered by Creative Commons

   Copyright (C) 2014 Creative Commons

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

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.creativecommons.thelist.MainActivity;
import org.creativecommons.thelist.R;

import java.util.List;

public class FeedAdapter extends RecyclerView.Adapter<FeedAdapter.FeedViewholder> {
    private LayoutInflater inflater;
    private MainActivity mainActivity;
    private List<MainListItem> listItems; //Collections.emptyList()

    public FeedAdapter(Context context, List<MainListItem> listItems, Activity activity) {
        this.mainActivity = (MainActivity) activity;
        this.listItems = listItems;
        inflater = LayoutInflater.from(context);
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
        MainListItem l = listItems.get(position);

        holder.nameLabel.setText(l.getItemName());
        holder.makerLabel.setText("requested by " + l.getMakerName());
        holder.iconImageView.setImageResource(R.drawable.ic_camera_alt_black_36dp);

    }

    @Override
    public int getItemCount() {
        return listItems.size();
    }

    public class FeedViewholder extends RecyclerView.ViewHolder {
        RelativeLayout relativeLayout;
        ImageView iconImageView;
        TextView nameLabel;
        TextView makerLabel;

        public FeedViewholder(View itemView) {
            super(itemView);
            //itemView.setOnClickListener(this);
            iconImageView = (ImageView)itemView.findViewById(R.id.camera_icon);
            nameLabel = (TextView)itemView.findViewById(R.id.list_item_name);
            makerLabel = (TextView)itemView.findViewById(R.id.list_item_maker);

            //iconImageView.setOnClickListener(this);
        }
    }
}
