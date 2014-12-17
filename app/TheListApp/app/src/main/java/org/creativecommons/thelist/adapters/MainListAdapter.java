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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.creativecommons.thelist.R;

import java.util.List;

public class MainListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<MainListItem> listItems;

    public MainListAdapter(Activity activity, List<MainListItem> listItems) {
        this.activity = activity;
        this.listItems = listItems;
    }

    @Override
    public int getCount() {
        return listItems.size();
    }

    @Override
    public Object getItem(int location) {
        return listItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (inflater == null) {
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_main, parent, false);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView)convertView.findViewById(R.id.camera_icon);
            holder.nameLabel = (TextView)convertView.findViewById(R.id.list_item_name);
            holder.makerLabel = (TextView)convertView.findViewById(R.id.list_item_maker);

            convertView.setTag(holder);
        } else {
            holder = (ViewHolder)convertView.getTag();
        }

        //Getting Data for the row
        MainListItem l = listItems.get(position);
        //Item Name
        holder.nameLabel.setText(l.getItemName());
        //Maker Name
        holder.makerLabel.setText("requested by " + l.getMakerName());
        //Camera Icon
        holder.iconImageView.setImageResource(R.drawable.ic_camera_alt_black_36dp);


        //holder.removeAllViews();
        return convertView;


    }

    private static class ViewHolder {

        ImageView iconImageView;
        TextView nameLabel;
        TextView makerLabel;
    }

}
