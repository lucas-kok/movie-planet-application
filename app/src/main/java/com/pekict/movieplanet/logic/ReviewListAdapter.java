package com.pekict.movieplanet.logic;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pekict.movieplanet.R;
import com.pekict.movieplanet.domain.review.Review;
import com.squareup.picasso.Picasso;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.ReviewViewHolder> {
    private static final String TAG_NAME = ReviewListAdapter.class.getSimpleName();

    private final Review[] mReviews;
    private final LayoutInflater mInflater;

    static class ReviewViewHolder extends RecyclerView.ViewHolder {
        private final ImageView mAuthorAvatarImage;
        private final TextView mReviewAuthorText;
        private final TextView mReviewCreatedText;
        private final TextView mReviewContentText;

        final ReviewListAdapter mAdapter;

        public ReviewViewHolder(View itemView, ReviewListAdapter mAdapter) {
            super(itemView);

            mAuthorAvatarImage = itemView.findViewById(R.id.iv_author_avatar);
            mReviewAuthorText = itemView.findViewById(R.id.tv_review_author);
            mReviewCreatedText = itemView.findViewById(R.id.tv_review_created);
            mReviewContentText = itemView.findViewById(R.id.tv_review_content);

            this.mAdapter = mAdapter;
        }
    }

    public ReviewListAdapter(Context context, Review[] reviews) {
        mInflater = LayoutInflater.from(context);
        mReviews = reviews;
    }

    @NonNull
    @Override
    public ReviewViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View mItemView = mInflater.inflate(R.layout.review_item, parent, false);
        return new ReviewViewHolder(mItemView, this);
    }

    @Override
    public void onBindViewHolder(ReviewViewHolder holder, int position) {
        Review mCurrent = mReviews[position];
        Log.d(TAG_NAME, String.valueOf(position));

        // Setting the items UI elements to the Reviews values
        if (mCurrent.getAvatar_path != null) {
            Picasso.get().load(mCurrent.getAvatar_path).into(holder.mAuthorAvatarImage);
        } else {
            Picasso.get().load("https://i.pinimg.com/custom_covers/222x/85498161615209203_1636332751.jpg").into(holder.mAuthorAvatarImage);
        }

        holder.mReviewAuthorText.setText(mCurrent.getAuthor());
        holder.mReviewCreatedText.setText("Created at: " + mCurrent.getCreated_at());
        holder.mReviewContentText.setText(mCurrent.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.length;
    }
}
