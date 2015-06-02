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

import android.app.Activity;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.layouts.CheckableRelativeLayout;
import org.creativecommons.thelist.utils.ListUser;
import org.creativecommons.thelist.utils.RequestMethods;

import java.util.ArrayList;
import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    public static final String TAG = CategoryAdapter.class.getSimpleName();

    private Activity mActivity;
    private RequestMethods mRequestMethods;

    private LayoutInflater inflater;
    private List<CategoryListItem> categoryItems;

    public SparseBooleanArray selectedItems = new SparseBooleanArray();

    public CategoryAdapter(Activity activity, List<CategoryListItem> categoryItems) {
        this.mActivity = activity;
        this.mRequestMethods = new RequestMethods(activity);

        this.categoryItems = categoryItems;
        inflater = LayoutInflater.from(activity);
    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    public void toggleSelection(int pos) {

        boolean value = selectedItems.get(pos);
        Log.v(TAG, "TOGGLESELECTION > INITIAL VALUE " + value);

        if(selectedItems.get(pos)){
            selectedItems.put(pos, false);
            Log.v(TAG, "ITEM WAS TRUE: MADE FALSE");
        } else {
            selectedItems.put(pos, true);
            Log.v(TAG, "ITEM WAS FALSE: MADE TRUE");
        }

    } //toggleSelection

    public void setState(int pos, boolean bol){
        selectedItems.put(pos, bol);
    }


    public boolean getToggleState(int pos){
        return selectedItems != null && selectedItems.get(pos);
    }

    public int getSelectedItemCount() {
        return selectedItems.size();
    }

    public List<Integer> getSelectedItems() {

        List<Integer> items = new ArrayList<Integer>();
        int length = selectedItems.size();

        for (int i = 0; i < length; i++) {
            boolean value = selectedItems.get(i);

            if(value) {
                items.add(selectedItems.keyAt(i));
            }
        }

//        for (int i = 0; i < selectedItems.size(); i++) {
//            items.add(selectedItems.keyAt(i));
//        }

        Log.v(TAG, items.toString());
        return items;
    }

    @Override
    public CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.list_item_category, viewGroup, false);
        CategoryViewHolder holder = new CategoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(final CategoryViewHolder holder, final int position) {

        final ListUser listUser = new ListUser(mActivity);

        final CategoryListItem c = categoryItems.get(position);
        final String catId = String.valueOf(c.getCategoryID());

        holder.categoryNameLabel.setText(c.getCategoryName().toUpperCase());

        //Item Background Color
        if(c.getCategoryColour() != null){
            holder.checkableLayout.setBackgroundColor((Color.parseColor(c.getCategoryColour())));
        } else {
            holder.checkableLayout.setBackgroundColor(mActivity.getResources().getColor(R.color.category_default));
        }

        if(getToggleState(position)){
            holder.checkableLayout.getBackground().setAlpha(200);
            holder.categoryNameLabel.setTextColor(mActivity.getResources().getColor(R.color.secondary_text_material_dark));
            holder.categoryCheckIcon.setVisibility(View.VISIBLE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                toggleSelection(position);

                Log.v(TAG, c.getCategoryName() + " POS: " + position + "id: " + catId);

                if(getToggleState(position)){
                    holder.checkableLayout.getBackground().setAlpha(200);
                    holder.categoryNameLabel.setTextColor(mActivity.getResources().getColor(R.color.secondary_text_material_dark));
                    holder.categoryCheckIcon.setVisibility(View.VISIBLE);

                    mRequestMethods.addCategory(catId);

                } else {
                    holder.checkableLayout.getBackground().setAlpha(255);
                    holder.categoryNameLabel.setTextColor(mActivity.getResources().getColor(R.color.primary_text_default_material_dark));
                    holder.categoryCheckIcon.setVisibility(View.INVISIBLE);

                    mRequestMethods.removeCategory(catId);
                }
            }
        });

        holder.itemView.setTag(c);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        CheckableRelativeLayout checkableLayout;
        TextView categoryNameLabel;
        ImageView categoryCheckIcon;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            checkableLayout = (CheckableRelativeLayout)itemView.findViewById(R.id.checkable_layout);
            categoryNameLabel = (TextView)itemView.findViewById(R.id.category_title);
            categoryCheckIcon = (ImageView)itemView.findViewById(R.id.category_checkmark);

        }
    }
}
