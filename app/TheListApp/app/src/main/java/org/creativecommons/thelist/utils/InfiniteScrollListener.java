package org.creativecommons.thelist.utils;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

/**
 * Created by alex on 15-09-14.
 */
public abstract class InfiniteScrollListener extends RecyclerView.OnScrollListener {

    private int bufferItemCount = 2;
    private int previousItemCount = 0;
    private int currentPage = 1;
    private int firstVisibleItem, visibleItemCount, totalItemCount;
    private boolean isLoading = true;
    private LinearLayoutManager mLinearLayoutManager;

    public InfiniteScrollListener(LinearLayoutManager llm) {
        this.mLinearLayoutManager = llm;
    }

    public abstract void loadMore(int currentPage);

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);

        visibleItemCount = recyclerView.getChildCount();
        totalItemCount = mLinearLayoutManager.getItemCount();
        firstVisibleItem = mLinearLayoutManager.findFirstVisibleItemPosition();

        if (isLoading) {
            if (totalItemCount > previousItemCount) {
                isLoading = false;
                previousItemCount = totalItemCount;
            }
        }
        if (!isLoading && (totalItemCount - visibleItemCount)
                <= (firstVisibleItem + bufferItemCount)) {

            currentPage++;

            loadMore(currentPage);

            isLoading = true;
        }
    }
}