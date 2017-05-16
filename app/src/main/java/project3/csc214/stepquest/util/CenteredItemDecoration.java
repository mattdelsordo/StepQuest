package project3.csc214.stepquest.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

/**
 * Created by mdelsord on 5/15/17.
 * Added to a grid recyclerview to evenly space out its elements
 */

public class CenteredItemDecoration extends RecyclerView.ItemDecoration {
    private static final String TAG = "CenteredItemDecoration";
    private int mSpanCount;
    private int mSpacing;

    public CenteredItemDecoration(int spanCount, int spacing){
        mSpanCount = spanCount;
        mSpacing = spacing;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % mSpanCount;

        int parentWidth = parent.getWidth();
        int viewWidth = parent.getChildViewHolder(view).itemView.getMeasuredWidth(); //TODO: this is super broken
        int space = ((parentWidth/mSpanCount) - viewWidth)/2;
        Log.i(TAG, "Parent width: " + parentWidth);
        Log.i(TAG, "View width: " + viewWidth);
        Log.i(TAG, "Space: " + space);

        outRect.left = space;
        outRect.right = space;

        if(position < mSpanCount){
            outRect.top = mSpacing;
        }
        outRect.bottom = mSpacing;

    }

}
