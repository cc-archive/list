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

package org.creativecommons.thelist.misc;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.adapters.CategoryListItem;
import org.creativecommons.thelist.layouts.CheckableRelativeLayout;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private LayoutInflater inflater;
    private List<CategoryListItem> categoryItems;

    public CategoryAdapter(Context context, List<CategoryListItem> categoryItems) {
        this.categoryItems = categoryItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return categoryItems.size();
    }

    @Override
    public CategoryAdapter.CategoryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.list_item_category, viewGroup, false);
        CategoryViewHolder holder = new CategoryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(CategoryViewHolder holder, int position) {
        CategoryListItem c = categoryItems.get(position);

        holder.categoryNameLabel.setText(c.getCategoryName());
        holder.checkableLayout.setBackgroundColor(Color.parseColor(c.getCategoryColour()));

        if(c.getCategoryChecked()){
            Log.v("CATEGORY ADAPTER", "CHECK MARK VIEWS ARE BEING SET TO VISIBLE");
            holder.checkmarkView.setVisibility(View.VISIBLE);
            holder.checkableLayout.setChecked(true);
        } else {
            holder.checkmarkView.setVisibility(View.GONE);
            holder.checkableLayout.setChecked(false);
        }

        holder.itemView.setTag(c);
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        CheckableRelativeLayout checkableLayout;
        TextView categoryNameLabel;
        ImageView checkmarkView;

        public CategoryViewHolder(View itemView) {
            super(itemView);
            checkableLayout = (CheckableRelativeLayout)itemView.findViewById(R.id.checkable_layout);
            categoryNameLabel = (TextView)itemView.findViewById(R.id.category);
            //checkmarkView = (ImageView)itemView.findViewById(R.id.checkmark);
        }
    }
}
