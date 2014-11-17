package org.creativecommons.thelist.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (inflater == null) {
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.category_list_item, null);
            holder = new ViewHolder();
            holder.categoryNameLabel = (TextView)convertView.findViewById(R.id.category);

            //getting Data for the row
            CategoryListItem c = categoryListItems.get(position);

            //Item Name
            holder.categoryNameLabel.setText(c.getCategoryName());
            convertView.setTag(c.getCategoryID());
        }
        return convertView;
    }

    private static class ViewHolder {
        TextView categoryNameLabel;
    }
}