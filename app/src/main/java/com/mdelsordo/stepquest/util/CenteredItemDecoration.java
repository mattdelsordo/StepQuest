package com.mdelsordo.stepquest.util;

import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
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


        outRect.left = mSpacing - column * mSpacing / mSpanCount;
        outRect.right = (column + 1) * mSpacing / mSpanCount;

        if(position < mSpanCount){
            outRect.top = mSpacing;
        }
        outRect.bottom = mSpacing;

    }

}
