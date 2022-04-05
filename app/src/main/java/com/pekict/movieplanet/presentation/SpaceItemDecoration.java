package com.pekict.movieplanet.presentation;

import android.graphics.Rect;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int mSpace;
    private final boolean mIsHorizontal;

    public SpaceItemDecoration(int space, boolean isHorizontal) {
        mSpace = space;
        mIsHorizontal = isHorizontal;
    }

    // Function to add spacing to the RecyclerView items
    @Override
    public void getItemOffsets(Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        outRect.bottom = mSpace;

        if (!mIsHorizontal) { return; }

        outRect.right = mSpace;
    }
}
