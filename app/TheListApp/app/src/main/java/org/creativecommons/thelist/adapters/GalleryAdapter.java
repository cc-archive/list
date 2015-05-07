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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.utils.RequestMethods;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    public static final String TAG = GalleryAdapter.class.getSimpleName();

    private Context mContext;

    private RequestMethods mRequestMethods;
    private LayoutInflater inflater;
    private List<GalleryItem> galleryItems;
//    private static final int DEFAULT_VIEW = 1;
//    public static final int ERROR_VIEW = 0;
//    public static final int PROGRESS_VIEW = 2;

    public GalleryAdapter(Context context, List<GalleryItem> galleryItems){
        mContext = context;
        mRequestMethods = new RequestMethods(mContext);

        this.galleryItems = galleryItems;

        //For debugging
        //Picasso.with(mContext).setIndicatorsEnabled(true);

        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return galleryItems.size();
    }

    @Override
    public GalleryViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = inflater.inflate(R.layout.list_item_gallery, viewGroup, false);
        GalleryViewHolder holder = new GalleryViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(GalleryViewHolder holder, int position) {
        //Getting data for the row
        GalleryItem g = galleryItems.get(position);

        Picasso.with(mContext)
                .load(g.getUrl() + "/300")
                .placeholder(R.drawable.progress_view)
                .error(R.drawable.progress_view)
                .into(holder.photoView);

        holder.itemView.setTag(g);
    }

    public class GalleryViewHolder extends RecyclerView.ViewHolder {
        ImageView photoView;

        public GalleryViewHolder(View itemView) {
            super(itemView);
            photoView = (ImageView)itemView.findViewById(R.id.gallery_image);
        }
    }

}

