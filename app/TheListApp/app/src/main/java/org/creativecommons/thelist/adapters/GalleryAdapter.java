package org.creativecommons.thelist.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.creativecommons.thelist.R;

import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<GalleryAdapter.GalleryViewHolder> {
    public static final String TAG = GalleryAdapter.class.getSimpleName();

    private Context mContext;
    private LayoutInflater inflater;
    private List<GalleryItem> galleryItems;
    private static final int DEFAULT_VIEW = 1;
    public static final int ERROR_VIEW = 0;
    public static final int PROGRESS_VIEW = 2;

    public GalleryAdapter(Context context, List<GalleryItem> galleryItems){
        mContext = context;
        this.galleryItems = galleryItems;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getItemCount() {
        return galleryItems.size();
    }

    @Override
    public int getItemViewType(int position){
        GalleryItem g = galleryItems.get(position);

        if(g.getError()){
            return ERROR_VIEW;
        } else {
            return DEFAULT_VIEW;
        }
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

        //Set different views
        switch(getItemViewType(position)){
            case ERROR_VIEW:
                //TODO: set error view
                holder.photoView.setImageResource(R.drawable.ic_error_red_24dp);
                break;
            case DEFAULT_VIEW:
                Picasso.with(mContext)
                        .load(g.getUrl())
                        .placeholder(R.drawable.ic_camera_alt_grey600_24dp)
                        .error(R.drawable.ic_error_red_24dp)
                        .into(holder.photoView);
                break;
//            case PROGRESS_VIEW:
//                //TODO: add progress drawable
//                holder.iconImageView.setImageResource(R.drawable.ic_camera_alt_black_36dp);
        }
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

