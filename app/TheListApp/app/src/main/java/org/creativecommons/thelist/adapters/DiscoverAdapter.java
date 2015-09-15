/* The List powered by Creative Commons

   Copyright (C) 2014, 2015 Creative Commons Corporation

   This program is free software: you can redistribute it and/or modify
   it under the terms of either the GNU Affero General Public License or
   the GNU General Public License as published by the
   Free Software Foundation, either version 3 of the Licenses, or
   (at your option) any later version.

   This program is distributed in the hope that it will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.

   You should have received a copy of the GNU General Public License and
   the GNU Affero General Public License along with this program.

   If not, see <http://www.gnu.org/licenses/>.

*/

package org.creativecommons.thelist.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.models.Photo;
import org.creativecommons.thelist.utils.MessageHelper;
import org.creativecommons.thelist.utils.RecyclerViewUtils;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder> {

    private Activity mActivity;

    private LayoutInflater inflater;
    private List<Photo> photoItems = new ArrayList<>();

    private RecyclerViewUtils.cardSelectionListener cardListener;

    public DiscoverAdapter(Activity activity, List<Photo> photoItems, RecyclerViewUtils.cardSelectionListener listener){
        this.photoItems = photoItems;
        this.mActivity = activity;
        this.cardListener = listener;

        inflater = LayoutInflater.from(activity);
    }

    public void updateList(List<Photo> data) {
        photoItems.addAll(data);
        this.notifyDataSetChanged();
    }

    @Override
    public DiscoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.list_item_discover_card, parent, false);

        DiscoverViewHolder holder = new DiscoverViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DiscoverViewHolder holder, int position) {

        Photo photoItem = photoItems.get(position);

        //Card Header
        //TODO: set profile pic
        holder.title.setText(MessageHelper.capitalize(photoItem.title));
        holder.byline.setText(photoItem.username);

        //Card Body
        Picasso.with(mActivity)
                .load(photoItem.url)
                .placeholder(R.drawable.progress_view) //TODO: replace with loading image
                .fit()
                .centerCrop()
                .error(R.drawable.progress_view) //TODO: replace with loading image
                .into(holder.photoPreview);

        holder.description.setText(photoItem.description);
        holder.requestedBy.setText("requested by " + photoItem.makername);

    }

    @Override
    public int getItemCount() {
        return photoItems.size();
    }

    public void deleteItem(int position){
        photoItems.remove(position);
        notifyDataSetChanged();
    }

    public class DiscoverViewHolder extends RecyclerView.ViewHolder {

        //card header
        @Bind(R.id.discover_toolbar)android.support.v7.widget.Toolbar toolbar;
        @Bind(R.id.discover_card_profile_pic)de.hdodenhof.circleimageview.CircleImageView profilePic;
        @Bind(R.id.discover_card_title)TextView title;
        @Bind(R.id.discover_card_byline)TextView byline;

        //card body
        @Bind(R.id.discover_card_photo)ImageView photoPreview;
        @Bind(R.id.discover_card_description)TextView description;
        @Bind(R.id.discover_card_requestedby)TextView requestedBy;

        //card footer
        @Bind(R.id.likeButton)ImageView likeButton;
        @Bind(R.id.bookmarkButton) ImageView bookmarkButton;
        @Bind(R.id.contributeButton)Button contributeButton;

        public String getSelectedItemId(){
            return photoItems.get(getAdapterPosition()).id;
        }

        public DiscoverViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);

            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem menuItem) {

                    switch (menuItem.getItemId()) {
                        case R.id.action_flag:
                            cardListener.onFlag(getSelectedItemId(), getAdapterPosition());
                            return true;
                    }

                    return true;
                }
            });

            toolbar.inflateMenu(R.menu.menu_discover_card);

            //TODO: set on click listeners for each clickable item on the card
            contributeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    cardListener.onContribute(getSelectedItemId());
                }
            });
        }

    } //DiscoverViewHolder

} //FeedAdapter
