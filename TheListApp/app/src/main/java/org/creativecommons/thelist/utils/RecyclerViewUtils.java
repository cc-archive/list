package org.creativecommons.thelist.utils;

public class RecyclerViewUtils {

    public interface cardSelectionListener {
        void onFlag(String photoID);
        void onLike(String photoID); //numberOfLikes?
        void onBookmark(String photoID);
        //TODO: void onMakerClick(String makerID);
        void onContribute(String itemID);

    }

} //RecyclerViewUtils
