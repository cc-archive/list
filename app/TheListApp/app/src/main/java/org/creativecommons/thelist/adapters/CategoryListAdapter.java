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

/**
 * Created by damaris on 2014-11-14.
 */
public class CategoryListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<CategoryListItem> categoryListItems;

    public CategoryListAdapter(Activity activity, List<CategoryListItem> categoryItems) {
        this.activity = activity;
        this.categoryListItems = categoryItems;
    }

    @Override
    public int getCount() {
        return categoryListItems.size();
    }

    @Override
    public Object getItem(int location) {
        return categoryListItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (inflater == null) {
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item_category, parent, false);
            holder = new ViewHolder();
            holder.categoryNameLabel = (TextView)convertView.findViewById(R.id.category);
            holder.checkmarkView = (ImageView)convertView.findViewById(R.id.checkmark);
            convertView.setTag(holder);

            //Getting Data for the row
            CategoryListItem c = categoryListItems.get(position);
            //Item Name
            holder.categoryNameLabel.setText(c.getCategoryName());

        } else {
            holder = (ViewHolder)convertView.getTag();
        }

//        If items have been previously checked in DB
//        ListView listview = (ListView)parent;
//        if(listview.isItemChecked(position)) {
//            holder.checkmarkView.setVisibility(View.VISIBLE);
//        } else {
//            holder.checkmarkView.setVisibility(View.INVISIBLE);
//        }

        return convertView;
    }

    private static class ViewHolder {
        TextView categoryNameLabel;
        ImageView checkmarkView;
    }
}