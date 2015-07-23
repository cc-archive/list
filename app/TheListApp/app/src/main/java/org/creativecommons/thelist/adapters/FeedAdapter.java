package org.creativecommons.thelist.adapters;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.gc.materialdesign.utils.Utils;

import org.creativecommons.thelist.R;
import org.creativecommons.thelist.models.Photo;
import org.creativecommons.thelist.utils.RecyclerViewUtils;

import java.util.List;

public class FeedAdapter extends  RecyclerView.Adapter<FeedAdapter.FeedViewHolder> {

    private Activity mActivity;

    private LayoutInflater inflater;
    private List<Photo> photoItems;

    private RecyclerViewUtils.cardSelectionListener cardListener;

    public FeedAdapter (Activity activity, List<Photo> photoItems, RecyclerViewUtils.cardSelectionListener listener){
        this.photoItems = photoItems;
        this.mActivity = activity;
        this.cardListener = listener;

        inflater = LayoutInflater.from(activity);
    }


    @Override
    public FeedAdapter.FeedViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.feed_item, parent, false);

        FeedViewHolder holder = new FeedViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(FeedAdapter.FeedViewHolder holder, int position) {

        Photo photoitem = photoItems.get(position);

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder {

        //TODO: list variables

        public FeedViewHolder(View itemView) {
            super(itemView);

            //TODO: assign variables from view: itemView.findViewById


            //TODO: set on click listeners for each clickable item on the card

        }
    }

} //FeedAdapter
