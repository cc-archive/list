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

package org.creativecommons.thelist.misc;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import org.creativecommons.thelist.R;
import org.json.JSONArray;

import java.util.List;

public class ListFeedAdapter extends ArrayAdapter<JSONArray> {

    protected Context mContext;
    protected List<JSONArray> mListItems;

    public ListFeedAdapter(Context context, List<JSONArray> listItems) {
        super(context, R.layout.list_item_main, listItems);
        mContext = context;
        mListItems = listItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.list_item_main, null);
            holder = new ViewHolder();
            holder.iconImageView = (ImageView)convertView.findViewById(R.id.camera_icon);
            holder.nameLabel = (TextView)convertView.findViewById(R.id.list_item_name);
            holder.makerLabel = (TextView)convertView.findViewById(R.id.list_item_maker);
        }
        else {
            holder = (ViewHolder)convertView.getTag();
        }

        JSONArray listItem = mListItems.get(position);
        holder.iconImageView.setImageResource(R.drawable.ic_camera_alt_black_36dp);
//        try {
//            holder.nameLabel.setText(listItem.getString(ApiConstants.ITEM_NAME));
//            holder.makerLabel.setText(listItem.getString(ApiConstants.MAKER_NAME));
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }

        return convertView;
    }

    private static class ViewHolder {
        ImageView iconImageView;
        TextView nameLabel;
        TextView makerLabel;
    }
}
