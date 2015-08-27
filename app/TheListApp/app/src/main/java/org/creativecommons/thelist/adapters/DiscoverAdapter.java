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
import android.util.Log;
import android.view.LayoutInflater;
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

public class DiscoverAdapter extends RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder> {

    private Activity mActivity;

    private LayoutInflater inflater;
    private ArrayList<Photo> photoItems;

    private RecyclerViewUtils.cardSelectionListener cardListener;

    public DiscoverAdapter(Activity activity, ArrayList<Photo> photoItems, RecyclerViewUtils.cardSelectionListener listener){
        this.photoItems = photoItems;
        this.mActivity = activity;
        this.cardListener = listener;

        inflater = LayoutInflater.from(activity);
    }

    public void updateList(ArrayList<Photo> data) {
        this.photoItems = data;
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

    public class DiscoverViewHolder extends RecyclerView.ViewHolder {

        //card header
        de.hdodenhof.circleimageview.CircleImageView profilePic;
        TextView title;
        TextView byline;

        //card body
        ImageView photoPreview;
        TextView description;
        TextView requestedBy;

        //card footer
        ImageView likeButton;
        ImageView bookmarkButton;
        Button contributeButton;


        public DiscoverViewHolder(View itemView) {
            super(itemView);

            //card header
            profilePic = (de.hdodenhof.circleimageview.CircleImageView)
                    itemView.findViewById(R.id.discover_card_profile_pic);
            title = (TextView)itemView.findViewById(R.id.discover_card_title);
            byline = (TextView)itemView.findViewById(R.id.discover_card_byline);

            //card body
            photoPreview = (ImageView)itemView.findViewById(R.id.discover_card_photo);
            description = (TextView)itemView.findViewById(R.id.discover_card_description);
            requestedBy = (TextView)itemView.findViewById(R.id.discover_card_requestedby);

            //card footer
            likeButton = (ImageView)itemView.findViewById(R.id.likeButton);
            bookmarkButton = (ImageView)itemView.findViewById(R.id.bookmarkButton);
            contributeButton = (Button)itemView.findViewById(R.id.contributeButton);

            //TODO: set on click listeners for each clickable item on the card
            contributeButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Photo selectedItem = photoItems.get(getAdapterPosition());
                    Log.v("HELLO", selectedItem.title);

                    cardListener.onContribute(selectedItem.id);
                }
            });
        }

    } //DiscoverViewHolder

} //FeedAdapter
