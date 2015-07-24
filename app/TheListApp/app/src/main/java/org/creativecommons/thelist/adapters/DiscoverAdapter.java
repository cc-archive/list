package org.creativecommons.thelist.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.models.Photo;
import org.creativecommons.thelist.utils.RecyclerViewUtils;

import java.util.List;

public class DiscoverAdapter extends  RecyclerView.Adapter<DiscoverAdapter.DiscoverViewHolder> {

    private Activity mActivity;

    private LayoutInflater inflater;
    private List<Photo> photoItems;

    private RecyclerViewUtils.cardSelectionListener cardListener;

    public DiscoverAdapter(Activity activity, List<Photo> photoItems, RecyclerViewUtils.cardSelectionListener listener){
        this.photoItems = photoItems;
        this.mActivity = activity;
        this.cardListener = listener;

        inflater = LayoutInflater.from(activity);
    }


    @Override
    public DiscoverViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.feed_item, parent, false);

        DiscoverViewHolder holder = new DiscoverViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(DiscoverViewHolder holder, int position) {

        Photo photoItem = photoItems.get(position);

        //Card Header
        //TODO: set profile pic
        holder.title.setText(photoItem.title);
        holder.byline.setText(photoItem.username);

        //Card Body
        Picasso.with(mActivity)
                .load(photoItem.url)
                .placeholder(R.drawable.progress_view)
                .fit()
                .centerCrop()
                .error(R.drawable.progress_view)
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

        }
    }

} //FeedAdapter
