package org.creativecommons.thelist.TabbedActivity;

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

/**
 * Created by damaris on 2014-11-12.
 */
public class ListFeedAdapter extends ArrayAdapter<JSONArray> {

    protected Context mContext;
    protected List<JSONArray> mListItems;

    public ListFeedAdapter(Context context, List<JSONArray> listItems) {
        super(context, R.layout.main_list_item, listItems);
        mContext = context;
        mListItems = listItems;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if(convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.main_list_item, null);
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
